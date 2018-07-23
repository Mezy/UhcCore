package com.gmail.val59000mc.playuhc.mc1_8.customitems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemFactory {
	
	public static ItemStack createPlayerSkull(String name){
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta im = (SkullMeta) item.getItemMeta();
		im.setOwner(name);
		item.setItemMeta(im);
		return item;
	}
	
}
