package com.gmail.val59000mc.configuration;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class MobLootConfiguration {
	
	private EntityType entity;
	private ItemStack loot;
	private int addXp;
	
	
	public MobLootConfiguration() {
		this.entity = EntityType.UNKNOWN;
		this.loot = new ItemStack(Material.AIR);
		this.addXp = 0;
	}
	
	public boolean parseConfiguration(ConfigurationSection section){
		try{
			this.entity = EntityType.valueOf(section.getName());
		}catch(IllegalArgumentException e){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse section '"+section.getName()+"' in mob-loot. This is not an existing entity type. Ignoring it.");
			return false;
		}
		
		String itemStr = section.getString("loot");
		try{
			String[] itemArr = itemStr.split("/");
			this.loot = new ItemStack(Material.valueOf(itemArr[0]), Integer.parseInt(itemArr[1]), Short.parseShort(itemArr[2]));
		}catch(Exception e){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse loot '"+entity.toString()+"' in mob-loot. The syntax must be 'MATERIAL/QUANTITY/DAMAGE'.Ignoring it.");
			return false;
		}
		
		this.addXp = section.getInt("add-xp",0);
		
		return true;
	}
	
	public EntityType getEntityType() {
		return entity;
	}
	
	public ItemStack getLoot() {
		return loot;
	}
	
	public int getAddXp() {
		return addXp;
	}
}
