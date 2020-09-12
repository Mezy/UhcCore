package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.BlockLootConfiguration;
import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.utils.RandomUtils;
import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BlockListener implements Listener{
	
	private Map<Material, BlockLootConfiguration> blockLoots;
	private int maxBuildingHeight;
	
	public BlockListener(){
		MainConfiguration cfg = GameManager.getGameManager().getConfiguration();
		blockLoots = cfg.getEnableBlockLoots() ? cfg.getBlockLoots() : new HashMap<>();
		maxBuildingHeight = cfg.getMaxBuildingHeight();
	}

	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event){
		handleBlockLoot(event);
		handleShearedLeaves(event);
	}

	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent event){
		handleMaxBuildingHeight(event);
	}

	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event){
		handleAppleDrops(event);
	}

	private void handleMaxBuildingHeight(BlockPlaceEvent e){
		if (maxBuildingHeight < 0 || e.getPlayer().getGameMode() != GameMode.SURVIVAL) return;

		if (e.getBlock().getY() > maxBuildingHeight){
			e.setCancelled(true);
			e.getPlayer().sendMessage(Lang.PLAYERS_BUILD_HEIGHT);
		}
	}

	private void handleBlockLoot(BlockBreakEvent event){
		Material material = event.getBlock().getType();
		if(blockLoots.containsKey(material)){
			BlockLootConfiguration lootConfig = blockLoots.get(material);
			Location loc = event.getBlock().getLocation().add(.5,.5,.5);
			event.getBlock().setType(Material.AIR);
			event.setExpToDrop(lootConfig.getAddXp());
			loc.getWorld().dropItem(loc, lootConfig.getLoot().clone());
			if (lootConfig.getAddXp() > 0) {
				UhcItems.spawnExtraXp(loc, lootConfig.getAddXp());
			}
		}
	}

	private void handleShearedLeaves(BlockBreakEvent e){
		MainConfiguration cfg = GameManager.getGameManager().getConfiguration();
		if (!cfg.getAppleDropsFromShearing()){
			return;
		}

		if (!UniversalMaterial.isLeaves(e.getBlock().getType())){
			return;
		}

		if (e.getPlayer().getItemInHand().getType() == Material.SHEARS){
			Bukkit.getPluginManager().callEvent(new LeavesDecayEvent(e.getBlock()));
		}
	}

	private void handleAppleDrops(LeavesDecayEvent e){
		MainConfiguration cfg = GameManager.getGameManager().getConfiguration();
		Block block = e.getBlock();
		Material type = block.getType();
		boolean isOak;

		if (cfg.getAppleDropsFromAllTrees()){
			if (type != UniversalMaterial.OAK_LEAVES.getType()) {
				e.getBlock().setType(UniversalMaterial.OAK_LEAVES.getType());
			}
			isOak = true;
		}else {
			isOak = type == UniversalMaterial.OAK_LEAVES.getType() || type == UniversalMaterial.DARK_OAK_LEAVES.getType();
		}

		if (!isOak){
			return; // Will never drop apples so drops don't need to increase
		}

		double percentage = cfg.getAppleDropPercentage()-0.5;

		if (percentage <= 0){
			return; // No added drops
		}

		// Number 0-100
		double random = RandomUtils.randomInteger(0, 200)/2D;

		if (random > percentage){
			return; // Number above percentage so no extra apples.
		}

		// Add apple to drops
		Bukkit.getScheduler().runTask(UhcCore.getPlugin(), new Runnable() {
			@Override
			public void run() {
				block.getWorld().dropItem(block.getLocation().add(.5, .5, .5), new ItemStack(Material.APPLE));
			}
		});
	}

}