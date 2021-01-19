package com.gmail.val59000mc.scenarios.scenariolisteners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.RandomUtils;

public class RandomizedDropsListener extends ScenarioListener{

	private List<Material> items;
	private final Map<Material, ItemStack> dropList;
	
	public RandomizedDropsListener() {
		dropList = new HashMap<>();
	}

	@Override
	public void onEnable() {
		items = VersionUtils.getVersionUtils().getItemList();
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		//Create new HashMap so each each type of broken block drops the same random item every time it is broken (configurable
		Block block = event.getBlock();
		
		ItemStack blockDrop;
		if(dropList.containsKey(block.getType())) {
			 blockDrop = dropList.get(block.getType());
		}
		else {
			int itemindex  = RandomUtils.randomInteger(1, items.size())-1;
			Material material = items.get(itemindex);

			 blockDrop = new ItemStack(material);
			dropList.put(block.getType(), blockDrop);

			items.remove(material);
		}

		event.setCancelled(true);
		block.setType(Material.AIR);
		Location dropLocation = block.getLocation().add(.5, 0, .5);
		dropLocation.getWorld().dropItemNaturally(dropLocation, blockDrop);

		Player player = event.getPlayer();
		ItemStack tool = player.getItemInHand();

		if (tool != null && tool.hasItemMeta() && tool.getDurability() > 1) {
			tool.setDurability((short) (tool.getDurability()-1));
			player.setItemInHand(tool);
		}
	}

}
