package com.gmail.val59000mc.game;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.commands.*;
import com.gmail.val59000mc.configuration.MainConfig;
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
import com.pieterdebot.biomemapping.Biome;
import com.pieterdebot.biomemapping.BiomeMappingAPI;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GameManager{

	// GameManager Instance
	private static GameManager gameManager;

	// Managers
	private final PlayersManager playerManager;
	private final TeamManager teamManager;
	private final ScoreboardManager scoreboardManager;
	private final ScenarioManager scenarioManager;
	private final MainConfig config;
	private final MapLoader mapLoader;
	private final UhcWorldBorder worldBorder;

	private Lobby lobby;
	private DeathmatchArena arena;

    private GameState gameState;
	private boolean pvp;
	private boolean gameIsEnding;
	private int episodeNumber;
	private long remainingTime;
	private long elapsedTime;

	static{
	    gameManager = null;
    }

	public GameManager() {
		gameManager = this;
		playerManager = new PlayersManager();
		teamManager = new TeamManager(playerManager);
		scoreboardManager = new ScoreboardManager();
		scenarioManager = new ScenarioManager();
		config = new MainConfig();
		mapLoader = new MapLoader();
		worldBorder = new UhcWorldBorder();

		episodeNumber = 0;
		elapsedTime = 0;
	}

	public static GameManager getGameManager(){
		return gameManager;
	}

	public PlayersManager getPlayersManager(){
		return playerManager;
	}

	public TeamManager getTeamManager() {
		return teamManager;
	}

	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

	public ScenarioManager getScenarioManager() {
		return scenarioManager;
	}

	public MainConfig getConfig() {
		return config;
	}

	public UhcWorldBorder getWorldBorder() {
		return worldBorder;
	}

	public MapLoader getMapLoader(){
		return mapLoader;
	}

	public synchronized GameState getGameState(){
		return gameState;
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
		return episodeNumber * config.get(MainConfig.EPISODE_MARKERS_DELAY) - getElapsedTime();
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
        if (config.get(MainConfig.DISABLE_MOTD)){
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

		registerListeners();

		if (config.get(MainConfig.REPLACE_OCEAN_BIOMES)){
            replaceOceanBiomes();
        }

		if(config.get(MainConfig.DEBUG)){
			mapLoader.loadOldWorld(Environment.NORMAL);
			if (config.get(MainConfig.ENABLE_NETHER)) {
				mapLoader.loadOldWorld(Environment.NETHER);
			}
			if (config.get(MainConfig.ENABLE_THE_END)) {
				mapLoader.loadOldWorld(Environment.THE_END);
			}
		}else{
			mapLoader.deleteLastWorld(Environment.NORMAL);
			mapLoader.deleteLastWorld(Environment.NETHER);
			mapLoader.deleteLastWorld(Environment.THE_END);

			mapLoader.createNewWorld(Environment.NORMAL);
			if (config.get(MainConfig.ENABLE_NETHER)) {
				mapLoader.createNewWorld(Environment.NETHER);
			}
			if (config.get(MainConfig.ENABLE_THE_END)) {
				mapLoader.createNewWorld(Environment.THE_END);
			}
		}

		if(config.get(MainConfig.ENABLE_BUNGEE_SUPPORT))
			UhcCore.getPlugin().getServer().getMessenger().registerOutgoingPluginChannel(UhcCore.getPlugin(), "BungeeCord");

		if(config.get(MainConfig.ENABLE_PRE_GENERATE_WORLD) && !config.get(MainConfig.DEBUG))
			mapLoader.generateChunks(Environment.NORMAL);
		else
			startWaitingPlayers();
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

	private void replaceOceanBiomes(){
		BiomeMappingAPI biomeMapping = new BiomeMappingAPI();

		Biome replacementBiome = Biome.PLAINS;

		for (Biome biome : Biome.values()){
			if (biome.isOcean() && biomeMapping.biomeSupported(biome)){
				try {
					biomeMapping.replaceBiomes(biome, replacementBiome);
				}catch (Exception ex){
					ex.printStackTrace();
				}

				replacementBiome = replacementBiome == Biome.PLAINS ? Biome.FOREST : Biome.PLAINS;
			}
		}
	}

	public void startWaitingPlayers(){
		loadWorlds();
		registerCommands();
		setGameState(GameState.WAITING);
		Bukkit.getLogger().info(Lang.DISPLAY_MESSAGE_PREFIX+" Players are now allowed to join");
		Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new PreStartThread(this),0);
	}

	public void startGame(){
		setGameState(GameState.STARTING);

		if(config.get(MainConfig.ENABLE_DAY_NIGHT_CYCLE)) {
			World overworld = mapLoader.getUhcWorld(Environment.NORMAL);
			VersionUtils.getVersionUtils().setGameRuleValue(overworld, "doDaylightCycle", true);
			overworld.setTime(0);
		}

		// scenario voting
		if (config.get(MainConfig.ENABLE_SCENARIO_VOTING)) {
			scenarioManager.countVotes();
		}

		Bukkit.getPluginManager().callEvent(new UhcStartingEvent());

		broadcastInfoMessage(Lang.GAME_STARTING);
		broadcastInfoMessage(Lang.GAME_PLEASE_WAIT_TELEPORTING);
		playerManager.randomTeleportTeams();
		gameIsEnding = false;
	}

	public void startWatchingEndOfGame(){
		setGameState(GameState.PLAYING);

		World overworld = getMapLoader().getUhcWorld(Environment.NORMAL);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "doMobSpawning", true);

		if (!config.get(MainConfig.LOBBY_IN_DEFAULT_WORLD)) {
			lobby.destroyBoundingBox();
		}
		playerManager.startWatchPlayerPlayingThread();
		Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new ElapsedTimeThread());
		Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new EnablePVPThread(this));

		if (config.get(MainConfig.ENABLE_EPISODE_MARKERS)){
			Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new EpisodeMarkersThread(this));
		}

		if(config.get(MainConfig.ENABLE_TIME_LIMIT)){
			Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new TimeBeforeEndThread(this));
		}

		if (config.get(MainConfig.ENABLE_DAY_NIGHT_CYCLE) && config.get(MainConfig.TIME_BEFORE_PERMANENT_DAY) != -1){
			Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new EnablePermanentDayThread(mapLoader), config.get(MainConfig.TIME_BEFORE_PERMANENT_DAY)*20);
		}

		if (config.get(MainConfig.ENABLE_FINAL_HEAL)){
            Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new FinalHealThread(this, playerManager), config.get(MainConfig.FINAL_HEAL_DELAY)*20);
        }

		worldBorder.startBorderThread();

		Bukkit.getPluginManager().callEvent(new UhcStartedEvent());
		UhcCore.getPlugin().addGameToStatistics();
	}

	public void broadcastMessage(String message){
		for(UhcPlayer player : playerManager.getPlayersList()){
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
			config.load(cfg.getConfigurationFile());
		}catch (InvalidConfigurationException | IOException ex){
			ex.printStackTrace();
			return;
		}


		// Dependencies
		config.loadWorldEdit();
		config.loadVault();
		config.loadProtocolLib();

		// Map loader
		mapLoader.loadWorldUuids(storage);

		// Config
		config.preLoad();
		config.load(cfg);
		worldBorder.loadSettings(config);

		// Load kits
		KitsManager.loadKits();

		// Load crafts
		CraftsManager.loadBannedCrafts();
		CraftsManager.loadCrafts();
		
		VaultManager.setupEconomy();

		if (config.getProtocolLibLoaded()){
			try {
				ProtocolUtils.register();
			}catch (Exception ex){
				config.setProtocolLibLoaded(false);
				Bukkit.getLogger().severe("[UhcCore] Failed to load ProtocolLib, are you using the right version?");
				ex.printStackTrace();
			}
		}
	}

	private void registerListeners(){
		// Registers Listeners
		List<Listener> listeners = new ArrayList<>();
		listeners.add(new PlayerConnectionListener(this, playerManager));
		listeners.add(new PlayerChatListener(playerManager, config));
		listeners.add(new PlayerDamageListener(this));
		listeners.add(new ItemsListener());
		listeners.add(new TeleportListener());
		listeners.add(new PlayerDeathListener());
		listeners.add(new EntityDeathListener(playerManager, config));
		listeners.add(new CraftListener());
		listeners.add(new PingListener());
		listeners.add(new BlockListener(this));
		listeners.add(new WorldListener());
		listeners.add(new PlayerMovementListener(playerManager));
		listeners.add(new EntityDamageListener(this));
		listeners.add(new PlayerHungerGainListener(playerManager));
		for(Listener listener : listeners){
			Bukkit.getServer().getPluginManager().registerEvents(listener, UhcCore.getPlugin());
		}
	}

	private void loadWorlds(){
		World overworld = mapLoader.getUhcWorld(Environment.NORMAL);
		overworld.save();
		if (!config.get(MainConfig.ENABLE_HEALTH_REGEN)){
			VersionUtils.getVersionUtils().setGameRuleValue(overworld, "naturalRegeneration", false);
		}
		if (!config.get(MainConfig.ANNOUNCE_ADVANCEMENTS) && UhcCore.getVersion() >= 12){
			VersionUtils.getVersionUtils().setGameRuleValue(overworld, "announceAdvancements", false);
		}
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "doDaylightCycle", false);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "commandBlockOutput", false);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "logAdminCommands", false);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "sendCommandFeedback", false);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, "doMobSpawning", false);
		overworld.setTime(6000);
		overworld.setDifficulty(config.get(MainConfig.GAME_DIFFICULTY));
		overworld.setWeatherDuration(999999999);

		if (config.get(MainConfig.ENABLE_NETHER)){
			World nether = mapLoader.getUhcWorld(Environment.NETHER);
			nether.save();
			if (!config.get(MainConfig.ENABLE_HEALTH_REGEN)){
				VersionUtils.getVersionUtils().setGameRuleValue(nether, "naturalRegeneration", false);
			}
			if (!config.get(MainConfig.ANNOUNCE_ADVANCEMENTS) && UhcCore.getVersion() >= 12){
				VersionUtils.getVersionUtils().setGameRuleValue(overworld, "announceAdvancements", false);
			}
			VersionUtils.getVersionUtils().setGameRuleValue(nether, "commandBlockOutput", false);
			VersionUtils.getVersionUtils().setGameRuleValue(nether, "logAdminCommands", false);
			VersionUtils.getVersionUtils().setGameRuleValue(nether, "sendCommandFeedback", false);
			nether.setDifficulty(config.get(MainConfig.GAME_DIFFICULTY));
		}

		if (config.get(MainConfig.ENABLE_THE_END)){
			World theEnd = mapLoader.getUhcWorld(Environment.THE_END);
			theEnd.save();
			if (!config.get(MainConfig.ENABLE_HEALTH_REGEN)){
				VersionUtils.getVersionUtils().setGameRuleValue(theEnd, "naturalRegeneration", false);
			}
			if (!config.get(MainConfig.ANNOUNCE_ADVANCEMENTS) && UhcCore.getVersion() >= 12){
				VersionUtils.getVersionUtils().setGameRuleValue(overworld, "announceAdvancements", false);
			}
			VersionUtils.getVersionUtils().setGameRuleValue(theEnd, "commandBlockOutput", false);
			VersionUtils.getVersionUtils().setGameRuleValue(theEnd, "logAdminCommands", false);
			VersionUtils.getVersionUtils().setGameRuleValue(theEnd, "sendCommandFeedback", false);
			theEnd.setDifficulty(config.get(MainConfig.GAME_DIFFICULTY));
		}

		if (config.get(MainConfig.LOBBY_IN_DEFAULT_WORLD)){
			lobby = new Lobby(new Location(Bukkit.getWorlds().get(0), .5, 100,.5));
		}else {
			lobby = new Lobby(new Location(overworld, 0.5, 200, 0.5));
			lobby.build();
		}

		arena = new DeathmatchArena(new Location(overworld, 10000, config.get(MainConfig.ARENA_PASTE_AT_Y), 10000));
		arena.build();

		if (config.get(MainConfig.ENABLE_UNDERGROUND_NETHER)) {
			UndergroundNether undergoundNether = new UndergroundNether();
			undergoundNether.build(config, getMapLoader().getUhcWorld(Environment.NORMAL));
		}

		worldBorder.setUpBukkitBorder(mapLoader);

		setPvp(false);
	}

	private void registerCommands(){
		// Registers CommandExecutor
		registerCommand("uhccore", new UhcCommandExecutor(this));
		registerCommand("chat", new ChatCommandExecutor(playerManager));
		registerCommand("teleport", new TeleportCommandExecutor(this));
		registerCommand("start", new StartCommandExecutor());
		registerCommand("scenarios", new ScenarioCommandExecutor(scenarioManager));
		registerCommand("teaminventory", new TeamInventoryCommandExecutor(playerManager, scenarioManager));
		registerCommand("hub", new HubCommandExecutor(this));
		registerCommand("iteminfo", new ItemInfoCommandExecutor());
		registerCommand("revive", new ReviveCommandExecutor(this));
		registerCommand("seed", new SeedCommandExecutor(mapLoader));
		registerCommand("crafts", new CustomCraftsCommandExecutor());
		registerCommand("top", new TopCommandExecutor(playerManager));
		registerCommand("spectate", new SpectateCommandExecutor(this));
		registerCommand("upload", new UploadCommandExecutor());
		registerCommand("deathmatch", new DeathmatchCommandExecutor(this));
		registerCommand("team", new TeamCommandExecutor(this));
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
			playerManager.playSoundToAll(UniversalSound.ENDERDRAGON_GROWL, 1, 2);
			playerManager.setAllPlayersEndGame();
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
		playerManager.playSoundToAll(UniversalSound.ENDERDRAGON_GROWL);

		// DeathMatch arena DeathMatch
		if (arena.isUsed()) {
			Location arenaLocation = arena.getLocation();

			//Set big border size to avoid hurting players
			worldBorder.setBukkitWorldBorderSize(arenaLocation.getWorld(), arenaLocation.getBlockX(), arenaLocation.getBlockZ(), 50000);

			// Teleport players
			playerManager.setAllPlayersStartDeathmatch();

			// Shrink border to arena size
			worldBorder.setBukkitWorldBorderSize(arenaLocation.getWorld(), arenaLocation.getBlockX(), arenaLocation.getBlockZ(), arena.getMaxSize());

			// Start Enable pvp thread
			Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new StartDeathmatchThread(this, false), 20);
		}
		// 0 0 DeathMach
		else{
			//Set big border size to avoid hurting players
			worldBorder.setBukkitWorldBorderSize(getMapLoader().getUhcWorld(Environment.NORMAL), 0, 0, 50000);

			// Teleport players
			playerManager.setAllPlayersStartDeathmatch();

			// Shrink border to arena size
			worldBorder.setBukkitWorldBorderSize(getMapLoader().getUhcWorld(Environment.NORMAL), 0, 0, config.get(MainConfig.DEATHMATCH_START_SIZE)*2);

			// Start Enable pvp thread
			Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new StartDeathmatchThread(this, true), 20);
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
