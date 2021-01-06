package com.gmail.val59000mc.schematics;

import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class UndergroundNether extends Schematic {

	private static final String SCHEMATIC_NAME = "nether";
	
	public UndergroundNether(){
		super(SCHEMATIC_NAME);
	}

	public void build(MainConfig cfg, World world){
		if (!canBePasted()){
			Bukkit.getLogger().severe("[UhcCore] Worldedit not installed or nether schematic not found in 'plugins/UhcCore/nether.schematic'. There will be no underground nether");
			return;
		}

		int occurrences = RandomUtils.randomInteger(cfg.get(MainConfig.MIN_OCCURRENCES_UNDERGROUND_NETHER), cfg.get(MainConfig.MAX_OCCURRENCES_UNDERGROUND_NETHER));
		int worldSize = cfg.get(MainConfig.BORDER_START_SIZE);

		for(int i = 1; i <= occurrences ; i++){

			int randX = RandomUtils.randomInteger(-worldSize, worldSize);
			int randZ = RandomUtils.randomInteger(-worldSize, worldSize);
			Location randLoc = new Location(world, randX, cfg.get(MainConfig.NETHER_PASTE_AT_Y), randZ);

			try {
				// to do find loc
				build(randLoc);
			} catch (Exception e) {
				Bukkit.getLogger().severe("[UhcCore] Couldn't paste nether schematic at "+
						randLoc.getBlockX()+" "+randLoc.getBlockY()+" "+randLoc.getBlockZ());
				e.printStackTrace();
			}
		}
	}
	
}
