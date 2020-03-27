package com.gmail.val59000mc.scenarios.scenariolisteners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.RandomUtils;

public class RandomItemsListener extends ScenarioListener{
	
	private List<Material> materialitems;
	private Map<Material, ItemStack> dropList; 
	
	public RandomItemsListener() {
		
		materialitems = new ArrayList<>();
		dropList = new HashMap<Material, ItemStack>();
	}

	@Override
	public void onEnable() {
		//Create new arraylist of materials that are all items
	for(Material material : Material.values()) {
		if(material.isItem()) {
			 materialitems.add(material);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		
		//Create new HashMap so each each type of broken block drops the same random item every time it is broken (configurable
		Block block = event.getBlock();
		int itemindex  = RandomUtils.randomInteger(1, materialitems.size())-1;
		
		ItemStack randomdrops = new ItemStack(materialitems.get(itemindex), 1);
		
		ItemStack itemdrops = dropList.get(block.getType());
		
		if(dropList.containsKey(block.getType())) {
			block.setType(Material.AIR);
			block.getWorld().dropItemNaturally(block.getLocation().add(0.5,0,0.5), itemdrops);
			event.setCancelled(true);
		}
		else {
			dropList.put(block.getType(), randomdrops);
			materialitems.remove(itemindex);
			block.setType(Material.AIR);
			block.getWorld().dropItemNaturally(block.getLocation().add(0.5,0,0.5), randomdrops);
			event.setCancelled(true);
		}
	}
}
