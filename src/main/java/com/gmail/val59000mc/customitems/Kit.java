package com.gmail.val59000mc.customitems;

import com.gmail.val59000mc.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Kit {
	protected String key;
	protected String name;
	protected ItemStack symbol;
	protected List<ItemStack> items;

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public boolean canBeUsedBy(Player player){
		GameManager gm = GameManager.getGameManager();
		return !gm.getConfiguration().getEnableKitsPermissions() || player.hasPermission("uhc-core.kit."+key);
	}

}