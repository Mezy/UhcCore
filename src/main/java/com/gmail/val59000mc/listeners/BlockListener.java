package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.BlockLootConfiguration;
import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockListener implements Listener{
	
	private Map<Material, BlockLootConfiguration> blockLoots;
	private boolean treesAutoCut;
	private boolean treesApplesOnEveryTreeType;
	private int maxBuildingHeight;
	
	public BlockListener(){
		MainConfiguration cfg = GameManager.getGameManager().getConfiguration();
		blockLoots = cfg.getEnableBlockLoots() ? cfg.getBlockLoots() : new HashMap<>();
		treesAutoCut = cfg.getTreesAutoCut();
		treesApplesOnEveryTreeType = cfg.getTreesApplesOnEveryTreeType();
		maxBuildingHeight = cfg.getMaxBuildingHeight();
	}

	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event){
		handleBlockLoot(event);
		handleTreeBreak(event);
		handleLeavesBreak(event);
	}

	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent event){
		handleMaxBuildingHeight(event);
	}

	@EventHandler
	private void onLeavesDecay(LeavesDecayEvent event) {
		replaceLeavesByOakLeaves(event.getBlock());
		event.getBlock().breakNaturally();
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
	
	@SuppressWarnings("deprecation")
	private void replaceLeavesByOakLeaves(Block block) {
		if(UniversalMaterial.isLeaves(block.getType())){
			block.setType(UniversalMaterial.OAK_LEAVES.getType());
			//block.setData((byte) 0); todo check if this still works
		}
	}
	
	private void handleLeavesBreak(BlockBreakEvent event) {
		if(treesApplesOnEveryTreeType){
			replaceLeavesByOakLeaves(event.getBlock());
		}
	}
	
	private void handleTreeBreak(BlockBreakEvent event) {
		if(treesAutoCut){
			breakTreeStartingFrom(event.getBlock());
		}
	}
	
	private void breakTreeStartingFrom(Block block){
		if(UniversalMaterial.isLog(block.getType())){
			block.breakNaturally();
			for(Block woodBlock : getFacingBlocks(block)){
				breakTreeStartingFrom(woodBlock);
			}
			for(Block leaveBlock : getSurroundingBlocks(block, 2)){
				if(UniversalMaterial.isLeaves(leaveBlock.getType())){
					if(treesApplesOnEveryTreeType){
						replaceLeavesByOakLeaves(leaveBlock);
					}
					leaveBlock.breakNaturally();
				}
			}
		}
	}
	
	private Set<Block> getFacingBlocks(Block block){
		Set<Block> blocks = new HashSet<Block>();
		blocks.add(block.getRelative(BlockFace.DOWN));
		blocks.add(block.getRelative(BlockFace.UP));
		blocks.add(block.getRelative(BlockFace.SOUTH));
		blocks.add(block.getRelative(BlockFace.NORTH));
		blocks.add(block.getRelative(BlockFace.EAST));
		blocks.add(block.getRelative(BlockFace.WEST));
		return blocks;
	}
	
	private Set<Block> getSurroundingBlocks(Block block, int radius){
		Set<Block> blocks = new HashSet<Block>();
		for(int i=-radius ; i<=radius ; i++){
			for(int j=-radius ; j<=radius ; j++){
				for(int k=-radius ; k<=radius ; k++){
					Block neigbour = block.getRelative(i, j, k);
					blocks.add(neigbour);
				}
			}
		}
		return blocks;
	}

}
