package com.gmail.val59000mc.game;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.commands.*;
import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.configuration.YamlFile;
import com.gmail.val59000mc.customitems.CraftsManager;
import com.gmail.val59000mc.customitems.KitsManager;
import com.gmail.val59000mc.events.UhcStartedEvent;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.listeners.*;
import com.gmail.val59000mc.maploader.MapLoader;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.TeamManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.ScenarioManager;
import com.gmail.val59000mc.schematics.DeathmatchArena;
import com.gmail.val59000mc.schematics.Lobby;
import com.gmail.val59000mc.schematics.UndergroundNether;
import com.gmail.val59000mc.scoreboard.ScoreboardManager;
import com.gmail.val59000mc.threads.*;
import com.gmail.val59000mc.utils.*;
import org.bukkit.*;
import org.bukkit.World.Environment;
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
	private ScenarioManager scenarioManager;
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
		scenarioManager = new ScenarioManager();
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

	public ScenarioManager getScenarioManager() {
		return scenarioManager;
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
		return episodeNumber * configuration.getEpisodeMarkersDelay() - getElapsedTime();
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

		if (configuration.getReplaceOceanBiomes()){
            VersionUtils.getVersionUtils().replaceOceanBiomes();
        }

		mapLoader = new MapLoader();
		if(getConfiguration().getDebug()){
			mapLoader.loadOldWorld(configuration.getOverworldUuid(),Environment.NORMAL);
			mapLoader.loadOldWorld(configuration.getNetherUuid(),Environment.NETHER);
		}else{
			mapLoader.deleteLastWorld(configuration.getOverworldUuid());
			mapLoader.deleteLastWorld(configuration.getNetherUuid());
			mapLoader.deleteLastWorld(configuration.getTheEndUuid());
			mapLoader.createNewWorld(Environment.NORMAL);
			if (!configuration.getBanNether()) {
				mapLoader.createNewWorld(Environment.NETHER);
			}
			if (configuration.getEnableTheEnd()) {
				mapLoader.createNewWorld(Environment.THE_END);
			}
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
		if(!getConfiguration().getAlwaysDay()) {
			World overworld = Bukkit.getWorld(configuration.getOverworldUuid());
			VersionUtils.getVersionUtils().setGameRuleValue(overworld, "doDaylightCycle", true);
			overworld.setTime(0);
		}

		// scenario voting
		if (getConfiguration().getEnableScenarioVoting()) {
			getScenarioManager().countVotes();
		}

		broadcastInfoMessage(Lang.GAME_STARTING);
		broadcastInfoMessage(Lang.GAME_PLEASE_WAIT_TELEPORTING);
		getPlayersManager().randomTeleportTeams();
		gameIsEnding = false;
	}

	public void startWatchingEndOfGame(){
		setGameState(GameState.PLAYING);

		World overworld = Bukkit.getWorld(configuration.getOverworldUuid());
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "doMobSpawning", true);

		getLobby().destroyBoundingBox();
		getPlayersManager().startWatchPlayerPlayingThread();
		Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new ElapsedTimeThread());
		Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new EnablePVPThread());
		if (getConfiguration().getEnableEpisodeMarkers()){
			Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new EpisodeMarkersThread());
		}
		if(getConfiguration().getEnableTimeLimit())
			Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new TimeBeforeEndThread());
		worldBorder.startBorderThread();

		Bukkit.getPluginManager().callEvent(new UhcStartedEvent());
		UhcCore.getPlugin().addGameToStatistics();
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

		YamlFile cfg = FileUtils.saveResourceIfNotAvailable("config.yml");
		YamlFile storage = FileUtils.saveResourceIfNotAvailable("storage.yml");
		configuration = new MainConfiguration();
		configuration.load(cfg, storage);

		// Load kits
		KitsManager.loadKits();
		CraftsManager.loadBannedCrafts();
		CraftsManager.loadCrafts();
	}

	private void registerListeners(){
		// Registers Listeners
		List<Listener> listeners = new ArrayList<Listener>();
		listeners.add(new PlayerConnectionListener());
		listeners.add(new PlayerChatListener());
		listeners.add(new PlayerDamageListener());
		listeners.add(new ItemsListener());
		listeners.add(new TeleportListener());
		listeners.add(new PlayerDeathListener());
		listeners.add(new EntityDeathListener());
		listeners.add(new CraftListener());
		listeners.add(new PingListener());
		listeners.add(new BlockListener());
		listeners.add(new WorldListener());
		for(Listener listener : listeners){
			Bukkit.getServer().getPluginManager().registerEvents(listener, UhcCore.getPlugin());
		}
	}

	private void loadWorlds(){
		World overworld = Bukkit.getWorld(configuration.getOverworldUuid());
		overworld.save();
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "naturalRegeneration", false);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "doDaylightCycle", false);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "commandBlockOutput", false);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "logAdminCommands", false);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "sendCommandFeedback", false);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "doMobSpawning", false);
		overworld.setTime(6000);
		overworld.setDifficulty(Difficulty.HARD);
		overworld.setWeatherDuration(999999999);

		if (!configuration.getBanNether()){
			World nether = Bukkit.getWorld(configuration.getNetherUuid());
			nether.save();
			VersionUtils.getVersionUtils().setGameRuleValue(nether, "naturalRegeneration", false);
			VersionUtils.getVersionUtils().setGameRuleValue(nether, "commandBlockOutput", false);
			VersionUtils.getVersionUtils().setGameRuleValue(nether, "logAdminCommands", false);
			VersionUtils.getVersionUtils().setGameRuleValue(nether, "sendCommandFeedback", false);
			nether.setDifficulty(Difficulty.HARD);
		}

		if (configuration.getEnableTheEnd()){
			World theEnd = Bukkit.getWorld(configuration.getTheEndUuid());
			theEnd.save();
			VersionUtils.getVersionUtils().setGameRuleValue(theEnd, "naturalRegeneration", false);
			VersionUtils.getVersionUtils().setGameRuleValue(theEnd, "commandBlockOutput", false);
			VersionUtils.getVersionUtils().setGameRuleValue(theEnd, "logAdminCommands", false);
			VersionUtils.getVersionUtils().setGameRuleValue(theEnd, "sendCommandFeedback", false);
			theEnd.setDifficulty(Difficulty.HARD);
		}

		lobby = new Lobby(new Location(overworld, 0.5, 200, 0.5), Material.GLASS);
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
		UhcCore.getPlugin().getCommand("uhccore").setExecutor(new UhcCommandExecutor());
		UhcCore.getPlugin().getCommand("chat").setExecutor(new ChatCommandExecutor());
		UhcCore.getPlugin().getCommand("teleport").setExecutor(new TeleportCommandExecutor());
		UhcCore.getPlugin().getCommand("start").setExecutor(new StartCommandExecutor());
		UhcCore.getPlugin().getCommand("scenarios").setExecutor(new ScenarioCommandExecutor());
		UhcCore.getPlugin().getCommand("teaminventory").setExecutor(new TeamInventoryCommandExecutor());
		UhcCore.getPlugin().getCommand("hub").setExecutor(new HubCommandExecutor());
		UhcCore.getPlugin().getCommand("iteminfo").setExecutor(new ItemInfoCommandExecutor());
		UhcCore.getPlugin().getCommand("revive").setExecutor(new ReviveCommandExecutor());
		UhcCore.getPlugin().getCommand("seed").setExecutor(new SeedCommandExecutor());
	}

	public void endGame() {
		if(gameState.equals(GameState.PLAYING) || gameState.equals(GameState.DEATHMATCH)){
			setGameState(GameState.ENDED);
			pvp = false;
			gameIsEnding = true;
			broadcastInfoMessage(Lang.GAME_FINISHED);
			getPlayersManager().playSoundToAll(UniversalSound.ENDERDRAGON_GROWL, 1, 2);
			getPlayersManager().setAllPlayersEndGame();
			Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new StopRestartThread(),20);
		}

	}

	public void startDeathmatch() {
		if(gameState.equals(GameState.PLAYING)){
			setGameState(GameState.DEATHMATCH);
			pvp = false;
			broadcastInfoMessage(Lang.GAME_START_DEATHMATCH);
			getPlayersManager().playSoundToAll(UniversalSound.ENDERDRAGON_GROWL);
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