package com.gmail.val59000mc.playuhc.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.gmail.val59000mc.playuhc.sounds.SoundManager;
import com.gmail.val59000mc.playuhc.sounds.UhcSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.gmail.val59000mc.playuhc.PlayUhc;
import com.gmail.val59000mc.playuhc.commands.ChatCommandExecutor;
import com.gmail.val59000mc.playuhc.commands.TeleportCommandExecutor;
import com.gmail.val59000mc.playuhc.commands.UhcCommandExecutor;
import com.gmail.val59000mc.playuhc.configuration.MainConfiguration;
import com.gmail.val59000mc.playuhc.customitems.CraftsManager;
import com.gmail.val59000mc.playuhc.customitems.KitsManager;
import com.gmail.val59000mc.playuhc.languages.Lang;
import com.gmail.val59000mc.playuhc.listeners.BlockListener;
import com.gmail.val59000mc.playuhc.listeners.CraftListener;
import com.gmail.val59000mc.playuhc.listeners.EntityDeathListener;
import com.gmail.val59000mc.playuhc.listeners.ItemsListener;
import com.gmail.val59000mc.playuhc.listeners.PingListener;
import com.gmail.val59000mc.playuhc.listeners.PlayerChatListener;
import com.gmail.val59000mc.playuhc.listeners.PlayerConnectionListener;
import com.gmail.val59000mc.playuhc.listeners.PlayerDamageListener;
import com.gmail.val59000mc.playuhc.listeners.PlayerDeathListener;
import com.gmail.val59000mc.playuhc.listeners.PortalListener;
import com.gmail.val59000mc.playuhc.maploader.MapLoader;
import com.gmail.val59000mc.playuhc.players.PlayersManager;
import com.gmail.val59000mc.playuhc.players.UhcPlayer;
import com.gmail.val59000mc.playuhc.schematics.DeathmatchArena;
import com.gmail.val59000mc.playuhc.schematics.Lobby;
import com.gmail.val59000mc.playuhc.schematics.UndergroundNether;
import com.gmail.val59000mc.playuhc.threads.Auto20MinBroadcastThread;
import com.gmail.val59000mc.playuhc.threads.ElapsedTimeThread;
import com.gmail.val59000mc.playuhc.threads.EnablePVPThread;
import com.gmail.val59000mc.playuhc.threads.EndThread;
import com.gmail.val59000mc.playuhc.threads.PreStartThread;
import com.gmail.val59000mc.playuhc.threads.StartDeathmatchThread;
import com.gmail.val59000mc.playuhc.threads.StopRestartThread;
import com.gmail.val59000mc.playuhc.threads.TimeBeforeEndThread;
import com.gmail.val59000mc.playuhc.utils.TimeUtils;


public class GameManager {
	private GameState gameState;
	private Lobby lobby;
	private DeathmatchArena arena;
	private PlayersManager playerManager;
	private MapLoader mapLoader;
	private UhcWorldBorder worldBorder;
	private SoundManager soundManager;
	private boolean pvp;
	private boolean gameIsEnding;
	
	private long remainingTime;
	private long elapsedTime = 0;
	
	private MainConfiguration configuration;
	
	private static GameManager uhcGM = null;
	
	public GameManager() {
		uhcGM = this;
	}



	public MainConfiguration getConfiguration() {
		return configuration;
	}
	
	public UhcWorldBorder getWorldBorder() {
		return worldBorder;
	}

	public SoundManager getSoundManager() {
		return soundManager;
	}

	public static GameManager getGameManager(){
		return uhcGM;
	}
	
	public synchronized GameState getGameState(){
		return gameState;
	}

	
	
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
	
	public PlayersManager getPlayersManager(){
		return playerManager;
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
		gameState = GameState.LOADING;
		soundManager = new SoundManager();
		loadConfig();
		
		worldBorder = new UhcWorldBorder();
		playerManager = new PlayersManager();
		
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
			PlayUhc.getPlugin().getServer().getMessenger().registerOutgoingPluginChannel(PlayUhc.getPlugin(), "BungeeCord");
		
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
		gameState = GameState.WAITING;
		Bukkit.getLogger().info(Lang.DISPLAY_MESSAGE_PREFIX+" Players are now allowed to join");
		Bukkit.getScheduler().scheduleSyncDelayedTask(PlayUhc.getPlugin(), new PreStartThread(),0);
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
		gameState = GameState.PLAYING;

		World overworld = Bukkit.getWorld(configuration.getOverworldUuid());
		overworld.setGameRuleValue("doMobSpawning", "true");
		
		getLobby().destroyBoundingBox();
		getPlayersManager().startWatchPlayerPlayingThread();
		Bukkit.getScheduler().runTaskAsynchronously(PlayUhc.getPlugin(), new ElapsedTimeThread());
		Bukkit.getScheduler().runTaskAsynchronously(PlayUhc.getPlugin(), new EnablePVPThread());
		Bukkit.getScheduler().runTaskAsynchronously(PlayUhc.getPlugin(), new Auto20MinBroadcastThread());
		if(getConfiguration().getEnableTimeLimit())
			Bukkit.getScheduler().runTaskAsynchronously(PlayUhc.getPlugin(), new TimeBeforeEndThread());
		worldBorder.startBorderThread();
	}

	public void broadcastMessage(String message){
		for(UhcPlayer player : getPlayersManager().getPlayersList()){
			player.sendMessage(message);
		}
	}
	
	public void broadcastInfoMessage(String message){
		broadcastMessage(ChatColor.GREEN+Lang.DISPLAY_MESSAGE_PREFIX+" "+ChatColor.WHITE+message);
	}
	
	private void loadConfig(){
		new Lang();
		
		FileConfiguration cfg = PlayUhc.getPlugin().getConfig();
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
				Bukkit.getServer().getPluginManager().registerEvents(listener, PlayUhc.getPlugin());
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
			PlayUhc.getPlugin().getCommand("playuhc").setExecutor(new UhcCommandExecutor());
			PlayUhc.getPlugin().getCommand("chat").setExecutor(new ChatCommandExecutor());
			PlayUhc.getPlugin().getCommand("teleport").setExecutor(new TeleportCommandExecutor());
	}

	public void endGame() {
		if(gameState.equals(GameState.PLAYING) || gameState.equals(GameState.DEATHMATCH)){
			setGameState(GameState.ENDED);
			pvp = false;
			gameIsEnding = true;
			broadcastInfoMessage(Lang.GAME_FINISHED);
			getPlayersManager().playSoundToAll(UhcSound.ENDERDRAGON_GROWL, 1, 2);
			getPlayersManager().setAllPlayersEndGame();
			Bukkit.getScheduler().scheduleSyncDelayedTask(PlayUhc.getPlugin(), new StopRestartThread(),20);
		}
		
	}
	
	public void startDeathmatch() {
		if(gameState.equals(GameState.PLAYING)){
			setGameState(GameState.DEATHMATCH);
			pvp = false;
			broadcastInfoMessage(Lang.GAME_START_DEATHMATCH);
			getPlayersManager().playSoundToAll(UhcSound.ENDERDRAGON_GROWL);
			Location arenaLocation = getArena().getLoc();
			
			//Set big border size to avoid hurting players
			getWorldBorder().setBukkitWorldBorderSize(arenaLocation.getWorld(), arenaLocation.getBlockX(), arenaLocation.getBlockZ(), 50000);
			
			// Teleport players
			getPlayersManager().setAllPlayersStartDeathmatch();
			
			// Shrink border to arena size
			getWorldBorder().setBukkitWorldBorderSize(arenaLocation.getWorld(), arenaLocation.getBlockX(), arenaLocation.getBlockZ(), getArena().getMaxSize());
			
			// Start Enable pvp thread
			Bukkit.getScheduler().scheduleSyncDelayedTask(PlayUhc.getPlugin(), new StartDeathmatchThread(),20);
		}
		
	}

	public void startEndGameThread() {
		if(gameIsEnding == false && (gameState.equals(GameState.DEATHMATCH) || gameState.equals(GameState.PLAYING))){
			gameIsEnding = true;
			EndThread.start();
		}		
	}
	
	public void stopEndGameThread(){
		if(gameIsEnding == true && (gameState.equals(GameState.DEATHMATCH) || gameState.equals(GameState.PLAYING))){
			gameIsEnding = false;
			EndThread.stop();
		}
	}


	
	


	
	
	

}
