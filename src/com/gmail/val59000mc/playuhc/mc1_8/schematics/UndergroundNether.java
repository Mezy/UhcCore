package com.gmail.val59000mc.playuhc.mc1_8.schematics;

import com.gmail.val59000mc.playuhc.mc1_8.configuration.MainConfiguration;
import com.gmail.val59000mc.playuhc.mc1_8.game.GameManager;
import com.gmail.val59000mc.playuhc.mc1_8.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;

public class UndergroundNether {
	private int minOccurrences;
	private int maxOccurrences;
	private boolean enable;
	
	public UndergroundNether(){
		MainConfiguration cfg = GameManager.getGameManager().getConfiguration();
		
		this.enable = cfg.getEnableUndergroundNether();
		this.minOccurrences = cfg.getMinOccurrencesUndergroundNether();
		this.maxOccurrences = cfg.getMaxOccurrencesUndergroundNether();
		checkIfSchematicCanBePasted(); 
	}
	
	private void checkIfSchematicCanBePasted() {
		if(GameManager.getGameManager().getConfiguration().getWorldEditLoaded()){
			File arenaSchematic = new File("plugins/PlayUHC/nether.schematic");
        	if(!arenaSchematic.exists()){
        		if(enable){
            		enable = false;
        			Bukkit.getLogger().severe("[PlayUHC] Nether schematic not found in 'plugins/PlayUHC/nether.schematic'. There will be no underground nether");
        		}        		
        	}
		}else{
			enable = false;
		}
	}

	public void build(){
		if(enable){
			MainConfiguration cfg = GameManager.getGameManager().getConfiguration();
			
			int occurrences = RandomUtils.randomInteger(minOccurrences, maxOccurrences);
			int worldSize = GameManager.getGameManager().getWorldBorder().getStartSize();
			World overworld = Bukkit.getWorld(cfg.getOverworldUuid());
			
			for(int i = 1; i <= occurrences ; i++){

				int randX = RandomUtils.randomInteger(-worldSize, worldSize);
				int randZ = RandomUtils.randomInteger(-worldSize, worldSize);
				Location randLoc = new Location(overworld,randX,cfg.getNetherPasteAtY(),randZ);
				
				try {
					// to do find loc
					SchematicHandler.pasteSchematic(randLoc,"plugins/PlayUHC/nether.schematic");
				} catch (Exception e) {
					Bukkit.getLogger().severe("[PlayUHC] Coulnd't paste nether schematic at "+
							randLoc.getBlockX()+" "+randLoc.getBlockY()+" "+randLoc.getBlockZ());
				}
			}
			
		}  
		
	}
	
}
