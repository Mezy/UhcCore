package com.gmail.val59000mc.maploader;

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

    private static final BlockFace[] BLOCK_FACES = new BlockFace[]{
            BlockFace.DOWN,
            BlockFace.UP,
            BlockFace.SOUTH,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.WEST
    };

    private static final Material AIR;
    private static final Material CAVE_AIR;

    static{
        AIR = Material.AIR;
        CAVE_AIR = UniversalMaterial.CAVE_AIR.getType();
    }

    @Override
    public void populate(World world, Random random, Chunk chunk){
        scanChunk(chunk);
    }

    private void scanChunk(Chunk chunk){
        for (int x = 0; x < 16; x++){
            for (int y = 5; y < 30; y++){
                for (int z = 0; z < 16; z++){

                    Block block = chunk.getBlock(x, y, z);
                    Material type = block.getType();
                    if (
                            type == Material.DIAMOND_ORE ||
                            type == Material.GOLD_ORE ||
                            type == Material.LAPIS_ORE
                    ){
                        Vein vein = new Vein();
                        vein.process(block);
                        if (!vein.isConnectedToAir()){
                            vein.setToStone();
                        }
                    }

                }
            }
        }
    }

    private static class Vein{
        private final Set<Block> ores;

        private Vein(){
            ores = new HashSet<>();
        }

        private void process(Block startBlock){
            getVeinBlocks(startBlock, startBlock.getType(), 2, 10);
        }

        private void getVeinBlocks(Block block, Material type, int i, int maxBlocks){
            if (maxBlocks == 0) return;

            if (block.getType() == UniversalMaterial.GLOWING_REDSTONE_ORE.getType()){
                block.setType(Material.REDSTONE_ORE);
            }

            if (block.getType() == type && !ores.contains(block)){
                ores.add(block);
                i = 2;
            }else {
                i--;
            }
            if (i > 0){
                for (BlockFace face : BLOCK_FACES) {
                    getVeinBlocks(block.getRelative(face), type, i, maxBlocks-1);
                }
            }
        }

        private boolean isConnectedToAir(){
            for (Block block : ores){
                for (BlockFace face : BLOCK_FACES){
                    Material relative = block.getRelative(face).getType();
                    if (relative == AIR || relative == CAVE_AIR){
                        return true;
                    }
                }
            }
            return false;
        }

        private void setToStone(){
            for (Block block : ores){
                block.setType(Material.STONE);
            }
        }
    }

}