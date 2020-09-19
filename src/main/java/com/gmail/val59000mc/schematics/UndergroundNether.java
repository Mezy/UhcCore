package com.gmail.val59000mc.schematics;

import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;

public class UndergroundNether {

	private final GameManager gameManager;
	private final int minOccurrences;
	private final int maxOccurrences;
	private boolean enable;
	private File netherSchematic;
	
	public UndergroundNether(GameManager gameManager){
		this.gameManager = gameManager;

		MainConfiguration cfg = gameManager.getConfiguration();
		
		this.enable = cfg.getEnableUndergroundNether();
		this.minOccurrences = cfg.getMinOccurrencesUndergroundNether();
		this.maxOccurrences = cfg.getMaxOccurrencesUndergroundNether();
		checkIfSchematicCanBePasted(); 
	}
	
	private void checkIfSchematicCanBePasted() {
		if(gameManager.getConfiguration().getWorldEditLoaded()){
			netherSchematic = SchematicHandler.getSchematicFile("nether");
        	if(!netherSchematic.exists()){
        		if(enable){
            		enable = false;
        			Bukkit.getLogger().severe("[UhcCore] Nether schematic not found in 'plugins/UhcCore/nether.schematic'. There will be no underground nether");
        		}        		
        	}
		}else{
			enable = false;
		}
	}

	public void build(){
		if(enable){
			MainConfiguration cfg = gameManager.getConfiguration();
			
			int occurrences = RandomUtils.randomInteger(minOccurrences, maxOccurrences);
			int worldSize = gameManager.getWorldBorder().getStartSize();
			World overworld = Bukkit.getWorld(cfg.getOverworldUuid());
			
			for(int i = 1; i <= occurrences ; i++){

				int randX = RandomUtils.randomInteger(-worldSize, worldSize);
				int randZ = RandomUtils.randomInteger(-worldSize, worldSize);
				Location randLoc = new Location(overworld,randX,cfg.getNetherPasteAtY(),randZ);
				
				try {
					// to do find loc
					SchematicHandler.pasteSchematic(randLoc, netherSchematic, 0);
				} catch (Exception e) {
					Bukkit.getLogger().severe("[UhcCore] Couldn't paste nether schematic at "+
							randLoc.getBlockX()+" "+randLoc.getBlockY()+" "+randLoc.getBlockZ());
					e.printStackTrace();
				}
			}
			
		}  
		
	}
	
}
