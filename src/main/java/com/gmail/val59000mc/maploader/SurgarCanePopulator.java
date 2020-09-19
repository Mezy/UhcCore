package com.gmail.val59000mc.maploader;

import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class SurgarCanePopulator extends BlockPopulator{

    private final int percentage;

    public SurgarCanePopulator(int percentage){
        this.percentage = percentage;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk){
        for (int x = 1; x < 15; x++) {
            for (int z = 1; z < 15; z++) {
                Block block = world.getHighestBlockAt(chunk.getBlock(x, 0, z).getLocation());
                Block below = block.getRelative(BlockFace.DOWN);

                if (percentage > random.nextInt(100) && (below.getType() == Material.SAND || below.getType() == Material.GRASS)){

                    Material water = UniversalMaterial.STATIONARY_WATER.getType();
                    if (
                            below.getRelative(BlockFace.NORTH).getType() == water ||
                            below.getRelative(BlockFace.EAST).getType() == water ||
                            below.getRelative(BlockFace.SOUTH).getType() == water ||
                            below.getRelative(BlockFace.WEST).getType() == water
                    ){
                        if (block.getType() == Material.AIR){
                            int height = random.nextInt(3)+1;
                            Location location = block.getLocation();
                            while (height > 0){
                                world.getBlockAt(location).setType(UniversalMaterial.SUGAR_CANE_BLOCK.getType());
                                location = location.add(0, 1, 0);
                                height--;
                            }
                        }
                    }
                }
            }
        }
    }

}