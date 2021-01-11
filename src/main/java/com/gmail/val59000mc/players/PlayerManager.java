package com.gmail.val59000mc.players;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.customitems.GameItem;
import com.gmail.val59000mc.customitems.KitsManager;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.events.*;
import com.gmail.val59000mc.exceptions.UhcPlayerDoesNotExistException;
import com.gmail.val59000mc.exceptions.UhcPlayerJoinException;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.exceptions.UhcTeamException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.game.handlers.CustomEventHandler;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioManager;
import com.gmail.val59000mc.scenarios.scenariolisteners.SilentNightListener;
import com.gmail.val59000mc.scenarios.scenariolisteners.TeamInventoryListener;
import com.gmail.val59000mc.threads.CheckRemainingPlayerThread;
import com.gmail.val59000mc.threads.TeleportPlayersThread;
import com.gmail.val59000mc.threads.TimeBeforeSendBungeeThread;
import com.gmail.val59000mc.utils.*;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerManager {

	private final CustomEventHandler customEventHandler;
	private final List<UhcPlayer> players;
	private long lastDeathTime;

	public PlayerManager(CustomEventHandler customEventHandler) {
		this.customEventHandler = customEventHandler;
		players = Collections.synchronizedList(new ArrayList<>());
	}

	public void setLastDeathTime() {
		lastDeathTime = System.currentTimeMillis();
	}

	public boolean isPlayerAllowedToJoin(Player player) throws UhcPlayerJoinException {
		GameManager gm = GameManager.getGameManager();
		UhcPlayer uhcPlayer;

		switch(gm.getGameState()){
			case LOADING:
				throw new UhcPlayerJoinException(Lang.KICK_LOADING);

			case WAITING:
				return true;

			case STARTING:
				if (doesPlayerExist(player)){
					uhcPlayer = getUhcPlayer(player);
					if(uhcPlayer.getState().equals(PlayerState.PLAYING)){
						return true;
					}else{
						throw new UhcPlayerJoinException(Lang.KICK_STARTING);
					}
				}else{
					throw new UhcPlayerJoinException(Lang.KICK_STARTING);
				}
			case DEATHMATCH:
			case PLAYING:
				if (doesPlayerExist(player)){
					uhcPlayer = getUhcPlayer(player);

					boolean canSpectate = gm.getConfig().get(MainConfig.CAN_SPECTATE_AFTER_DEATH);
					if(
							uhcPlayer.getState().equals(PlayerState.PLAYING) ||
							((canSpectate || player.hasPermission("uhc-core.spectate.override")) && uhcPlayer.getState().equals(PlayerState.DEAD))
					){
						return true;
					}else{
						throw new UhcPlayerJoinException(Lang.KICK_PLAYING);
					}
				}else{
					if(player.hasPermission("uhc-core.join-override")
							|| player.hasPermission("uhc-core.spectate.override")
							|| gm.getConfig().get(MainConfig.CAN_JOIN_AS_SPECTATOR) && gm.getConfig().get(MainConfig.CAN_SPECTATE_AFTER_DEATH)){
						UhcPlayer spectator = newUhcPlayer(player);
						spectator.setState(PlayerState.DEAD);
						return true;
					}
					throw new UhcPlayerJoinException(Lang.KICK_PLAYING);
				}

			case ENDED:
				if(player.hasPermission("uhc-core.join-override")){
					return true;
				}
				throw new UhcPlayerJoinException(Lang.KICK_ENDED);

		}
		return false;
	}

	/**
	 * This method is used to get the UhcPlayer object from Bukkit Player.
	 * When using this method in the PlayerJoinEvent please check the doesPlayerExist(Player) to see if the player has a matching UhcPlayer.
	 * @param player The Bukkit player you want the UhcPlayer from.
	 * @return Returns a UhcPlayer.
	 */
	public UhcPlayer getUhcPlayer(Player player){
		try {
			return getUhcPlayer(player.getUniqueId());
		}catch (UhcPlayerDoesNotExistException ex){
			throw new RuntimeException(ex);
		}
	}

	public boolean doesPlayerExist(Player player){
		try {
			getUhcPlayer(player.getUniqueId());
			return true;
		}catch (UhcPlayerDoesNotExistException ex){
			return false;
		}
	}

	public UhcPlayer getUhcPlayer(String name) throws UhcPlayerDoesNotExistException {
		for(UhcPlayer uhcPlayer : getPlayersList()){
			if(uhcPlayer.getName().equals(name)) {
				return uhcPlayer;
			}
		}
		throw new UhcPlayerDoesNotExistException(name);
	}

	public UhcPlayer getUhcPlayer(UUID uuid) throws UhcPlayerDoesNotExistException {
		for(UhcPlayer uhcPlayer : getPlayersList()){
			if(uhcPlayer.getUuid().equals(uuid)) {
				return uhcPlayer;
			}
		}
		throw new UhcPlayerDoesNotExistException(uuid.toString());
	}

	public UhcPlayer getOrCreateUhcPlayer(Player player){
		if (doesPlayerExist(player)){
			return getUhcPlayer(player);
		}else{
			return newUhcPlayer(player);
		}
	}

    public synchronized UhcPlayer newUhcPlayer(Player bukkitPlayer){
        return newUhcPlayer(bukkitPlayer.getUniqueId(), bukkitPlayer.getName());
    }

    public synchronized UhcPlayer newUhcPlayer(UUID uuid, String name){
        UhcPlayer newPlayer = new UhcPlayer(uuid, name);
        getPlayersList().add(newPlayer);
        return newPlayer;
    }

	public synchronized List<UhcPlayer> getPlayersList(){
		return players;
	}

	public Set<UhcPlayer> getOnlinePlayingPlayers() {
		return players.stream()
				.filter(UhcPlayer::isPlaying)
				.filter(UhcPlayer::isOnline)
				.collect(Collectors.toSet());
	}

	public Set<UhcPlayer> getAllPlayingPlayers() {
		return players.stream()
				.filter(UhcPlayer::isPlaying)
				.collect(Collectors.toSet());
	}

	public void playerJoinsTheGame(Player player){
		UhcPlayer uhcPlayer;

		if (doesPlayerExist(player)){
			uhcPlayer = getUhcPlayer(player);
		}else{
			uhcPlayer = newUhcPlayer(player);
			Bukkit.getLogger().warning("[UhcCore] None existent player joined!");
		}

		GameManager gm = GameManager.getGameManager();
		gm.getScoreboardManager().setUpPlayerScoreboard(uhcPlayer);

		switch(uhcPlayer.getState()){
			case WAITING:
				setPlayerWaitsAtLobby(uhcPlayer);

				if(gm.getConfig().get(MainConfig.AUTO_ASSIGN_PLAYER_TO_TEAM)){
					autoAssignPlayerToTeam(uhcPlayer);
				}
				uhcPlayer.sendPrefixedMessage(Lang.PLAYERS_WELCOME_NEW);
				break;
			case PLAYING:
				setPlayerStartPlaying(uhcPlayer);

				if(!uhcPlayer.getHasBeenTeleportedToLocation()){
					List<UhcPlayer> onlinePlayingMembers = uhcPlayer.getTeam().getOnlinePlayingMembers();

					// Only player in team so create random spawn location.
					if(onlinePlayingMembers.size() <= 1){
						World world = gm.getMapLoader().getUhcWorld(World.Environment.NORMAL);
						double maxDistance = 0.9 *  gm.getMapLoader().getBorderSize();
						uhcPlayer.getTeam().setStartingLocation(LocationUtils.findRandomSafeLocation(world, maxDistance));
					}
					// Set spawn location at team mate.
					else{
						UhcPlayer teamMate = onlinePlayingMembers.get(0);
						if (teamMate == uhcPlayer){
							teamMate = onlinePlayingMembers.get(1);
						}

						try{
							uhcPlayer.getTeam().setStartingLocation(teamMate.getPlayer().getLocation());
						}catch (UhcPlayerNotOnlineException ex){
							ex.printStackTrace();
						}
					}

					// Apply start potion effect.
					for(PotionEffect effect : GameManager.getGameManager().getConfig().get(MainConfig.POTION_EFFECT_ON_START)){
						player.addPotionEffect(effect);
					}

					// Teleport player
					player.teleport(uhcPlayer.getStartingLocation());
					uhcPlayer.setHasBeenTeleportedToLocation(true);

					// Remove lobby potion effects.
					player.removePotionEffect(PotionEffectType.BLINDNESS);

					// Call event
					Bukkit.getPluginManager().callEvent(new PlayerStartsPlayingEvent(uhcPlayer));
				}
				if (uhcPlayer.getOfflineZombie() != null){
					Optional<LivingEntity> zombie = player.getWorld().getLivingEntities()
							.stream()
							.filter(e -> e.getUniqueId().equals(uhcPlayer.getOfflineZombie()))
							.findFirst();

					zombie.ifPresent(Entity::remove);
					uhcPlayer.setOfflineZombie(null);
				}
				uhcPlayer.sendPrefixedMessage(Lang.PLAYERS_WELCOME_BACK_IN_GAME);
				break;
			case DEAD:
				setPlayerSpectateAtLobby(uhcPlayer);
				break;
		}
	}

	private void autoAssignPlayerToTeam(UhcPlayer uhcPlayer) {
		GameManager gm = GameManager.getGameManager();

		if (gm.getScenarioManager().isEnabled(Scenario.LOVE_AT_FIRST_SIGHT)){
			return;
		}

		for(UhcTeam team : listUhcTeams()){
			// Don't assign player to spectating team.
			if (team.isSpectating()){
				continue;
			}

			if(team != uhcPlayer.getTeam() && team.getMembers().size() < gm.getConfig().get(MainConfig.MAX_PLAYERS_PER_TEAM)){
				try {
					team.join(uhcPlayer);
				} catch (UhcTeamException ignored) {
				}
				break;
			}
		}
	}

	public void setPlayerWaitsAtLobby(UhcPlayer uhcPlayer){
		uhcPlayer.setState(PlayerState.WAITING);
		GameManager gm = GameManager.getGameManager();
		Player player;
		try {
			player = uhcPlayer.getPlayer();
			player.teleport(gm.getMapLoader().getLobby().getLocation());
			clearPlayerInventory(player);
			player.setGameMode(GameMode.ADVENTURE);
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999999, 0), false);
			player.setHealth(20);
			player.setExhaustion(20);
			player.setFoodLevel(20);
			player.setExp(0);

			UhcItems.giveLobbyItemsTo(player);
		} catch (UhcPlayerNotOnlineException e) {
			// Do nothing because WAITING is a safe state
		}

	}

	public void setPlayerStartPlaying(UhcPlayer uhcPlayer){

		Player player;
		MainConfig cfg = GameManager.getGameManager().getConfig();

		if(!uhcPlayer.getHasBeenTeleportedToLocation()){
			uhcPlayer.setState(PlayerState.PLAYING);
			uhcPlayer.selectDefaultGlobalChat();

			try {
				player = uhcPlayer.getPlayer();
				clearPlayerInventory(player);
				player.setFireTicks(0);

				for(PotionEffect effect : player.getActivePotionEffects())
				{
					player.removePotionEffect(effect.getType());
				}
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 1), false);
				player.setGameMode(GameMode.SURVIVAL);
				if(cfg.get(MainConfig.ENABLE_EXTRA_HALF_HEARTS)){
					VersionUtils.getVersionUtils().setPlayerMaxHealth(player, 20+((double) cfg.get(MainConfig.EXTRA_HALF_HEARTS)));
					player.setHealth(20+((double) cfg.get(MainConfig.EXTRA_HALF_HEARTS)));
				}
				UhcItems.giveGameItemTo(player, GameItem.COMPASS_ITEM);
				UhcItems.giveGameItemTo(player, GameItem.CUSTOM_CRAFT_BOOK);
				KitsManager.giveKitTo(player);

				if (!uhcPlayer.getStoredItems().isEmpty()){
					uhcPlayer.getStoredItems().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
					uhcPlayer.getStoredItems().clear();
				}
			} catch (UhcPlayerNotOnlineException e) {
				// Nothing done
			}
		}
	}

	private void clearPlayerInventory(Player player) {
		player.getInventory().clear();

		//clear player armor
		ItemStack[] emptyArmor = new ItemStack[4];
		for(int i=0 ; i<emptyArmor.length ; i++){
			emptyArmor[i] = new ItemStack(Material.AIR);
		}
		player.getInventory().setArmorContents(emptyArmor);

	}

	public void setPlayerSpectateAtLobby(UhcPlayer uhcPlayer){
		GameManager gm = GameManager.getGameManager();

		uhcPlayer.setState(PlayerState.DEAD);
		uhcPlayer.sendPrefixedMessage(Lang.PLAYERS_WELCOME_BACK_SPECTATING);

		if(gm.getConfig().get(MainConfig.SPECTATING_TELEPORT)) {
			uhcPlayer.sendPrefixedMessage(Lang.COMMAND_SPECTATING_HELP);
		}

		Player player;
		try {
			player = uhcPlayer.getPlayer();player.getEquipment().clear();
			clearPlayerInventory(player);
			player.setGameMode(GameMode.SPECTATOR);
			for(PotionEffect effect : player.getActivePotionEffects())
			{
				player.removePotionEffect(effect.getType());
			}
			if(gm.getGameState().equals(GameState.DEATHMATCH)){
				player.teleport(gm.getMapLoader().getArena().getLocation());
			}else{
				player.teleport(gm.getMapLoader().getLobby().getLocation());
			}
		} catch (UhcPlayerNotOnlineException e) {
			// Do nothing because DEAD is a safe state
		}
	}

	public void setAllPlayersEndGame() {
		GameManager gm = GameManager.getGameManager();
		MainConfig cfg = gm.getConfig();

		List<UhcPlayer> winners = getWinners();

		if (!winners.isEmpty()) {
			UhcPlayer player1 = winners.get(0);
			if (winners.size() == 1) {
				gm.broadcastInfoMessage(Lang.PLAYERS_WON_SOLO.replace("%player%", player1.getDisplayName()));
			} else {
				gm.broadcastInfoMessage(Lang.PLAYERS_WON_TEAM.replace("%team%", player1.getTeam().getTeamName()));
			}
		}

		// send to bungee
		if(cfg.get(MainConfig.ENABLE_BUNGEE_SUPPORT) && cfg.get(MainConfig.TIME_BEFORE_SEND_BUNGEE_AFTER_END) >= 0){
			for(UhcPlayer player : getPlayersList()){
				Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new TimeBeforeSendBungeeThread(this, player, cfg.get(MainConfig.TIME_BEFORE_SEND_BUNGEE_AFTER_END)));
			}
		}

		UhcWinEvent event = new UhcWinEvent(new HashSet<>(winners));
		Bukkit.getServer().getPluginManager().callEvent(event);

		customEventHandler.handleWinEvent(new HashSet<>(winners));

		// When the game finished set all player states to DEAD
		getPlayersList().forEach(player -> player.setState(PlayerState.DEAD));
	}

	private List<UhcPlayer> getWinners(){
		List<UhcPlayer> winners = new ArrayList<>();
		for(UhcPlayer player : getPlayersList()){
			try{
				Player connected = player.getPlayer();
				if(connected.isOnline() && player.getState().equals(PlayerState.PLAYING))
					winners.add(player);
			}catch(UhcPlayerNotOnlineException e){
				// not adding the player to winner list
			}
		}
		return winners;
	}

	public List<UhcTeam> listUhcTeams(){
		List<UhcTeam> teams = new ArrayList<>();
		for(UhcPlayer player : getPlayersList()){
			UhcTeam team = player.getTeam();
			if(!teams.contains(team))
				teams.add(team);
		}
		return teams;
	}

	public void randomTeleportTeams() {
		GameManager gm = GameManager.getGameManager();
		World world = gm.getMapLoader().getUhcWorld(World.Environment.NORMAL);
		double maxDistance = 0.9 * gm.getConfig().get(MainConfig.BORDER_START_SIZE);

		// Fore solo players to join teams
		if(gm.getConfig().get(MainConfig.FORCE_ASSIGN_SOLO_PLAYER_TO_TEAM_WHEN_STARTING)){
			for(UhcPlayer uhcPlayer : getPlayersList()){
				// If player is spectating don't assign player.
				if (uhcPlayer.getState() == PlayerState.DEAD){
					continue;
				}

				if(uhcPlayer.getTeam().getMembers().size() == 1){
					autoAssignPlayerToTeam(uhcPlayer);
				}
			}
		}

		for(UhcTeam team : listUhcTeams()){
			Location newLoc = LocationUtils.findRandomSafeLocation(world, maxDistance);
			team.setStartingLocation(newLoc);
		}

		Bukkit.getPluginManager().callEvent(new UhcPreTeleportEvent());

		long delayTeleportByTeam = 0;

		for(UhcTeam team : listUhcTeams()){

			if (team.isSpectating()){
				gm.getPlayerManager().setPlayerSpectateAtLobby(team.getLeader());
				continue;
			}

			for(UhcPlayer uhcPlayer : team.getMembers()){
				gm.getPlayerManager().setPlayerStartPlaying(uhcPlayer);
			}

			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), new TeleportPlayersThread(GameManager.getGameManager(), team), delayTeleportByTeam);
			Bukkit.getLogger().info("[UhcCore] Teleporting a team in "+delayTeleportByTeam+" ticks");
			delayTeleportByTeam += 10; // ticks
		}

		Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), () -> GameManager.getGameManager().startWatchingEndOfGame(), delayTeleportByTeam + 20);

	}

	public void strikeLightning(UhcPlayer uhcPlayer) {
		try{
			Location loc = uhcPlayer.getPlayer().getLocation();
			loc.getWorld().strikeLightningEffect(loc);
			loc.getWorld().getBlockAt(loc).setType(Material.AIR);
		}catch(UhcPlayerNotOnlineException e){
			Location loc = new Location(GameManager.getGameManager().getMapLoader().getUhcWorld(World.Environment.NORMAL),0, 200,0);
			loc.getWorld().strikeLightningEffect(loc);
			loc.getWorld().getBlockAt(loc).setType(Material.AIR);
		}

		// Extinguish fire
	}

	public void playSoundToAll(UniversalSound sound) {
		for(UhcPlayer player : getPlayersList()){
			playsoundTo(player, sound);
		}
	}

	public void playSoundToAll(UniversalSound sound, float v, float v1){
		for(UhcPlayer player : getPlayersList()){
			playsoundTo(player, sound,v,v1);
		}
	}

	public void playsoundTo(UhcPlayer player, UniversalSound sound) {
		playsoundTo(player,sound,1,1);
	}

	public void playsoundTo(UhcPlayer player, UniversalSound sound, float v, float v1) {
		try {
			Player p = player.getPlayer();
			p.playSound(p.getLocation(), sound.getSound(), v, v1);
		} catch (UhcPlayerNotOnlineException e) {
			// No sound played
		}
	}

	public void checkIfRemainingPlayers(){
		int playingPlayers = 0;
		int playingPlayersOnline = 0;
		int playingTeams = 0;
		int playingTeamsOnline = 0;

		for(UhcTeam team : listUhcTeams()){

			int teamIsOnline = 0;
			int teamIsPlaying = 0;

			for(UhcPlayer player : team.getMembers()){
				if(player.getState().equals(PlayerState.PLAYING)){
					playingPlayers++;
					teamIsPlaying = 1;
					try{
						player.getPlayer();
						playingPlayersOnline++;
						teamIsOnline = 1;
					}catch(UhcPlayerNotOnlineException e){
						// Player isn't online
					}
				}
			}

			playingTeamsOnline += teamIsOnline;
			playingTeams += teamIsPlaying;
		}

		GameManager gm = GameManager.getGameManager();
		MainConfig cfg = gm.getConfig();
		if(playingPlayers == 0){
			gm.endGame();
		}
		else if(
				gm.getGameState() == GameState.DEATHMATCH &&
				cfg.get(MainConfig.ENABLE_DEATHMATCH_FORCE_END) &&
				gm.getPvp() &&
				(lastDeathTime+(cfg.get(MainConfig.DEATHMATCH_FORCE_END_DELAY)*TimeUtils.SECOND)) < System.currentTimeMillis()
		){
			gm.endGame();
		}
		else if(playingPlayers>0 && playingPlayersOnline == 0){
			// Check if all playing players have left the game
			if(cfg.get(MainConfig.END_GAME_WHEN_ALL_PLAYERS_HAVE_LEFT)){
				gm.startEndGameThread();
			}
		}
		else if(playingPlayers>0 && playingPlayersOnline > 0 && playingTeamsOnline == 1 && playingTeams == 1 && !cfg.get(MainConfig.ONE_PLAYER_MODE)){
			// Check if one playing team remains
			gm.endGame();
		}
		else if(playingPlayers>0 && playingPlayersOnline > 0 && playingTeamsOnline == 1 && playingTeams > 1){
			// Check if one playing team remains
			if(cfg.get(MainConfig.END_GAME_WHEN_ALL_PLAYERS_HAVE_LEFT) && !cfg.get(MainConfig.ONE_PLAYER_MODE)){
				gm.startEndGameThread();
			}
		}
		else if(gm.getGameIsEnding()){
			gm.stopEndGameThread();
		}
	}

	public void startWatchPlayerPlayingThread() {
		for(Player player : Bukkit.getOnlinePlayers()){
			player.removePotionEffect(PotionEffectType.BLINDNESS);
		}

		// Unfreeze players
		for (UhcPlayer uhcPlayer : getPlayersList()){
			uhcPlayer.releasePlayer();
			Bukkit.getPluginManager().callEvent(new PlayerStartsPlayingEvent(uhcPlayer));
		}

		Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), new CheckRemainingPlayerThread(GameManager.getGameManager()) , 40);
	}

	public void sendPlayerToBungeeServer(Player player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(GameManager.getGameManager().getConfig().get(MainConfig.SERVER_BUNGEE));
		player.sendMessage(Lang.PLAYERS_SEND_BUNGEE_NOW);
		player.sendPluginMessage(UhcCore.getPlugin(), "BungeeCord", out.toByteArray());
	}

	public void killOfflineUhcPlayer(UhcPlayer uhcPlayer, Set<ItemStack> playerDrops){
		killOfflineUhcPlayer(uhcPlayer, null, playerDrops, null);
	}

	public void killOfflineUhcPlayer(UhcPlayer uhcPlayer, @Nullable Location location, Set<ItemStack> playerDrops, @Nullable Player killer){
		GameManager gm = GameManager.getGameManager();
		PlayerManager pm = gm.getPlayerManager();
		ScenarioManager sm = gm.getScenarioManager();
		MainConfig cfg = gm.getConfig();

		if (uhcPlayer.getState() != PlayerState.PLAYING){
			Bukkit.getLogger().warning("[UhcCore] " + uhcPlayer.getName() + " died while already in 'DEAD' mode!");
			return;
		}

		// kill event
		if(killer != null){
			UhcPlayer uhcKiller = pm.getUhcPlayer(killer);

			uhcKiller.addKill();

			// Call Bukkit event
			UhcPlayerKillEvent killEvent = new UhcPlayerKillEvent(uhcKiller, uhcPlayer);
			Bukkit.getServer().getPluginManager().callEvent(killEvent);

			customEventHandler.handleKillEvent(killer, uhcKiller);
		}

		// Drop the team inventory if the last player on a team was killed
		if (sm.isEnabled(Scenario.TEAM_INVENTORY))
		{
			UhcTeam team = uhcPlayer.getTeam();
			if (team.getPlayingMemberCount() == 1)
			{
				((TeamInventoryListener) sm.getScenarioListener(Scenario.TEAM_INVENTORY)).dropTeamInventory(team, location);
			}
		}

		// Store drops in case player gets re-spawned.
		uhcPlayer.getStoredItems().clear();
		uhcPlayer.getStoredItems().addAll(playerDrops);

		// eliminations
		if (!sm.isEnabled(Scenario.SILENT_NIGHT) || !((SilentNightListener) sm.getScenarioListener(Scenario.SILENT_NIGHT)).isNightMode()) {
			gm.broadcastInfoMessage(Lang.PLAYERS_ELIMINATED.replace("%player%", uhcPlayer.getName()));
		}

		if(cfg.get(MainConfig.REGEN_HEAD_DROP_ON_PLAYER_DEATH)){
			playerDrops.add(UhcItems.createRegenHead(uhcPlayer));
		}

		if(location != null && cfg.get(MainConfig.ENABLE_GOLDEN_HEADS)){
			if (cfg.get(MainConfig.PLACE_HEAD_ON_FENCE) && !gm.getScenarioManager().isEnabled(Scenario.TIMEBOMB)){
				// place head on fence
				Location loc = location.clone().add(1,0,0);
				loc.getBlock().setType(UniversalMaterial.OAK_FENCE.getType());
				loc.add(0, 1, 0);
				loc.getBlock().setType(UniversalMaterial.PLAYER_HEAD_BLOCK.getType());

				Skull skull = (Skull) loc.getBlock().getState();
				VersionUtils.getVersionUtils().setSkullOwner(skull, uhcPlayer);
				skull.setRotation(BlockFace.NORTH);
				skull.update();
			}else{
				playerDrops.add(UhcItems.createGoldenHeadPlayerSkull(uhcPlayer.getName(), uhcPlayer.getUuid()));
			}
		}

		if(location != null && cfg.get(MainConfig.ENABLE_EXP_DROP_ON_DEATH)){
			UhcItems.spawnExtraXp(location, cfg.get(MainConfig.EXP_DROP_ON_DEATH));
		}

		if (location != null){
			playerDrops.forEach(item -> location.getWorld().dropItem(location, item));
		}

		uhcPlayer.setState(PlayerState.DEAD);
		pm.strikeLightning(uhcPlayer);
		playSoundToAll(UniversalSound.WITHER_SPAWN);

		pm.checkIfRemainingPlayers();
	}

	public void spawnOfflineZombieFor(Player player){
		UhcPlayer uhcPlayer = getUhcPlayer(player);

		Zombie zombie = (Zombie) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
		zombie.setCustomName(uhcPlayer.getDisplayName());
		zombie.setCustomNameVisible(true);
		// 1.8 doesn't have setAI method so use VersionUtils.
		VersionUtils.getVersionUtils().setEntityAI(zombie, false);
		zombie.setBaby(false);
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999999, 1, true, true));

		EntityEquipment equipment = zombie.getEquipment();
		equipment.setHelmet(VersionUtils.getVersionUtils().createPlayerSkull(player.getName(), player.getUniqueId()));
		equipment.setChestplate(player.getInventory().getChestplate());
		equipment.setLeggings(player.getInventory().getLeggings());
		equipment.setBoots(player.getInventory().getBoots());
		equipment.setItemInHand(player.getItemInHand());

		uhcPlayer.getStoredItems().clear();
		for (ItemStack item : player.getInventory().getContents()){
			if (item != null){
				uhcPlayer.getStoredItems().add(item);
			}
		}

		uhcPlayer.setOfflineZombie(zombie.getUniqueId());
	}

	public UhcPlayer revivePlayer(UUID uuid, String name, boolean spawnWithItems){
		UhcPlayer uhcPlayer;

		try{
			uhcPlayer = getUhcPlayer(uuid);
		}catch (UhcPlayerDoesNotExistException ex){
			uhcPlayer = newUhcPlayer(uuid, name);
		}

		revivePlayer(uhcPlayer, spawnWithItems);
		return uhcPlayer;
	}

	public void revivePlayer(UhcPlayer uhcPlayer, boolean spawnWithItems){
		uhcPlayer.setHasBeenTeleportedToLocation(false);
		uhcPlayer.setState(PlayerState.PLAYING);

		// If not respawn with items, clear stored items.
		if (!spawnWithItems){
			uhcPlayer.getStoredItems().clear();
		}

		try{
			playerJoinsTheGame(uhcPlayer.getPlayer());
		}catch (UhcPlayerNotOnlineException ex){
			// Player gets revived next time they attempt to join.
		}
	}

}