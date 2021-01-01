package com.gmail.val59000mc.schematics;

import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class UndergroundNether extends Schematic {

	private static final String SCHEMATIC_NAME = "nether";
	
	public UndergroundNether(){
		super(SCHEMATIC_NAME);
	}

	public void build(MainConfiguration cfg, World world){
		if (!canBePasted()){
			Bukkit.getLogger().severe("[UhcCore] Worldedit not installed or nether schematic not found in 'plugins/UhcCore/nether.schematic'. There will be no underground nether");
			return;
		}

		int occurrences = RandomUtils.randomInteger(cfg.getMinOccurrencesUndergroundNether(), cfg.getMaxOccurrencesUndergroundNether());
		int worldSize = cfg.getBorderStartSize();

		for(int i = 1; i <= occurrences ; i++){

			int randX = RandomUtils.randomInteger(-worldSize, worldSize);
			int randZ = RandomUtils.randomInteger(-worldSize, worldSize);
			Location randLoc = new Location(world, randX, cfg.getNetherPasteAtY(), randZ);

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
