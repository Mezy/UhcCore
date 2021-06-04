package com.gmail.val59000mc.schematics;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.configuration.YamlFile;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.utils.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeathmatchArena extends Schematic {

	private final static String SCHEMATIC_NAME = "arena";

	private boolean enable;
	private List<Location> teleportSpots;
	
	public DeathmatchArena(Location location){
		super(SCHEMATIC_NAME, location, 3);

		teleportSpots = new ArrayList<>();
		teleportSpots.add(location);

		enable = canBePasted();
		if (!enable){
			Bukkit.getLogger().info("[UhcCore] No WorldEdit/schematic installed so ending with deathmatch at 0 0");
		}
	}

	@Override
	public void build(){
		if(enable){
			super.build();

			if(isBuild()){
				calculateTeleportSpots();
			}else{
				Bukkit.getLogger().severe("[UhcCore] Deathmatch will be at 0 0 as the arena could not be pasted.");
				enable = false;
			}
		}
	}

	public boolean isUsed() {
		return enable;
	}

	public int getMaxSize() {
		return Math.max(getLength(), getWidth());
	}
	
	public void calculateTeleportSpots(){
		Material spotMaterial = GameManager.getGameManager().getConfig().get(MainConfig.ARENA_TELEPORT_SPOT_BLOCK);
		YamlFile storage;

		try{
			storage = FileUtils.saveResourceIfNotAvailable(UhcCore.getPlugin(), "storage.yml");
		}catch (InvalidConfigurationException ex){
			ex.printStackTrace();
			return;
		}

		long spotsDate = storage.getLong("arena.last-edit", -1);
		String type = storage.getString("arena.type");

		List<Location> spots = new ArrayList<>();
		List<Vector> vectorSpots = new ArrayList<>();
		File schematicFile = getSchematicFile();

		if (spotsDate == schematicFile.lastModified() && spotMaterial.toString().equals(type)){
			Bukkit.getLogger().info("[UhcCore] Loading stored arena teleport spots.");

			vectorSpots = (ArrayList<Vector>) storage.get("arena.locations");

			for (Vector vector : vectorSpots){
				spots.add(vector.toLocation(getLocation().getWorld()));
			}
		}
		else{

			int x = getLocation().getBlockX(),
					y = getLocation().getBlockY(),
					z = getLocation().getBlockZ();

			Bukkit.getLogger().info("[UhcCore] Scanning schematic for arena teleport spots.");

			for (int i = x - getWidth(); i < x + getWidth(); i++) {
				for (int j = y - getHeight(); j < y + getHeight(); j++) {
					for (int k = z - getLength(); k < z + getLength(); k++) {
						Block block = getLocation().getWorld().getBlockAt(i, j, k);
						if (block.getType().equals(spotMaterial) && hasAirOnTop(block)) {
							spots.add(block.getLocation().clone().add(0.5, 1, 0.5));
							vectorSpots.add(block.getLocation().clone().add(0.5, 1, 0.5).toVector());
							Bukkit.getLogger().info("[UhcCore] Arena teleport spot found at " + i + " " + (j + 1) + " " + k);
						}
					}
				}
			}

			storage.set("arena.last-edit", schematicFile.lastModified());
			storage.set("arena.type", spotMaterial.toString());
			storage.set("arena.locations", vectorSpots);
			try {
				storage.save();
			}catch (IOException ex){
				ex.printStackTrace();
			}
		}

		if(spots.isEmpty()){
			Bukkit.getLogger().info("[UhcCore] No Arena teleport spot found, defaulting to schematic origin");
		}else{
			Collections.shuffle(spots);
			teleportSpots = spots;
		}
	}

	private boolean hasAirOnTop(Block block){
		Block up1 = block.getRelative(BlockFace.UP);
		Block up2 = up1.getRelative(BlockFace.UP);
		return up1.getType() == Material.AIR && up2.getType() == Material.AIR;
	}
	
	public List<Location> getTeleportSpots(){
		return teleportSpots;
	}

}
