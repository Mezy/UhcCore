package com.gmail.val59000mc.maploader;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.UhcWorldBorder;
import com.gmail.val59000mc.threads.ChunkLoaderThread;
import com.gmail.val59000mc.utils.FileUtils;
import com.gmail.val59000mc.configuration.YamlFile;
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

	private Map<Environment, String> worldUuids;

	private long mapSeed;
	private String mapName;

	public MapLoader(){
		worldUuids = new HashMap<>();
		mapSeed = -1;
		mapName = null;
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
			storage = FileUtils.saveResourceIfNotAvailable("storage.yml");
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

	public void loadWorldUuids(YamlFile storage){
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
	
	private void copyWorld(String randomWorldName, String worldName) {
		Bukkit.getLogger().info("[UhcCore] Copying " + randomWorldName + " to " + worldName);
		File worldDir = new File(randomWorldName);
		if(worldDir.exists() && worldDir.isDirectory()){
			recursiveCopy(worldDir,new File(worldName));
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
		GameManager gm = GameManager.getGameManager();
		UhcWorldBorder border = gm.getWorldBorder();

		World world = getUhcWorld(env);
		int size = border.getStartSize();

		if(env == Environment.NETHER){
			size = size/2;
		}

    	int restEveryNumOfChunks = gm.getConfig().get(MainConfig.REST_EVERY_NUM_OF_CHUNKS);
    	int restDuration = gm.getConfig().get(MainConfig.REST_DURATION);

    	VeinGenerator veinGenerator = new VeinGenerator(gm.getConfig().getGenerateVeins());
    	boolean generateVeins = gm.getConfig().get(MainConfig.ENABLE_GENERATE_VEINS);

		ChunkLoaderThread chunkLoaderThread = new ChunkLoaderThread(world, size, restEveryNumOfChunks, restDuration) {
			@Override
			public void onDoneLoadingWorld() {
				Bukkit.getLogger().info("[UhcCore] Environment "+env.toString()+" 100% loaded");
				if(env.equals(Environment.NORMAL) && gm.getConfig().get(MainConfig.ENABLE_NETHER)) {
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