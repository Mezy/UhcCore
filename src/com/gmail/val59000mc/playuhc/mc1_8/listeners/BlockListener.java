package com.gmail.val59000mc.playuhc.mc1_8.listeners;

import com.gmail.val59000mc.playuhc.mc1_8.configuration.BlockLootConfiguration;
import com.gmail.val59000mc.playuhc.mc1_8.configuration.MainConfiguration;
import com.gmail.val59000mc.playuhc.mc1_8.customitems.UhcItems;
import com.gmail.val59000mc.playuhc.mc1_8.game.GameManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockListener implements Listener{
	
	private Map<Material,BlockLootConfiguration> blockLoots;
	private boolean treesAutoCut;
	private boolean treesApplesOnEveryTreeType;
	
	public BlockListener(){
		MainConfiguration cfg = GameManager.getGameManager().getConfiguration();
		blockLoots = cfg.getEnableBlockLoots() ? cfg.getBlockLoots() : new HashMap<>();
		treesAutoCut = cfg.getTreesAutoCut();
		treesApplesOnEveryTreeType = cfg.getTreesApplesOnEveryTreeType();
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockBreak(final BlockBreakEvent event){
		handleBlockLoot(event);
		handleTreeBreak(event);
		handleLeavesBreak(event);
	}


	@EventHandler(priority=EventPriority.HIGHEST)
	private void onLeavesDecay(LeavesDecayEvent event) {
		replaceLeavesByOakLeaves(event.getBlock());
		event.getBlock().breakNaturally();
	}
	

	private void handleBlockLoot(BlockBreakEvent event){
		Material material = event.getBlock().getType();
		if(blockLoots.containsKey(material)){
			BlockLootConfiguration lootConfig = blockLoots.get(material);
			Location loc = event.getBlock().getLocation();
			event.getBlock().setType(Material.AIR);
			event.setExpToDrop(lootConfig.getAddXp());
			loc.getWorld().dropItem(loc, lootConfig.getLoot().clone());
			UhcItems.spawnExtraXp(loc,lootConfig.getAddXp());
		}
	}
	
	@SuppressWarnings("deprecation")
	private void replaceLeavesByOakLeaves(Block block) {
		if(block.getType().equals(Material.LEAVES) || block.getType().equals(Material.LEAVES_2)){
			block.setType(Material.LEAVES);
			block.setData((byte) 0);
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
		if(block.getType().equals(Material.LOG) || block.getType().equals(Material.LOG_2)){
			block.breakNaturally();
			for(Block woodBlock : getFacingBlocks(block)){
				breakTreeStartingFrom(woodBlock);
			}
			for(Block leaveBlock : getSurroundingBlocks(block, 2)){
				if(leaveBlock.getType().equals(Material.LEAVES) || leaveBlock.getType().equals(Material.LEAVES_2)){
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
