package com.gmail.val59000mc.maploader;

import com.gmail.val59000mc.configuration.GenerateVeinConfiguration;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.utils.RandomUtils;
import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class VeinGenerator {

	private Map<Material, GenerateVeinConfiguration> generateVeins;
	
	public VeinGenerator(){
		generateVeins = GameManager.getGameManager().getConfiguration().getGenerateVeins();
	}
	
	/**
	 * Generate random veins in the given chunk based on the configuration
	 * @param chunk : the chunk to generate the veins into
	 * @return number of veins generated
	 */
	public int generateVeinsInChunk(Chunk chunk) {
		int totalNbrVeins = 0;
		for(Entry<Material,GenerateVeinConfiguration> entry : generateVeins.entrySet()){
			GenerateVeinConfiguration veinCfg = entry.getValue();
			Material material = entry.getKey();
			
			int randNbrVeins = RandomUtils.randomInteger(veinCfg.getMinVeinsPerChunk(), veinCfg.getMaxVeinsPerChunk());
			
			for(int i=0 ; i<randNbrVeins ; i++){
				int randNbrBlocks =  RandomUtils.randomInteger(veinCfg.getMinBlocksPerVein(), veinCfg.getMaxBlocksPerVein());
				if(randNbrBlocks > 0){
					int randX = RandomUtils.randomInteger(0, 15);
					int randY = RandomUtils.randomInteger(veinCfg.getMinY(),veinCfg.getMaxY());
					int randZ = RandomUtils.randomInteger(0, 15);
					Block randBlock = tryAdjustingToProperBlock(chunk.getBlock(randX, randY, randZ));
					if(randBlock != null){
						totalNbrVeins++;
						generateVein(material,randBlock,randNbrBlocks);
					}
				}
			}
		}
		return totalNbrVeins;
	}
	
	/**
	 * Look in a 5 blocks radius to find a non AIR or WATER block
	 * @param randBlock
	 * @return a non AIR/WATER Block if found, else null
	 */
	private Block tryAdjustingToProperBlock(Block randBlock) {
		if(randBlock.getType().equals(Material.STONE)){
			return randBlock;
		}
		
		// Descend to go beneath the water in the sea
		if(randBlock.getType().equals(UniversalMaterial.STATIONARY_WATER.getType())){
			while(randBlock.getType().equals(UniversalMaterial.STATIONARY_WATER.getType()) && randBlock.getY() > 10){
				randBlock = randBlock.getRelative(0, -10, 0);
			}
			if(randBlock.getType().equals(Material.STONE)){
				return randBlock;
			}
		}
		
		// Find proper block nearby
		for(int i = -5; i<=5 ; i++){
			for(int j = -5; j<=5 ; j++){
				for(int k = -5; k<=5 ; k++){
					Block relativeBlock = randBlock.getRelative(i, j, k);
					if(relativeBlock.getType().equals(Material.STONE)){
						return relativeBlock;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Generate a vein starting from a block
	 * @param material : the material of the vein
	 * @param startBlock : the block where to start the vein
	 * @param nbrBlocks : the number of blocks in the vein
	 */
	private void generateVein(Material material, Block startBlock, int nbrBlocks){
		List<Block> blocks = getAdjacentsBlocks(startBlock,nbrBlocks);
		for(Block block : blocks){
			block.setType(material);
		}
	}
	
	/**
	 * Get a set of adjacent blocks starting from a block
	 * @param startBlock : the block where to start the search
	 * @param nbrBlocks : number of adjacent blocks
	 * @return
	 */
	private List<Block> getAdjacentsBlocks(Block startBlock, int nbrBlocks){
		int failedAttempts = 0;
		List<Block> adjacentBlocks = new ArrayList<Block>();
		adjacentBlocks.add(startBlock);
		while(adjacentBlocks.size() < nbrBlocks && failedAttempts < 25){
			// Get random block in the growing list of chosen blocks
			Block block = adjacentBlocks.get(RandomUtils.randomInteger(0, adjacentBlocks.size()-1));
			
			// RandomFace
			BlockFace face = RandomUtils.randomAdjacentFace();
			Location blockLocation = block.getLocation();
			if( (blockLocation.getBlockY() <= 1 && face.equals(BlockFace.DOWN)) || (blockLocation.getBlockY() >= 255 && face.equals(BlockFace.UP))){
				failedAttempts++;
			}else{
				// Find random adjacent block to this block
				Block adjacent = block.getRelative(face);
				if(adjacentBlocks.contains(adjacent) || !adjacent.getType().equals(Material.STONE)){
					// We only want to find new discovered block inside stone to avoid placing ores in mid-air in the caves.
					failedAttempts++;
				}else{
					adjacentBlocks.add(adjacent);
				}
			}
			
			
		}
		return adjacentBlocks;
	}
}
