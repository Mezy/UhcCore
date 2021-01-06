package com.gmail.val59000mc.configuration;

import com.gmail.val59000mc.exceptions.ParseException;
import com.gmail.val59000mc.utils.JsonItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class LootConfiguration<T extends Enum<T>> {

	private final Class<T> classType;

	private T type;
	private ItemStack loot;
	private int addXp;

	public LootConfiguration(Class<T> classType) {
		this.classType = classType;
		this.type = null;
		this.loot = new ItemStack(Material.AIR);
		this.addXp = 0;
	}
	
	public boolean parseConfiguration(ConfigurationSection section){
		if (section == null){
			return false;
		}

		try{
			type = Enum.valueOf(classType, section.getName());
		}catch(IllegalArgumentException e){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse section '"+section.getName()+"' in mob-loot. This is not an existing entity type. Ignoring it.");
			return false;
		}
		
		String itemStr = section.getString("loot");

		if (itemStr == null){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse section '"+section.getName()+"' in mob-loot. Missing loot item.");
			return false;
		}

		try {
			loot = JsonItemUtils.getItemFromJson(itemStr);
		}catch (ParseException ex){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse loot '"+type.name()+"' in mob-loot.");
			ex.printStackTrace();
			return false;
		}
		
		addXp = section.getInt("add-xp",0);
		return true;
	}
	
	public T getType() {
		return type;
	}
	
	public ItemStack getLoot() {
		return loot;
	}
	
	public int getAddXp() {
		return addXp;
	}

}