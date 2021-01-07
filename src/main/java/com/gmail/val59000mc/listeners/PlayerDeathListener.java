package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.configuration.VaultManager;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.events.UhcPlayerKillEvent;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioManager;
import com.gmail.val59000mc.scenarios.scenariolisteners.SilentNightListener;
import com.gmail.val59000mc.scenarios.scenariolisteners.TeamInventoryListener;
import com.gmail.val59000mc.threads.TimeBeforeSendBungeeThread;
import com.gmail.val59000mc.utils.UniversalMaterial;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

public class PlayerDeathListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		GameManager gm = GameManager.getGameManager();
		PlayersManager pm = gm.getPlayersManager();
		ScenarioManager sm = gm.getScenarioManager();
		MainConfig cfg = gm.getConfig();
		UhcPlayer uhcPlayer = pm.getUhcPlayer(player);

		if (uhcPlayer.getState() != PlayerState.PLAYING){
			Bukkit.getLogger().warning("[UhcCore] " + player.getName() + " died while already in 'DEAD' mode!");
			player.kickPlayer("Don't cheat!");
			return;
		}

		pm.setLastDeathTime();

		// kill event
		Player killer = player.getKiller();
		if(killer != null){
			UhcPlayer uhcKiller = pm.getUhcPlayer(killer);

			uhcKiller.addKill();

			// Call Bukkit event
			UhcPlayerKillEvent killEvent = new UhcPlayerKillEvent(uhcPlayer, uhcKiller);
			Bukkit.getServer().getPluginManager().callEvent(killEvent);

			if(cfg.get(MainConfig.ENABLE_KILL_EVENT)){
				double reward = cfg.get(MainConfig.REWARD_KILL_EVENT);
				List<String> killCommands = cfg.get(MainConfig.KILL_COMMANDS);
				if (reward > 0) {
					VaultManager.addMoney(killer, reward);
					if (!Lang.EVENT_KILL_REWARD.isEmpty()) {
						killer.sendMessage(Lang.EVENT_KILL_REWARD.replace("%money%", "" + reward));
					}
				}
				// If the list is empty, this will never execute
				killCommands.forEach(cmd -> {
					if (cmd.startsWith("/")){
						cmd = cmd.substring(1);
					}

					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%name%", uhcKiller.getRealName()));
					} catch (CommandException exception){
						Bukkit.getLogger().warning("[UhcCore] Failed to execute kill reward command: " + cmd);
						exception.printStackTrace();
					}
				});
			}
		}

		// Drop the team inventory if the last player on a team was killed
		if (sm.isActivated(Scenario.TEAMINVENTORY))
		{
			UhcTeam team = uhcPlayer.getTeam();
			if (team.getPlayingMemberCount() == 1)
			{
				((TeamInventoryListener) sm.getScenarioListener(Scenario.TEAMINVENTORY)).dropTeamInventory(team, player.getLocation());
			}
		}

		// Store drops in case player gets re-spawned.
		uhcPlayer.getStoredItems().clear();
		uhcPlayer.getStoredItems().addAll(event.getDrops());

		// eliminations
		if (!sm.isActivated(Scenario.SILENTNIGHT) || !((SilentNightListener) sm.getScenarioListener(Scenario.SILENTNIGHT)).isNightMode()) {
			gm.broadcastInfoMessage(Lang.PLAYERS_ELIMINATED.replace("%player%", player.getName()));
		}

		if(cfg.get(MainConfig.REGEN_HEAD_DROP_ON_PLAYER_DEATH)){
			event.getDrops().add(UhcItems.createRegenHead(uhcPlayer));
		}

		if(cfg.get(MainConfig.ENABLE_GOLDEN_HEADS)){
			if (cfg.get(MainConfig.PLACE_HEAD_ON_FENCE) && !gm.getScenarioManager().isActivated(Scenario.TIMEBOMB)){
				// place head on fence
				Location loc = player.getLocation().clone().add(1,0,0);
				loc.getBlock().setType(UniversalMaterial.OAK_FENCE.getType());
				loc.add(0, 1, 0);
				loc.getBlock().setType(UniversalMaterial.PLAYER_HEAD_BLOCK.getType());

				Skull skull = (Skull) loc.getBlock().getState();
				VersionUtils.getVersionUtils().setSkullOwner(skull, uhcPlayer);
				skull.setRotation(BlockFace.NORTH);
				skull.update();
			}else{
				event.getDrops().add(UhcItems.createGoldenHeadPlayerSkull(player.getName(), player.getUniqueId()));
			}
		}

		if(cfg.get(MainConfig.ENABLE_EXP_DROP_ON_DEATH)){
			UhcItems.spawnExtraXp(player.getLocation(), cfg.get(MainConfig.EXP_DROP_ON_DEATH));
		}

		uhcPlayer.setState(PlayerState.DEAD);
		pm.strikeLightning(uhcPlayer);
		pm.playSoundPlayerDeath();

		// handle player leaving the server
		boolean canContinueToSpectate = player.hasPermission("uhc-core.spectate.override")
				|| cfg.get(MainConfig.CAN_SPECTATE_AFTER_DEATH);

		if (!canContinueToSpectate) {
			if (cfg.get(MainConfig.ENABLE_BUNGEE_SUPPORT)) {
				Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new TimeBeforeSendBungeeThread(pm, uhcPlayer, cfg.get(MainConfig.TIME_BEFORE_SEND_BUNGEE_AFTER_DEATH)));
			} else {
				player.kickPlayer(Lang.DISPLAY_MESSAGE_PREFIX + " " + Lang.KICK_DEAD);
			}
		}

		pm.checkIfRemainingPlayers();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event){
		PlayersManager pm = GameManager.getGameManager().getPlayersManager();
		UhcPlayer uhcPlayer = pm.getUhcPlayer(event.getPlayer());

		if(uhcPlayer.getState().equals(PlayerState.DEAD)){
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), () -> pm.setPlayerSpectateAtLobby(uhcPlayer), 1);
		}
	}
	
}