package com.gmail.val59000mc.maploader;

import com.gmail.val59000mc.utils.OreType;
import com.gmail.val59000mc.utils.UniversalMaterial;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CaveOresOnlyPopulator extends BlockPopulator{

	private final boolean[][][] explored = new boolean[16][128][16];
	
	private static final Material AIR;
    private static final Material CAVE_AIR;
    private static final Material WATER;
	
    static{
        AIR = Material.AIR;
        CAVE_AIR = UniversalMaterial.CAVE_AIR.getType();
        WATER = Material.WATER;
    }
	
    private static final BlockFace[] BLOCK_FACES = new BlockFace[]{
            BlockFace.DOWN,
            BlockFace.UP,
            BlockFace.SOUTH,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.WEST
    };

    @Override
    public void populate(World world, Random random, Chunk chunk){
        scanChunk(chunk);
    }

    private void scanChunk(Chunk chunk){

    	for (int x = 0; x < 16; x++)
            for (int y = 5; y < 128; y++)
                for (int z = 0; z < 16; z++)
                	explored[x][y][z] = false;
    	
        for (int x = 0; x < 16; x++){
            for (int y = 5; y < 128; y++){
                for (int z = 0; z < 16; z++){

                    Block block = chunk.getBlock(x, y, z);
                    
                    Material type = block.getType();
                    if (OreType.valueOf(type).isPresent()) {
                        Vein vein = new Vein(chunk, block);
                        vein.process();
                        if (!vein.isConnectedToAir()){
                            vein.setToStone();
                        }
                    }

                }
            }
        }
    }

    private class Vein{
        private final Set<Block> ores;
        private final Chunk chunk;
        private final Material type;
        private final Block startBlock;

        private Vein(Chunk chunk, Block startBlock){
            this.ores = new HashSet<>();
            this.chunk = chunk;
            this.type = startBlock.getType();
            this.startBlock = startBlock;
        }

        private void process(){
            getVeinBlocks(startBlock);
        }

        private void getVeinBlocks(Block block){
        	
        	int relX = block.getX() & 0x0000000F;
        	int relY = block.getY();
        	int relZ = block.getZ() & 0x0000000F;
        	
        	// We must be sure that we remain within the chunk, otherwise the loading of an
        	// adjacent chunk will be triggered, leading to an infinite recursion
        	if 	(
        			(block.getX() >> 4) != chunk.getX() ||
					(block.getZ() >> 4) != chunk.getZ() ||
        			block.getType() != type ||
        			explored[relX][relY][relZ]
    			) 
        			return;
        	
        	
        	explored[relX][relY][relZ] = true;
            ores.add(block);

            for (BlockFace face : BLOCK_FACES) {
            		getVeinBlocks(block.getRelative(face));
            }
        }

        private boolean isConnectedToAir(){
            for (Block block : ores){
                for (BlockFace face : BLOCK_FACES)	{
                	Block adjacentBlock = block.getRelative(face);
                	if	(
                			(adjacentBlock.getX() >> 4) == chunk.getX() &&
                			(adjacentBlock.getZ() >> 4) == chunk.getZ()
            			) {
	                    Material relative = adjacentBlock.getType();
	                    if (relative == AIR || relative == CAVE_AIR || relative == WATER){
	                        return true;
	                    }
                	}
                }
            }
            return false;
        }

        private void setToStone(){
            for (Block block : ores){
                block.setType(Material.STONE, false);
            }
        }
    }

}