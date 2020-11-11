package com.gmail.val59000mc.schematics;

import com.gmail.val59000mc.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.File;
import java.util.ArrayList;

public class Lobby {

	private final Location loc;
	
	public Lobby(Location loc){
		this.loc = loc;
	}

	@Deprecated
	public void build(){

	}
	@Deprecated
	public void destroyBoundingBox(){

	}
	
	public void loadLobbyChunks(){
		World world = getLoc().getWorld();
		world.loadChunk(getLoc().getChunk());
	}

	public Location getLoc() {
		return loc;
	}
	@Deprecated
	public boolean isBuilt() {
		return false;
	}
}
