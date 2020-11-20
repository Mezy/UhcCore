package com.gmail.val59000mc.maploader;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.UhcWorldBorder;
import com.gmail.val59000mc.threads.ChunkLoaderThread;
import com.gmail.val59000mc.utils.FileUtils;
import com.gmail.val59000mc.configuration.YamlFile;
import io.papermc.lib.PaperLib;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

public class MapLoader {

	private long mapSeed;
	private String mapName;

	public MapLoader(){
		mapSeed = -1;
		mapName = null;
	}
	
	public void deleteLastWorld(String uuid){
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

		if(gm.getConfiguration().getPickRandomSeedFromList() && !gm.getConfiguration().getSeeds().isEmpty()){
			if (mapSeed == -1) {
				Random r = new Random();
				mapSeed = gm.getConfiguration().getSeeds().get(r.nextInt(gm.getConfiguration().getSeeds().size()));
				Bukkit.getLogger().info("[UhcCore] Picking random seed from list : "+mapName);
			}
			wc.seed(mapSeed);
		}else if(gm.getConfiguration().getPickRandomWorldFromList() && !gm.getConfiguration().getWorldsList().isEmpty()){
			if (mapName == null) {
				Random r = new Random();
				mapName = gm.getConfiguration().getWorldsList().get(r.nextInt(gm.getConfiguration().getWorldsList().size()));
			}

			String copyWorld = mapName;
			if (env != Environment.NORMAL){
				copyWorld = copyWorld + "_" + env.name().toLowerCase();
			}

			copyWorld(copyWorld, worldName);
		}

		if(env.equals(Environment.NORMAL)){
			gm.getConfiguration().setOverworldUuid(worldName);
		}else if (env == Environment.NETHER){
			gm.getConfiguration().setNetherUuid(worldName);
		}else {
			gm.getConfiguration().setTheEndUuid(worldName);
		}

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
	
	public void loadOldWorld(String uuid, Environment env){
		
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

		World world;
		int size;
		if(env.equals(Environment.NORMAL)){
			world = Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getOverworldUuid());
			size = border.getStartSize();
		}else {
			world = Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getNetherUuid());
			size = border.getStartSize()/2;
		}

    	int restEveryNumOfChunks = gm.getConfiguration().getRestEveryNumOfChunks();
    	int restDuration = gm.getConfiguration().getRestDuration();

    	VeinGenerator veinGenerator = new VeinGenerator(gm.getConfiguration().getGenerateVeins());
    	boolean generateVeins = gm.getConfiguration().getEnableGenerateVein();

		ChunkLoaderThread chunkLoaderThread = new ChunkLoaderThread(world, size, restEveryNumOfChunks, restDuration) {
			@Override
			public void onDoneLoadingWorld() {
				Bukkit.getLogger().info("[UhcCore] Environment "+env.toString()+" 100% loaded");
				if(env.equals(Environment.NORMAL) && gm.getConfiguration().getEnableNether()) {
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

		if (PaperLib.isPaper()){
			Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), chunkLoaderThread);
		}else {
			Bukkit.getScheduler().runTask(UhcCore.getPlugin(), chunkLoaderThread);
		}
	}

}