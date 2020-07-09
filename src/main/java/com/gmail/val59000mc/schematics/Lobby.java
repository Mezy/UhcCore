package com.gmail.val59000mc.schematics;

import com.gmail.val59000mc.game.GameManager;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Lobby {
	private Location loc;
	private Material block;
	private File lobbySchematic;
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
			lobbySchematic = SchematicHandler.getSchematicFile("lobby");
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
				dimensions = SchematicHandler.pasteSchematic(loc, lobbySchematic, 0);
				Lobby.height = dimensions.get(0);
				Lobby.length = dimensions.get(1);
				Lobby.width = dimensions.get(2);
				built = true;
			} catch (Exception e) {
				Bukkit.getLogger().severe("[UhcCore] An error ocurred while pasting the lobby");
				e.printStackTrace();
				built = false;
			}
		}
				
		if(!built){
				int x = loc.getBlockX(), y=loc.getBlockY()+2, z=loc.getBlockZ();
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
			int lobbyX = loc.getBlockX(), lobbyY = loc.getBlockY()+2, lobbyZ = loc.getBlockZ();
			
			World world = loc.getWorld();
			for(int x = -width; x <= width; x++){
				for(int y = height; y >= -height; y--){
					for(int z = -length ; z <= length ; z++){
						Block block = world.getBlockAt(lobbyX+x,lobbyY+y,lobbyZ+z);
						if(!block.getType().equals(Material.AIR)){
							block.setType(Material.AIR);
						}
					}
				}
			}
		}
	}
	
	public void loadLobbyChunks() throws ExecutionException, InterruptedException {
		World world = getLoc().getWorld();
		PaperLib.getChunkAtAsync(world, PaperLib.getChunkAtAsync(getLoc()).get().getX(), PaperLib.getChunkAtAsync(getLoc()).get().getX());
	}

	public Location getLoc() {
		return loc;
	}

	public boolean isBuilt() {
		return built;
	}
}
