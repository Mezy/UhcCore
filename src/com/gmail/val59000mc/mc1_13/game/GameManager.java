package com.gmail.val59000mc.mc1_13.game;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.mc1_13.commands.ChatCommandExecutor;
import com.gmail.val59000mc.mc1_13.commands.TeleportCommandExecutor;
import com.gmail.val59000mc.mc1_13.commands.UhcCommandExecutor;
import com.gmail.val59000mc.mc1_13.configuration.MainConfiguration;
import com.gmail.val59000mc.mc1_13.customitems.CraftsManager;
import com.gmail.val59000mc.mc1_13.customitems.KitsManager;
import com.gmail.val59000mc.mc1_13.languages.Lang;
import com.gmail.val59000mc.mc1_13.maploader.MapLoader;
import com.gmail.val59000mc.mc1_13.players.PlayersManager;
import com.gmail.val59000mc.mc1_13.players.TeamManager;
import com.gmail.val59000mc.mc1_13.players.UhcPlayer;
import com.gmail.val59000mc.mc1_13.schematics.DeathmatchArena;
import com.gmail.val59000mc.mc1_13.schematics.Lobby;
import com.gmail.val59000mc.mc1_13.schematics.UndergroundNether;
import com.gmail.val59000mc.mc1_13.scoreboard.ScoreboardManager;
import com.gmail.val59000mc.mc1_13.utils.NMSUtils;
import com.gmail.val59000mc.mc1_13.utils.TimeUtils;
import com.gmail.val59000mc.mc1_13.listeners.*;
import com.gmail.val59000mc.mc1_13.threads.*;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
	private GameState gameState;
	private Lobby lobby;
	private DeathmatchArena arena;
	private PlayersManager playerManager;
	private TeamManager teamManager;
	private MapLoader mapLoader;
	private UhcWorldBorder worldBorder;
	private ScoreboardManager scoreboardManager;
	private boolean pvp;
	private boolean gameIsEnding;
	private int episodeNumber = 0;

	private long remainingTime;
	private long elapsedTime = 0;

	private MainConfiguration configuration;

	private static GameManager uhcGM = null;

	public GameManager() {
		uhcGM = this;
		scoreboardManager = new ScoreboardManager();
	}


	public MainConfiguration getConfiguration() {
		return configuration;
	}

	public UhcWorldBorder getWorldBorder() {
		return worldBorder;
	}

	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

	public static GameManager getGameManager(){
		return uhcGM;
	}

	public synchronized GameState getGameState(){
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;

		// Update MOTD
		switch(gameState){
			case ENDED:
				setMotd(Lang.DISPLAY_MOTD_ENDED);
				break;
			case LOADING:
				setMotd(Lang.DISPLAY_MOTD_LOADING);
				break;
			case DEATHMATCH:
				setMotd(Lang.DISPLAY_MOTD_PLAYING);
				break;
			case PLAYING:
				setMotd(Lang.DISPLAY_MOTD_PLAYING);
				break;
			case STARTING:
				setMotd(Lang.DISPLAY_MOTD_STARTING);
				break;
			case WAITING:
				setMotd(Lang.DISPLAY_MOTD_WAITING);
				break;
			default:
				setMotd(Lang.DISPLAY_MOTD_ENDED);
				break;
		}
	}

	private void setMotd(String motd){
		try {
			Class craftServerClass = NMSUtils.getNMSClass("CraftServer");
			Object craftServer = craftServerClass.cast(Bukkit.getServer());
			Object dedicatedPlayerList = NMSUtils.getHandle(craftServer);
			Object dedicatedServer = NMSUtils.getServer(dedicatedPlayerList);

			Method setMotd = NMSUtils.getMethod(dedicatedServer.getClass(), "setMotd");
			setMotd.invoke(dedicatedServer, motd);
		}catch (InvocationTargetException | IllegalAccessException | NullPointerException ex){
			ex.printStackTrace();
		}
	}

	public PlayersManager getPlayersManager(){
		return playerManager;
	}

	public TeamManager getTeamManager() {
		return teamManager;
	}

	public MapLoader getMapLoader(){
		return mapLoader;
	}

	public Lobby getLobby() {
		return lobby;
	}

	public DeathmatchArena getArena() {
		return arena;
	}

	public boolean getGameIsEnding() {
		return gameIsEnding;
	}

	public synchronized long getRemainingTime(){
		return remainingTime;
	}

	public synchronized long getElapsedTime(){
		return elapsedTime;
	}

	public int getEpisodeNumber(){
		return episodeNumber;
	}

	public void setEpisodeNumber(int episodeNumber){
		this.episodeNumber = episodeNumber;
	}

	public long getTimeUntilNextEpisode(){
		// TODO: Add new episode system. return episodeNumber * configuration.getEpisodeMarkersDelay() - getElapsedTime();
		return 0;
	}

	public String getFormatedRemainingTime() {
		return TimeUtils.getFormattedTime(getRemainingTime());
	}

	public synchronized void setRemainingTime(long time){
		remainingTime = time;
	}

	public synchronized void setElapsedTime(long time){
		elapsedTime = time;
	}

	public boolean getPvp() {
		return pvp;
	}
	public void setPvp(boolean state) {
		pvp = state;
	}

	public void loadNewGame() {
		deleteOldPlayersFiles();
		setGameState(GameState.LOADING);
		loadConfig();

		worldBorder = new UhcWorldBorder();
		playerManager = new PlayersManager();
		teamManager = new TeamManager();

		registerListeners();

		mapLoader = new MapLoader();
		if(getConfiguration().getDebug()){
			mapLoader.loadOldWorld(configuration.getOverworldUuid(),Environment.NORMAL);
			mapLoader.loadOldWorld(configuration.getNetherUuid(),Environment.NETHER);
		}else{
			mapLoader.deleteLastWorld(configuration.getOverworldUuid());
			mapLoader.deleteLastWorld(configuration.getNetherUuid());
			mapLoader.createNewWorld(Environment.NORMAL);
			mapLoader.createNewWorld(Environment.NETHER);
		}

		if(getConfiguration().getEnableBungeeSupport())
			UhcCore.getPlugin().getServer().getMessenger().registerOutgoingPluginChannel(UhcCore.getPlugin(), "BungeeCord");

		if(getConfiguration().getEnablePregenerateWorld() && !getConfiguration().getDebug())
			mapLoader.generateChunks(Environment.NORMAL);
		else
			GameManager.getGameManager().startWaitingPlayers();
	}

	private void deleteOldPlayersFiles() {

		if(Bukkit.getServer().getWorlds().size()>0){
			// Deleting old players files
			File playerdata = new File(Bukkit.getServer().getWorlds().get(0).getName()+"/playerdata");
			if(playerdata.exists() && playerdata.isDirectory()){
				for(File playerFile : playerdata.listFiles()){
					playerFile.delete();
				}
			}

			// Deleting old players stats
			File stats = new File(Bukkit.getServer().getWorlds().get(0).getName()+"/stats");
			if(stats.exists() && stats.isDirectory()){
				for(File statFile : stats.listFiles()){
					statFile.delete();
				}
			}
		}

	}

	public void startWaitingPlayers(){
		loadWorlds();
		registerCommands();
		setGameState(GameState.WAITING);
		Bukkit.getLogger().info(Lang.DISPLAY_MESSAGE_PREFIX+" Players are now allowed to join");
		Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new PreStartThread(),0);
	}

	public void startGame(){
		setGameState(GameState.STARTING);
		if(!getConfiguration().getAlwaysDay())
			Bukkit.getWorld(configuration.getOverworldUuid()).setGameRuleValue("doDaylightCycle", "true");
		broadcastInfoMessage(Lang.GAME_STARTING);
		broadcastInfoMessage(Lang.GAME_PLEASE_WAIT_TELEPORTING);
		getPlayersManager().randomTeleportTeams();
		gameIsEnding = false;
	}

	public void startWatchingEndOfGame(){
		setGameState(GameState.PLAYING);

		World overworld = Bukkit.getWorld(configuration.getOverworldUuid());
		overworld.setGameRuleValue("doMobSpawning", "true");

		getLobby().destroyBoundingBox();
		getPlayersManager().startWatchPlayerPlayingThread();
		Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new ElapsedTimeThread());
		Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new EnablePVPThread());
		Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new Auto20MinBroadcastThread());
		if(getConfiguration().getEnableTimeLimit())
			Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new TimeBeforeEndThread());
		worldBorder.startBorderThread();
	}

	public void broadcastMessage(String message){
		for(UhcPlayer player : getPlayersManager().getPlayersList()){
			player.sendMessage(message);
		}
	}

	public void broadcastInfoMessage(String message){
		broadcastMessage(ChatColor.GREEN+ Lang.DISPLAY_MESSAGE_PREFIX+" "+ChatColor.WHITE+message);
	}

	private void loadConfig(){
		new Lang();

		FileConfiguration cfg = UhcCore.getPlugin().getConfig();
		configuration = new MainConfiguration();
		configuration.load(cfg);

		// Load kits
		KitsManager.loadKits();
		CraftsManager.loadCrafts();
		CraftsManager.loadBannedCrafts();
	}

	private void registerListeners(){
		// Registers Listeners
			List<Listener> listeners = new ArrayList<Listener>();
			listeners.add(new PlayerConnectionListener());
			listeners.add(new PlayerChatListener());
			listeners.add(new PlayerDamageListener());
			listeners.add(new ItemsListener());
			listeners.add(new PortalListener());
			listeners.add(new PlayerDeathListener());
			listeners.add(new EntityDeathListener());
			listeners.add(new CraftListener());
			listeners.add(new PingListener());
			listeners.add(new BlockListener());
			for(Listener listener : listeners){
				Bukkit.getServer().getPluginManager().registerEvents(listener, UhcCore.getPlugin());
			}
	}

	private void loadWorlds(){
		World overworld = Bukkit.getWorld(configuration.getOverworldUuid());
		overworld.save();
		overworld.setGameRuleValue("naturalRegeneration", "false");
		overworld.setGameRuleValue("doDaylightCycle", "false");
		overworld.setGameRuleValue("commandBlockOutput", "false");
		overworld.setGameRuleValue("logAdminCommands", "false");
		overworld.setGameRuleValue("sendCommandFeedback", "false");
		overworld.setGameRuleValue("doMobSpawning", "false");
		overworld.setTime(6000);
		overworld.setDifficulty(Difficulty.HARD);
		overworld.setWeatherDuration(999999999);

		World nether = Bukkit.getWorld(configuration.getNetherUuid());
		nether.save();
		nether.setGameRuleValue("naturalRegeneration", "false");
		nether.setGameRuleValue("commandBlockOutput", "false");
		nether.setGameRuleValue("logAdminCommands", "false");
		nether.setGameRuleValue("sendCommandFeedback", "false");
		nether.setDifficulty(Difficulty.HARD);

		lobby = new Lobby(new Location(overworld, 0, 200, 0), Material.GLASS);
		lobby.build();
		lobby.loadLobbyChunks();

		arena = new DeathmatchArena(new Location(overworld, 10000, configuration.getArenaPasteAtY(), 10000));
		arena.build();
		arena.loadChunks();

		UndergroundNether undergoundNether = new UndergroundNether();
		undergoundNether.build();

		worldBorder.setUpBukkitBorder();

		pvp = false;
	}

	private void registerCommands(){
		// Registers CommandExecutor
			UhcCore.getPlugin().getCommand("playuhc").setExecutor(new UhcCommandExecutor());
			UhcCore.getPlugin().getCommand("chat").setExecutor(new ChatCommandExecutor());
			UhcCore.getPlugin().getCommand("teleport").setExecutor(new TeleportCommandExecutor());
	}

	public void endGame() {
		if(gameState.equals(GameState.PLAYING) || gameState.equals(GameState.DEATHMATCH)){
			setGameState(GameState.ENDED);
			pvp = false;
			gameIsEnding = true;
			broadcastInfoMessage(Lang.GAME_FINISHED);
			getPlayersManager().playSoundToAll(Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 2);
			getPlayersManager().setAllPlayersEndGame();
			Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new StopRestartThread(),20);
		}

	}

	public void startDeathmatch() {
		if(gameState.equals(GameState.PLAYING)){
			setGameState(GameState.DEATHMATCH);
			pvp = false;
			broadcastInfoMessage(Lang.GAME_START_DEATHMATCH);
			getPlayersManager().playSoundToAll(Sound.ENTITY_ENDER_DRAGON_GROWL);
			Location arenaLocation = getArena().getLoc();

			//Set big border size to avoid hurting players
			getWorldBorder().setBukkitWorldBorderSize(arenaLocation.getWorld(), arenaLocation.getBlockX(), arenaLocation.getBlockZ(), 50000);

			// Teleport players
			getPlayersManager().setAllPlayersStartDeathmatch();

			// Shrink border to arena size
			getWorldBorder().setBukkitWorldBorderSize(arenaLocation.getWorld(), arenaLocation.getBlockX(), arenaLocation.getBlockZ(), getArena().getMaxSize());

			// Start Enable pvp thread
			Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new StartDeathmatchThread(),20);
		}

	}

	public void startEndGameThread() {
		if(!gameIsEnding && (gameState.equals(GameState.DEATHMATCH) || gameState.equals(GameState.PLAYING))){
			gameIsEnding = true;
			EndThread.start();
		}
	}

	public void stopEndGameThread(){
		if(gameIsEnding && (gameState.equals(GameState.DEATHMATCH) || gameState.equals(GameState.PLAYING))){
			gameIsEnding = false;
			EndThread.stop();
		}
	}

}