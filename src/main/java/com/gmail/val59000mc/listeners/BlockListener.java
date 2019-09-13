package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.BlockLootConfiguration;
import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

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
	}

	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent event){
		handleMaxBuildingHeight(event);
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
			UhcItems.spawnExtraXp(loc,lootConfig.getAddXp());
		}
	}

}