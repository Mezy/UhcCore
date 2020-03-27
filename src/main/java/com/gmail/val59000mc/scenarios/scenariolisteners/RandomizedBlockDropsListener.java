package com.gmail.val59000mc.scenarios.scenariolisteners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.RandomUtils;

public class RandomizedBlockDropsListener extends ScenarioListener{
	
	private List<Material> materialitems;
	private HashMap<Material, ItemStack> dropList; 
	
	public RandomizedBlockDropsListener() {
		
		materialitems = new ArrayList<>();
		dropList = new HashMap<Material, ItemStack>();
	}

	@Override
	public void onEnable() {
		//Create new arraylist of materials that are all items
	for(int i = 0; i< Material.values().length; i++) {
		if(Material.values()[i].isItem()) {
			 materialitems.add(Material.values()[i]);
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		
		//Create new HashMap so each each type of broken block drops the same random item every time it is broken (configurable
		Block block = event.getBlock();
		int itemindex  = RandomUtils.randomInteger(1, materialitems.size())-1;
		
		ItemStack randomdrops = new ItemStack(materialitems.get(itemindex), 1);
		
		ItemStack itemdrops = dropList.get(block.getType());
		
		System.out.println(dropList);
		if(dropList.containsKey(block.getType())) {
			block.setType(Material.AIR);
			block.getWorld().dropItemNaturally(block.getLocation(), itemdrops);
			event.setCancelled(true);
		}
		else {
			dropList.put(block.getType(), randomdrops);
			block.setType(Material.AIR);
			block.getWorld().dropItemNaturally(block.getLocation(), randomdrops);
			event.setCancelled(true);
		}
	}
}


