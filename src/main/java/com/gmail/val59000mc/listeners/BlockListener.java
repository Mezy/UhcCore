package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.LootConfiguration;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
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

	private final PlayerManager playerManager;
	private final MainConfig configuration;
	private final Map<Material, LootConfiguration<Material>> blockLoots;
	private final int maxBuildingHeight;
	
	public BlockListener(GameManager gameManager){
		playerManager = gameManager.getPlayerManager();
		configuration = gameManager.getConfig();
		blockLoots = configuration.get(MainConfig.ENABLE_BLOCK_LOOT) ? configuration.get(MainConfig.BLOCK_LOOT) : new HashMap<>();
		maxBuildingHeight = configuration.get(MainConfig.MAX_BUILDING_HEIGHT);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		handleBlockLoot(event);
		handleShearedLeaves(event);
		handleFrozenPlayers(event);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		handleMaxBuildingHeight(event);
		handleFrozenPlayers(event);
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
			LootConfiguration<Material> lootConfig = blockLoots.get(material);
			Location loc = event.getBlock().getLocation().add(.5,.5,.5);

			event.getBlock().setType(Material.AIR);
			event.setExpToDrop(lootConfig.getAddXp());

			lootConfig.getLoot().forEach(item -> loc.getWorld().dropItem(loc, item.clone()));

			if (lootConfig.getAddXp() > 0) {
				UhcItems.spawnExtraXp(loc, lootConfig.getAddXp());
			}
		}
	}

	private void handleShearedLeaves(BlockBreakEvent e){
		if (!configuration.get(MainConfig.APPLE_DROPS_FROM_SHEARING)){
			return;
		}

		if (!UniversalMaterial.isLeaves(e.getBlock().getType())){
			return;
		}

		if (e.getPlayer().getItemInHand().getType() == Material.SHEARS){
			Bukkit.getPluginManager().callEvent(new LeavesDecayEvent(e.getBlock()));
		}
	}

	private void handleFrozenPlayers(BlockBreakEvent e){
		UhcPlayer uhcPlayer = playerManager.getUhcPlayer(e.getPlayer());
		if (uhcPlayer.isFrozen()){
			e.setCancelled(true);
		}
	}

	private void handleFrozenPlayers(BlockPlaceEvent e){
		UhcPlayer uhcPlayer = playerManager.getUhcPlayer(e.getPlayer());
		if (uhcPlayer.isFrozen()){
			e.setCancelled(true);
		}
	}

	private void handleAppleDrops(LeavesDecayEvent e){
		Block block = e.getBlock();
		Material type = block.getType();
		boolean isOak;

		if (configuration.get(MainConfig.APPLE_DROPS_FROM_ALL_TREES)){
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

		double percentage = configuration.get(MainConfig.APPLE_DROP_PERCENTAGE)-0.5;

		if (percentage <= 0){
			return; // No added drops
		}

		// Number 0-100
		double random = RandomUtils.randomInteger(0, 200)/2D;

		if (random > percentage){
			return; // Number above percentage so no extra apples.
		}

		// Add apple to drops
		Bukkit.getScheduler().runTask(UhcCore.getPlugin(), () -> block.getWorld().dropItem(block.getLocation().add(.5, .5, .5), new ItemStack(Material.APPLE)));
	}

}