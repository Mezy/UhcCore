package com.gmail.val59000mc.maploader;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.schematics.DeathmatchArena;
import com.gmail.val59000mc.schematics.Lobby;
import com.gmail.val59000mc.schematics.UndergroundNether;
import com.gmail.val59000mc.threads.ChunkLoaderThread;
import com.gmail.val59000mc.threads.WorldBorderThread;
import com.gmail.val59000mc.utils.FileUtils;
import com.gmail.val59000mc.configuration.YamlFile;
import com.gmail.val59000mc.utils.VersionUtils;
import com.pieterdebot.biomemapping.Biome;
import com.pieterdebot.biomemapping.BiomeMappingAPI;
import io.papermc.lib.PaperLib;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.configuration.InvalidConfigurationException;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class MapLoader {

	public final static String DO_DAYLIGHT_CYCLE = "doDaylightCycle";
	public final static String DO_MOB_SPAWNING = "doMobSpawning";
	public final static String NATURAL_REGENERATION = "naturalRegeneration";
	public final static String ANNOUNCE_ADVANCEMENTS = "announceAdvancements";
	public final static String COMMAND_BLOCK_OUTPUT = "commandBlockOutput";
	public final static String LOG_ADMIN_COMMANDS = "logAdminCommands";
	public final static String SEND_COMMAND_FEEDBACK = "sendCommandFeedback";

	private final MainConfig config;
	private final Map<Environment, String> worldUuids;

	private Lobby lobby;
	private DeathmatchArena arena;

	private long mapSeed;
	private String mapName;

	public MapLoader(MainConfig config){
		this.config = config;
		worldUuids = new HashMap<>();
		mapSeed = -1;
		mapName = null;
	}

	public Lobby getLobby() {
		return lobby;
	}

	public DeathmatchArena getArena() {
		return arena;
	}

	public double getBorderSize(){
		World overworld = GameManager.getGameManager().getMapLoader().getUhcWorld(World.Environment.NORMAL);
		return overworld.getWorldBorder().getSize()/2;
	}

	public void loadWorlds(boolean debug) {
		if (config.get(MainConfig.REPLACE_OCEAN_BIOMES)){
			replaceOceanBiomes();
		}

		deleteOldPlayersFiles();

		if(debug){
			loadOldWorld(Environment.NORMAL);
			if (config.get(MainConfig.ENABLE_NETHER)) {
				loadOldWorld(Environment.NETHER);
			}
			if (config.get(MainConfig.ENABLE_THE_END)) {
				loadOldWorld(Environment.THE_END);
			}
		}else{
			deleteLastWorld(Environment.NORMAL);
			deleteLastWorld(Environment.NETHER);
			deleteLastWorld(Environment.THE_END);

			createNewWorld(Environment.NORMAL);
			if (config.get(MainConfig.ENABLE_NETHER)) {
				createNewWorld(Environment.NETHER);
			}
			if (config.get(MainConfig.ENABLE_THE_END)) {
				createNewWorld(Environment.THE_END);
			}
		}
	}

	public void deleteLastWorld(Environment env){
		String uuid = worldUuids.get(env);

		if(uuid == null || uuid.equals("null")){
			Bukkit.getLogger().info("[UhcCore] No world to delete");
		}else{
			File worldDir = new File(uuid);
			if(worldDir.exists()){
				Bukkit.getLogger().info("[UhcCore] Deleting last world : "+uuid);
				FileUtils.deleteFile(worldDir);
			}else{
				Bukkit.getLogger().info("[UhcCore] World "+uuid+" can't be removed, directory not found");
			}
		}
	}
	
	public void createNewWorld(Environment env){
		String worldName = UUID.randomUUID().toString();
		if (UhcCore.getPlugin().getConfig().getBoolean("permanent-world-names", false)){
			worldName = "uhc-"+env.name().toLowerCase();
		}

		Bukkit.getLogger().info("[UhcCore] Creating new world : "+worldName);
		
		GameManager gm = GameManager.getGameManager();

		WorldCreator wc = new WorldCreator(worldName);
		wc.generateStructures(true);
		wc.environment(env);

		List<Long> seeds = gm.getConfig().get(MainConfig.SEEDS);
		List<String> worlds = gm.getConfig().get(MainConfig.WORLDS);
		if(gm.getConfig().get(MainConfig.PICK_RANDOM_SEED_FROM_LIST) && !seeds.isEmpty()){
			if (mapSeed == -1) {
				Random r = new Random();
				mapSeed = seeds.get(r.nextInt(seeds.size()));
				Bukkit.getLogger().info("[UhcCore] Picking random seed from list : "+mapSeed);
			}
			wc.seed(mapSeed);
		}else if(gm.getConfig().get(MainConfig.PICK_RANDOM_WORLD_FROM_LIST) && !worlds.isEmpty()){
			if (mapName == null) {
				Random r = new Random();
				mapName = worlds.get(r.nextInt(worlds.size()));
			}

			String copyWorld = mapName;
			if (env != Environment.NORMAL){
				copyWorld = copyWorld + "_" + env.name().toLowerCase();
			}

			copyWorld(copyWorld, worldName);
		}

		worldUuids.put(env, worldName);

		YamlFile storage;

		try{
			storage = FileUtils.saveResourceIfNotAvailable(UhcCore.getPlugin(), "storage.yml");
		}catch (InvalidConfigurationException ex){
			ex.printStackTrace();
			return;
		}

		storage.set("worlds." + env.name().toLowerCase(), worldName);
		try {
			storage.save();
		}catch (IOException ex){
			ex.printStackTrace();
		}
		
		wc.type(WorldType.NORMAL);
		Bukkit.getServer().createWorld(wc);
	}
	
	public void loadOldWorld(Environment env){
		String uuid = worldUuids.get(env);

		if(uuid == null || uuid.equals("null")){
			Bukkit.getLogger().info("[UhcCore] No world to load, defaulting to default behavior");
			this.createNewWorld(env);
		}else{
			File worldDir = new File(uuid);
			if(worldDir.exists()){
				// Loading existing world
				Bukkit.getServer().createWorld(new WorldCreator(uuid));
			}else{
				this.createNewWorld(env);
			}
		}
	}

	public void loadWorldUuids(){
		YamlFile storage;

		try{
			storage = FileUtils.saveResourceIfNotAvailable(UhcCore.getPlugin(), "storage.yml");
		}catch (InvalidConfigurationException ex){
			ex.printStackTrace();
			return;
		}

		worldUuids.put(Environment.NORMAL, storage.getString("worlds.normal"));
		worldUuids.put(Environment.NETHER, storage.getString("worlds.nether"));
		worldUuids.put(Environment.THE_END, storage.getString("worlds.the_end"));
	}

	/**
	 * Used to obtain the UHC world uuid matching the given environment.
	 * @param environment The environment of the world uuid you want to obtain.
	 * @return Returns the UHC world uuid matching the environment or null if it doesn't exist.
	 */
	@Nullable
	public String getUhcWorldUuid(Environment environment){
		Validate.notNull(environment);
		return worldUuids.get(environment);
	}

	/**
	 * Used to obtain the UHC world matching the given environment.
	 * @param environment The environment of the world you want to obtain.
	 * @return Returns the UHC world matching the environment or null if it doesn't exist.
	 */
	@Nullable
	public World getUhcWorld(Environment environment){
		Validate.notNull(environment);

		String worldUuid = worldUuids.get(environment);
		if (worldUuid == null){
			return null;
		}

		return Bukkit.getWorld(worldUuid);
	}

	public void setWorldsStartGame() {
		World overworld = getUhcWorld(Environment.NORMAL);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, DO_MOB_SPAWNING, true);

		if(config.get(MainConfig.ENABLE_DAY_NIGHT_CYCLE)) {
			VersionUtils.getVersionUtils().setGameRuleValue(overworld, DO_DAYLIGHT_CYCLE, true);
			overworld.setTime(0);
		}

		if (!config.get(MainConfig.LOBBY_IN_DEFAULT_WORLD)) {
			lobby.destroyBoundingBox();
		}

		if(config.get(MainConfig.BORDER_IS_MOVING)){
			int endSize = config.get(MainConfig.BORDER_END_SIZE);
			int timeToShrink = config.get(MainConfig.BORDER_TIME_TO_SHRINK);
			int timeBeforeShrink = config.get(MainConfig.BORDER_TIME_BEFORE_SHRINK);

			Bukkit.getScheduler().runTask(UhcCore.getPlugin(), new WorldBorderThread(timeBeforeShrink, endSize, timeToShrink));
		}
	}

	public void prepareWorlds() {
		Difficulty difficulty = config.get(MainConfig.GAME_DIFFICULTY);
		boolean healthRegen = config.get(MainConfig.ENABLE_HEALTH_REGEN);
		boolean announceAdvancements = config.get(MainConfig.ANNOUNCE_ADVANCEMENTS);
		int startSize = config.get(MainConfig.BORDER_START_SIZE);

		World overworld = getUhcWorld(Environment.NORMAL);
		prepareWorld(overworld, difficulty, healthRegen, announceAdvancements, startSize*2);

		VersionUtils.getVersionUtils().setGameRuleValue(overworld, DO_DAYLIGHT_CYCLE, false);
		VersionUtils.getVersionUtils().setGameRuleValue(overworld, DO_MOB_SPAWNING, false);

		overworld.setTime(6000);
		overworld.setWeatherDuration(999999999);

		if (config.get(MainConfig.ENABLE_NETHER)){
			World nether = getUhcWorld(Environment.NETHER);
			prepareWorld(nether, difficulty, healthRegen, announceAdvancements, startSize);
		}

		if (config.get(MainConfig.ENABLE_THE_END)){
			World theEnd = getUhcWorld(Environment.THE_END);
			prepareWorld(theEnd, difficulty, healthRegen, announceAdvancements, startSize*2);
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
			undergoundNether.build(config, getUhcWorld(Environment.NORMAL));
		}
	}

	private void prepareWorld(World world, Difficulty difficulty, boolean healthRegen, boolean announceAdvancements, int borderSize) {
		world.save();
		if (!healthRegen){
			VersionUtils.getVersionUtils().setGameRuleValue(world, NATURAL_REGENERATION, false);
		}
		if (!announceAdvancements && UhcCore.getVersion() >= 12){
			VersionUtils.getVersionUtils().setGameRuleValue(world, ANNOUNCE_ADVANCEMENTS, false);
		}
		VersionUtils.getVersionUtils().setGameRuleValue(world, COMMAND_BLOCK_OUTPUT, false);
		VersionUtils.getVersionUtils().setGameRuleValue(world, LOG_ADMIN_COMMANDS, false);
		VersionUtils.getVersionUtils().setGameRuleValue(world, SEND_COMMAND_FEEDBACK, false);
		world.setDifficulty(difficulty);

		setBorderSize(world, 0, 0, borderSize);
	}

	public void setBorderSize(World world, int x, int z, double size) {
		WorldBorder worldborder = world.getWorldBorder();
		worldborder.setCenter(x, z);
		worldborder.setSize(size);
	}
	
	private void copyWorld(String randomWorldName, String worldName) {
		Bukkit.getLogger().info("[UhcCore] Copying " + randomWorldName + " to " + worldName);
		File worldDir = new File(randomWorldName);
		if(worldDir.exists() && worldDir.isDirectory()){
			recursiveCopy(worldDir,new File(worldName));
		}
	}

	private void deleteOldPlayersFiles() {
		if (Bukkit.getServer().getWorlds().isEmpty()) {
			return;
		}

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
	
	private void recursiveCopy(File fSource, File fDest) {
	     try {
	          if (fSource.isDirectory()) {
	          // A simple validation, if the destination is not exist then create it
	               if (!fDest.exists()) {
	                    fDest.mkdirs();
	               }
	 
	               // Create list of files and directories on the current source
	               // Note: with the recursion 'fSource' changed accordingly
	               String[] fList = fSource.list();

				  for (String s : fList) {
					  File dest = new File(fDest, s);
					  File source = new File(fSource, s);

					  // Recursion call take place here
					  recursiveCopy(source, dest);
				  }
	          }
	          else {
	               // Found a file. Copy it into the destination, which is already created in 'if' condition above
	 
	               // Open a file for read and write (copy)
	               FileInputStream fInStream = new FileInputStream(fSource);
	               FileOutputStream fOutStream = new FileOutputStream(fDest);
	 
	               // Read 2K at a time from the file
	               byte[] buffer = new byte[2048];
	               int iBytesReads;
	 
	               // In each successful read, write back to the source
	               while ((iBytesReads = fInStream.read(buffer)) >= 0) {
	                    fOutStream.write(buffer, 0, iBytesReads);
	               }
	 
	               // Safe exit
	               if (fInStream != null) {
	                    fInStream.close();
	               }
	 
	               if (fOutStream != null) {
	                    fOutStream.close();
	               }
	          }
	     }
	     catch (Exception ex) {
	          // Please handle all the relevant exceptions here
	     }
	}

	public void generateChunks(Environment env){
		World world = getUhcWorld(env);
		int size = config.get(MainConfig.BORDER_START_SIZE);

		if(env == Environment.NETHER){
			size = size/2;
		}

    	int restEveryNumOfChunks = config.get(MainConfig.REST_EVERY_NUM_OF_CHUNKS);
    	int restDuration = config.get(MainConfig.REST_DURATION);

    	boolean generateVeins = config.get(MainConfig.ENABLE_GENERATE_VEINS);
		VeinGenerator veinGenerator = new VeinGenerator(config.get(MainConfig.GENERATE_VEINS));

		ChunkLoaderThread chunkLoaderThread = new ChunkLoaderThread(world, size, restEveryNumOfChunks, restDuration) {
			@Override
			public void onDoneLoadingWorld() {
				Bukkit.getLogger().info("[UhcCore] Environment "+env.toString()+" 100% loaded");
				if(env.equals(Environment.NORMAL) && config.get(MainConfig.ENABLE_NETHER)) {
					generateChunks(Environment.NETHER);
				}else {
					GameManager.getGameManager().startWaitingPlayers();
				}
			}

			@Override
			public void onDoneLoadingChunk(Chunk chunk) {
				if(generateVeins && env.equals(Environment.NORMAL)){
					veinGenerator.generateVeinsInChunk(chunk);
				}
			}
		};

		chunkLoaderThread.printSettings();

		if (PaperLib.isPaper() && PaperLib.getMinecraftVersion() >= 13){
			Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), chunkLoaderThread);
		}else {
			Bukkit.getScheduler().runTask(UhcCore.getPlugin(), chunkLoaderThread);
		}
	}

}