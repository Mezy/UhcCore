package com.gmail.val59000mc.customitems;

import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemFactory {
	
	public static ItemStack createPlayerSkull(String name){
		ItemStack item = UniversalMaterial.PLAYER_HEAD.getStack();
		SkullMeta im = (SkullMeta) item.getItemMeta();
		im.setOwner(name);
		item.setItemMeta(im);
		return item;
	}
	
}