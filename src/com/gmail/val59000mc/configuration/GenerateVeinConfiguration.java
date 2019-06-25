package com.gmail.val59000mc.configuration;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class GenerateVeinConfiguration {

	private Material material;
	private int minVeinsPerChunk;
	private int maxVeinsPerChunk;
	private int minBlocksPerVein;
	private int maxBlocksPerVein;
	private int minY;
	private int maxY;
	
	
	public GenerateVeinConfiguration() {
		this.material = Material.AIR;
		this.minVeinsPerChunk = 0;
		this.maxVeinsPerChunk = 0;
		this.minBlocksPerVein = 0;
		this.maxBlocksPerVein = 0;
		this.minY = 0;
		this.maxY = 0;
	}
	
	public boolean parseConfiguration(ConfigurationSection section){
		try{
			this.material = Material.valueOf(section.getName());
		}catch(IllegalArgumentException e){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse section '"+section.getName()+"' in generate-vein. This is not an existing block type.Ignoring it.");
			return false;
		}
		this.minVeinsPerChunk = section.getInt("min-veins-per-chunk",0);
		this.maxVeinsPerChunk = section.getInt("max-veins-per-chunk",5);
		if(minVeinsPerChunk < 0 || maxVeinsPerChunk < 0){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse section '"+section.getName()+"' in generate-vein. min and max-veins-per-chunk must be positive.");
			return false;
		}
		
		this.minBlocksPerVein = section.getInt("min-blocks-per-vein",5);
		this.maxBlocksPerVein = section.getInt("max-blocks-per-vein",10);
		if(minBlocksPerVein < 0 || maxBlocksPerVein < 0){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse section '"+section.getName()+"' in generate-vein. min and max-blocks-per-vein must be positive.");
			return false;
		}
		
		this.minY = section.getInt("min-y",0);
		this.maxY = section.getInt("max-y",65);
		if(minY < 0 || minY > 255 || maxY < 0 || maxY > 255){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse section '"+section.getName()+"' in generate-vein. The min and max Y must be between 0 and 255.");
			return false;
		}
		
		return true;
	}
	
	public Material getMaterial() {
		return material;
	}
	public int getMinVeinsPerChunk() {
		return minVeinsPerChunk;
	}
	public int getMaxVeinsPerChunk() {
		return maxVeinsPerChunk;
	}
	public int getMinBlocksPerVein() {
		return minBlocksPerVein;
	}
	public int getMaxBlocksPerVein() {
		return maxBlocksPerVein;
	}
	public int getMinY() {
		return minY;
	}
	public int getMaxY() {
		return maxY;
	}
	
	
}
