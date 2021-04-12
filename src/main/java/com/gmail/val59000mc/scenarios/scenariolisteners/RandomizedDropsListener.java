package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.RandomUtils;
import com.gmail.val59000mc.utils.UniversalMaterial;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomizedDropsListener extends ScenarioListener {

	private List<Material> items;
	private final Map<Material, ItemStack> dropList;

	public RandomizedDropsListener() {
		dropList = new HashMap<>();
	}

	private static final UniversalMaterial[] FLOWERS = new UniversalMaterial[]{
			UniversalMaterial.POPPY,
			UniversalMaterial.BLUE_ORCHID,
			UniversalMaterial.ALLIUM,
			UniversalMaterial.AZURE_BLUET,
			UniversalMaterial.RED_TULIP,
			UniversalMaterial.ORANGE_TULIP,
			UniversalMaterial.WHITE_TULIP,
			UniversalMaterial.PINK_TULIP,
			UniversalMaterial.OXEYE_DAISY,
			UniversalMaterial.SUNFLOWER,
			UniversalMaterial.LILAC,
			UniversalMaterial.ROSE_BUSH,
			UniversalMaterial.PEONY,
			UniversalMaterial.DEAD_BUSH,
			UniversalMaterial.DANDELION
	};

	@Override
	public void onEnable() {
		items = VersionUtils.getVersionUtils().getItemList();
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		//Create new HashMap so each each type of broken block drops the same random item every time it is broken (configurable
		Block block = event.getBlock();
		//to allow flower power to work while playing with randomized drops
		if (getScenarioManager().isEnabled(Scenario.FLOWER_POWER) && isFlower(block)) return;
		//Fixes duping exploit when breaking bisected blocks from top
		event.getBlock().getDrops().clear();
		ItemStack blockDrop;
		if (dropList.containsKey(block.getType())) {
			blockDrop = dropList.get(block.getType());
		} else {
			int itemindex = RandomUtils.randomInteger(1, items.size()) - 1;
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
			tool.setDurability((short) (tool.getDurability() - 1));
			player.setItemInHand(tool);
		}
	}

	private boolean isFlower(Block block) {
		for (UniversalMaterial flower : FLOWERS) {
			if (flower.equals(block)) return true;
		}

		if (UhcCore.getVersion() >= 14) {
			String material = block.getType().toString();
			return material.equals("LILY_OF_THE_VALLEY") || material.equals("CORNFLOWER");
		}
		return false;
	}
}
