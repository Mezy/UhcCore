package com.gmail.val59000mc.game;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.commands.*;
import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.configuration.VaultManager;
import com.gmail.val59000mc.configuration.YamlFile;
import com.gmail.val59000mc.customitems.CraftsManager;
import com.gmail.val59000mc.customitems.KitsManager;
import com.gmail.val59000mc.events.UhcGameStateChangedEvent;
import com.gmail.val59000mc.events.UhcStartingEvent;
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
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Listener;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GameManager{
	private Lobby lobby;
	private DeathmatchArena arena;

    private MapLoader mapLoader;
    private UhcWorldBorder worldBorder;
	private PlayersManager playerManager;
	private TeamManager teamManager;
	private ScoreboardManager scoreboardManager;
	private ScenarioManager scenarioManager;
    private MainConfiguration configuration;

    private GameState gameState;
	private boolean pvp;
	private boolean gameIsEnding;
	private int episodeNumber;
	private long remainingTime;
	private long elapsedTime;

	private static GameManager gameManager;

	static{
	    gameManager = null;
    }

	public GameManager() {
		gameManager = this;
		playerManager = new PlayersManager();
		scoreboardManager = new ScoreboardManager();
		scenarioManager = new ScenarioManager();

		episodeNumber = 0;
		elapsedTime = 0;
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
		return gameManager;
	}

	public synchronized GameState getGameState(){
		return gameState;
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

    public void setGameState(GameState gameState){
        Validate.notNull(gameState);

        if (this.gameState == gameState){
            return; // Don't change the game state when the same.
        }

        GameState oldGameState = this.gameState;
        this.gameState = gameState;

        // Call UhcGameStateChangedEvent
        Bukkit.getPluginManager().callEvent(new UhcGameStateChangedEvent(oldGameState, gameState));

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
        if (getConfiguration().getDisableMotd()){
            return; // No motd support
        }

        if (motd == null){
        	return; // Failed to load lang.yml so motd is null.
		}

        try {
            Class craftServerClass = NMSUtils.getNMSClass("CraftServer");
            Object craftServer = craftServerClass.cast(Bukkit.getServer());
            Object dedicatedPlayerList = NMSUtils.getHandle(craftServer);
            Object dedicatedServer = NMSUtils.getServer(dedicatedPlayerList);

            Method setMotd = NMSUtils.getMethod(dedicatedServer.getClass(), "setMotd");
            setMotd.invoke(dedicatedServer, motd);
        }catch (ReflectiveOperationException | NullPointerException ex){
            ex.printStackTrace();
        }
    }

	public void loadNewGame() {
		deleteOldPlayersFiles();
		loadConfig();
		setGameState(GameState.LOADING);

		worldBorder = new UhcWorldBorder();
		teamManager = new TeamManager();

		registerListeners();

		if (configuration.getReplaceOceanBiomes()){
            VersionUtils.getVersionUtils().replaceOceanBiomes();
        }

		mapLoader = new MapLoader();
		if(getConfiguration().getDebug()){
			mapLoader.loadOldWorld(configuration.getOverworldUuid(),Environment.NORMAL);
			if (configuration.getEnableNether()) {
				mapLoader.loadOldWorld(configuration.getNetherUuid(), Environment.NETHER);
			}
			if (configuration.getEnableTheEnd()) {
				mapLoader.loadOldWorld(configuration.getNetherUuid(), Environment.THE_END);
			}
		}else{
			mapLoader.deleteLastWorld(configuration.getOverworldUuid());
			mapLoader.deleteLastWorld(configuration.getNetherUuid());
			mapLoader.deleteLastWorld(configuration.getTheEndUuid());
			mapLoader.createNewWorld(Environment.NORMAL);
			if (configuration.getEnableNether()) {
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

			// Deleting old players advancements
			File advancements = new File(Bukkit.getServer().getWorlds().get(0).getName()+"/advancements");
			if(advancements.exists() && advancements.isDirectory()){
				for(File advancementFile : advancements.listFiles()){
					advancementFile.delete();
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

		if(getConfiguration().getEnableDayNightCycle()) {
			World overworld = Bukkit.getWorld(configuration.getOverworldUuid());
			VersionUtils.getVersionUtils().setGameRuleValue(overworld, "doDaylightCycle", true);
			overworld.setTime(0);
		}

		// scenario voting
		if (getConfiguration().getEnableScenarioVoting()) {
			getScenarioManager().countVotes();
		}

		Bukkit.getPluginManager().callEvent(new UhcStartingEvent());

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

		if(getConfiguration().getEnableTimeLimit()){
			Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new TimeBeforeEndThread());
		}

		if (getConfiguration().getEnableDayNightCycle() && getConfiguration().getTimeBeforePermanentDay() != -1){
			Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new EnablePermanentDayThread(), getConfiguration().getTimeBeforePermanentDay()*20);
		}

		if (getConfiguration().getEnableFinalHeal()){
            Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new FinalHealThread(), getConfiguration().getFinalHealDelay()*20);
        }

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
		broadcastMessage(Lang.DISPLAY_MESSAGE_PREFIX+" "+message);
	}

	public void loadConfig(){
		new Lang();

		YamlFile cfg;
		YamlFile storage;

		try{
			cfg = FileUtils.saveResourceIfNotAvailable("config.yml");
			storage = FileUtils.saveResourceIfNotAvailable("storage.yml");
		}catch (InvalidConfigurationException ex){
			ex.printStackTrace();
			return;
		}

		configuration = new MainConfiguration();

		// Dependencies
		configuration.loadWorldEdit();
		configuration.loadVault();
		configuration.loadProtocolLib();

		// Config
		configuration.preLoad(cfg);
		configuration.load(cfg, storage);

		// Load kits
		KitsManager.loadKits();

		// Load crafts
		CraftsManager.loadBannedCrafts();
		CraftsManager.loadCrafts();
		
		VaultManager.setupEconomy();

		if (configuration.getProtocolLibLoaded()){
			try {
				ProtocolUtils.register();
			}catch (Exception ex){
				configuration.setProtocolLibLoaded(false);
				Bukkit.getLogger().severe("[UhcCore] Failed to load ProtocolLib, are you using the right version?");
				ex.printStackTrace();
			}
		}
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
		listeners.add(new PlayerMovementListener(getPlayersManager()));
		listeners.add(new EntityDamageListener());
		for(Listener listener : listeners){
			Bukkit.getServer().getPluginManager().registerEvents(listener, UhcCore.getPlugin());
		}
	}

	private void loadWorlds(){
		World overworld = Bukkit.getWorld(configuration.getOverworldUuid());
		overworld.save();
		if (!configuration.getEnableHealthRegen()){
			VersionUtils.getVersionUtils().setGameRuleValue(overworld, "naturalRegeneration", false);
		}
		if (!configuration.getAnnounceAdvancements() && UhcCore.getVersion() >= 12){
			VersionUtils.getVersionUtils().setGameRuleValue(overworld, "announceAdvancements", false);
		}
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "doDaylightCycle", false);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "commandBlockOutput", false);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "logAdminCommands", false);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "sendCommandFeedback", false);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "doMobSpawning", false);
		overworld.setTime(6000);
		overworld.setDifficulty(configuration.getGameDifficulty());
		overworld.setWeatherDuration(999999999);

		if (configuration.getEnableNether()){
			World nether = Bukkit.getWorld(configuration.getNetherUuid());
			nether.save();
			if (!configuration.getEnableHealthRegen()){
				VersionUtils.getVersionUtils().setGameRuleValue(nether, "naturalRegeneration", false);
			}
			if (!configuration.getAnnounceAdvancements() && UhcCore.getVersion() >= 12){
				VersionUtils.getVersionUtils().setGameRuleValue(overworld, "announceAdvancements", false);
			}
			VersionUtils.getVersionUtils().setGameRuleValue(nether, "commandBlockOutput", false);
			VersionUtils.getVersionUtils().setGameRuleValue(nether, "logAdminCommands", false);
			VersionUtils.getVersionUtils().setGameRuleValue(nether, "sendCommandFeedback", false);
			nether.setDifficulty(configuration.getGameDifficulty());
		}

		if (configuration.getEnableTheEnd()){
			World theEnd = Bukkit.getWorld(configuration.getTheEndUuid());
			theEnd.save();
			if (!configuration.getEnableHealthRegen()){
				VersionUtils.getVersionUtils().setGameRuleValue(theEnd, "naturalRegeneration", false);
			}
			if (!configuration.getAnnounceAdvancements() && UhcCore.getVersion() >= 12){
				VersionUtils.getVersionUtils().setGameRuleValue(overworld, "announceAdvancements", false);
			}
			VersionUtils.getVersionUtils().setGameRuleValue(theEnd, "commandBlockOutput", false);
			VersionUtils.getVersionUtils().setGameRuleValue(theEnd, "logAdminCommands", false);
			VersionUtils.getVersionUtils().setGameRuleValue(theEnd, "sendCommandFeedback", false);
			theEnd.setDifficulty(configuration.getGameDifficulty());
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

		setPvp(false);
	}

	private void registerCommands(){
		// Registers CommandExecutor
		registerCommand("uhccore", new UhcCommandExecutor());
		registerCommand("chat", new ChatCommandExecutor());
		registerCommand("teleport", new TeleportCommandExecutor());
		registerCommand("start", new StartCommandExecutor());
		registerCommand("scenarios", new ScenarioCommandExecutor());
		registerCommand("teaminventory", new TeamInventoryCommandExecutor());
		registerCommand("hub", new HubCommandExecutor());
		registerCommand("iteminfo", new ItemInfoCommandExecutor());
		registerCommand("revive", new ReviveCommandExecutor());
		registerCommand("seed", new SeedCommandExecutor());
		registerCommand("crafts", new CustomCraftsCommandExecutor());
		registerCommand("top", new TopCommandExecutor());
		registerCommand("spectate", new SpectateCommandExecutor());
		registerCommand("upload", new UploadCommandExecutor());
		registerCommand("deathmatch", new DeathmatchCommandExecutor());
		registerCommand("team", new TeamCommandExecutor());
	}

	private void registerCommand(String commandName, CommandExecutor executor){
		PluginCommand command = UhcCore.getPlugin().getCommand(commandName);
		if (command == null){
			Bukkit.getLogger().warning("[UhcCore] Failed to register " + commandName + " command!");
			return;
		}

		command.setExecutor(executor);
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

	public void startDeathmatch(){
		// DeathMatch can only be stated while GameState = Playing
		if (gameState != GameState.PLAYING){
			return;
		}

		setGameState(GameState.DEATHMATCH);
		pvp = false;
		broadcastInfoMessage(Lang.GAME_START_DEATHMATCH);
		getPlayersManager().playSoundToAll(UniversalSound.ENDERDRAGON_GROWL);

		// DeathMatch arena DeathMatch
		if (getArena().isUsed()) {
			Location arenaLocation = getArena().getLoc();

			//Set big border size to avoid hurting players
			getWorldBorder().setBukkitWorldBorderSize(arenaLocation.getWorld(), arenaLocation.getBlockX(), arenaLocation.getBlockZ(), 50000);

			// Teleport players
			getPlayersManager().setAllPlayersStartDeathmatch();

			// Shrink border to arena size
			getWorldBorder().setBukkitWorldBorderSize(arenaLocation.getWorld(), arenaLocation.getBlockX(), arenaLocation.getBlockZ(), getArena().getMaxSize());

			// Start Enable pvp thread
			Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new StartDeathmatchThread(false), 20);
		}
		// 0 0 DeathMach
		else{
			Location deathmatchLocation = getLobby().getLoc();

			//Set big border size to avoid hurting players
			getWorldBorder().setBukkitWorldBorderSize(deathmatchLocation.getWorld(), deathmatchLocation.getBlockX(), deathmatchLocation.getBlockZ(), 50000);

			// Teleport players
			getPlayersManager().setAllPlayersStartDeathmatch();

			// Shrink border to arena size
			getWorldBorder().setBukkitWorldBorderSize(deathmatchLocation.getWorld(), deathmatchLocation.getBlockX(), deathmatchLocation.getBlockZ(), getConfiguration().getDeathmatchStartSize()*2);

			// Start Enable pvp thread
			Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new StartDeathmatchThread(true), 20);
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