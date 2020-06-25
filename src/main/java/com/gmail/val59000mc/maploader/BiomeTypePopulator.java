package com.gmail.val59000mc.maploader;

import com.gmail.val59000mc.UhcCore;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

/**
 * Used by {@link BiomeMapping} to change the biome collor.
 */
public class BiomeTypePopulator extends BlockPopulator{

    @Override
    public void populate(World world, Random random, Chunk chunk){
        for (int x = 1; x < 15; x++) {
            for (int z = 1; z < 15; z++) {

                Block block = chunk.getBlock(x, 1, z);
                Biome replacementBiome = getReplacementBiome(block.getBiome());

                if (UhcCore.getVersion() < 16){
                    if (replacementBiome != null) {
                        block.setBiome(replacementBiome);
                    }
                }else {
                    for (int y = 0; y < 200; y++) {
                        block = chunk.getBlock(x, y, z);

                        if (replacementBiome != null) {
                            block.setBiome(replacementBiome);
                        }
                    }
                }
            }
        }
    }

    private Biome getReplacementBiome(Biome biome){
        switch (biome){
            case OCEAN:
            case FROZEN_OCEAN:
            case WARM_OCEAN:
            case LUKEWARM_OCEAN:
            case COLD_OCEAN:
                return Biome.PLAINS;
            case DEEP_OCEAN:
            case DEEP_FROZEN_OCEAN:
            case DEEP_WARM_OCEAN:
            case DEEP_LUKEWARM_OCEAN:
            case DEEP_COLD_OCEAN:
                return Biome.FOREST;
        }

        return null;
    }

}