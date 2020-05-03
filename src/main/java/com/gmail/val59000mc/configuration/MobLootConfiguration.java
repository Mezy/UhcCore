package com.gmail.val59000mc.configuration;

import com.gmail.val59000mc.exceptions.ParseException;
import com.gmail.val59000mc.utils.JsonItemUtils;
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
		if (section == null){
			return false;
		}

		try{
			entity = EntityType.valueOf(section.getName());
		}catch(IllegalArgumentException e){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse section '"+section.getName()+"' in mob-loot. This is not an existing entity type. Ignoring it.");
			return false;
		}
		
		String itemStr = section.getString("loot");

		if (itemStr == null){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse section '"+section.getName()+"' in mob-loot. Missing loot item.");
			return false;
		}

		if (itemStr.startsWith("{") && itemStr.endsWith("}")){
			try {
				loot = JsonItemUtils.getItemFromJson(itemStr);
			}catch (ParseException ex){
				Bukkit.getLogger().warning("[UhcCore] Couldn't parse loot '"+entity.toString()+"' in mob-loot.");
				ex.printStackTrace();
				return false;
			}
		}else{
			// TODO: Remove in future update
			try{
				String[] itemArr = itemStr.split("/");
				loot = new ItemStack(Material.valueOf(itemArr[0]), Integer.parseInt(itemArr[1]), Short.parseShort(itemArr[2]));
				Bukkit.getLogger().warning("[UhcCore] Using old mob-loot syntax, please update loot to json item.");
			}catch(Exception e){
				Bukkit.getLogger().warning("[UhcCore] Couldn't parse loot '"+entity.toString()+"' in mob-loot.");
				return false;
			}
		}
		
		addXp = section.getInt("add-xp",0);
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