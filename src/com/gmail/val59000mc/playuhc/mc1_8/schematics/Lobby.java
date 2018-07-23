package com.gmail.val59000mc.playuhc.mc1_8.schematics;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.gmail.val59000mc.playuhc.mc1_8.game.GameManager;

public class Lobby {
	private Location loc;
	private Material block;
	private boolean built;
	private boolean useSchematic;
	protected static int width, length, height; 
	
	public Lobby(Location loc, Material block){
		this.loc = loc;
		this.block = block;
		this.built = false;
		this.useSchematic = false;
		loc.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		checkIfSchematicCanBePasted();
		
		width = 10;
		length = 10;
		height = 3; 
	}
	
	private void checkIfSchematicCanBePasted() {
		if(GameManager.getGameManager().getConfiguration().getWorldEditLoaded()){
			File lobbySchematic = new File("plugins/PlayUHC/lobby.schematic");
        	if(lobbySchematic.exists()){
        		useSchematic = true;
        	}
		}else{
			useSchematic = false;
		}
		
	}

	public void build(){
		if(!built && useSchematic){
			
			ArrayList<Integer> dimensions;
			try {
				dimensions = SchematicHandler.pasteSchematic(loc,"plugins/PlayUHC/lobby.schematic");
				Lobby.height = dimensions.get(0);
				Lobby.length = dimensions.get(1);
				Lobby.width = dimensions.get(2);
				built = true;
			} catch (Exception e) {
				Bukkit.getLogger().severe("An error ocurred while pasting the lobby");
				built = false;
			}
		}
				
		if(!built){
				int x = loc.getBlockX(), y=loc.getBlockY(), z=loc.getBlockZ();
				World world = loc.getWorld();
				for(int i = -width; i <= width; i++){
					for(int j = -height; j <= height; j++){
						for(int k = -length ; k <= length ; k++){
							if(    i == -10 
								|| i == 10
								|| j == -3
								|| j == 3
								|| k == -10
								|| k == 10
							  ){
								world.getBlockAt(x+i,y+j,z+k).setType(block);
							}else{
								world.getBlockAt(x+i,y+j,z+k).setType(Material.AIR);
							}
						}
					}
				}

				built = true;
			}
	}
	
	public void destroyBoundingBox(){
		if(built){
			int x = loc.getBlockX(), y=loc.getBlockY(), z=loc.getBlockZ();
			World world = loc.getWorld();
			for(int i = -width; i <= width; i++){
				for(int j = -height; j <= height; j++){
					for(int k = -length ; k <= length ; k++){
						Block b = world.getBlockAt(x+i,y+j,z+k);
						if(!b.getType().equals(Material.AIR))
							world.getBlockAt(x+i,y+j,z+k).setType(Material.AIR);
					}
				}
			}
		}
	}
	
	public void loadLobbyChunks(){
		World world = getLoc().getWorld();
		world.loadChunk(getLoc().getChunk());
	}

	public Location getLoc() {
		return loc;
	}

	public boolean isBuilt() {
		return built;
	}
}
