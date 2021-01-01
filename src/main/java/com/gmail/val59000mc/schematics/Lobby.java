package com.gmail.val59000mc.schematics;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Lobby extends Schematic {

	private static final String SCHEMATIC_NAME = "lobby";

	private int width, length, height;

	public Lobby(Location loc){
		super(SCHEMATIC_NAME, loc);

		// Dimensions for glass box
		width = 10;
		length = 10;
		height = 3; 
	}

	@Override
	public void build(){
		// Paste schematic
		if (canBePasted()){
			super.build();

			height = getHeight();
			length = getLength();
			width = getWidth();
		}
		// Build glass box
		else {
			int x = getLocation().getBlockX(), y=getLocation().getBlockY()+2, z=getLocation().getBlockZ();
			World world = getLocation().getWorld();
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
							world.getBlockAt(x+i,y+j,z+k).setType(Material.GLASS);
						}else{
							world.getBlockAt(x+i,y+j,z+k).setType(Material.AIR);
						}
					}
				}
			}
		}
	}
	
	public void destroyBoundingBox(){
		int lobbyX = getLocation().getBlockX(), lobbyY = getLocation().getBlockY()+2, lobbyZ = getLocation().getBlockZ();

		World world = getLocation().getWorld();
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
