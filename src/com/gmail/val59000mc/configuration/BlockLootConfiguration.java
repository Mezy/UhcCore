package com.gmail.val59000mc.configuration;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class BlockLootConfiguration {
	private Material material;
	private ItemStack loot;
	private int addXp;
	
	
	public BlockLootConfiguration() {
		this.material = Material.AIR;
		this.loot = new ItemStack(material);
		this.addXp = 0;
	}
	
	public boolean parseConfiguration(ConfigurationSection section){
		try{
			this.material = Material.valueOf(section.getName());
		}catch(IllegalArgumentException e){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse section '"+section.getName()+"' in block-loot. This is not an existing block type. Ignoring it.");
			return false;
		}
		
		String itemStr = section.getString("loot");
		try{
			String[] itemArr = itemStr.split("/");
			this.loot = new ItemStack(Material.valueOf(itemArr[0]), Integer.parseInt(itemArr[1]), Short.parseShort(itemArr[2]));
		}catch(Exception e){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse loot '"+material.toString()+"' in block-loot. The syntax must be 'MATERIAL/QUANTITY/DAMAGE'.Ignoring it.");
			return false;
		}
		
		this.addXp = section.getInt("add-xp",0);
		
		return true;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public ItemStack getLoot() {
		return loot;
	}
	
	public int getAddXp() {
		return addXp;
	}
}
