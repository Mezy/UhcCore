package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.maploader.BiomeMapping;
import com.gmail.val59000mc.maploader.BiomeMapping16;

public class VersionUtils_1_16 extends VersionUtils_1_15{

    @Override
    public void replaceOceanBiomes() {
        BiomeMapping16.replaceBiomes(BiomeMapping16.Biome.OCEAN, BiomeMapping16.Biome.PLAINS);


        BiomeMapping16.replaceBiomes(BiomeMapping16.Biome.OCEAN, BiomeMapping16.Biome.PLAINS);
        BiomeMapping16.replaceBiomes(BiomeMapping16.Biome.DEEP_OCEAN, BiomeMapping16.Biome.FOREST);
        BiomeMapping16.replaceBiomes(BiomeMapping16.Biome.FROZEN_OCEAN, BiomeMapping16.Biome.PLAINS);
        BiomeMapping16.replaceBiomes(BiomeMapping16.Biome.DEEP_FROZEN_OCEAN, BiomeMapping16.Biome.FOREST);
        BiomeMapping16.replaceBiomes(BiomeMapping16.Biome.WARM_OCEAN, BiomeMapping16.Biome.PLAINS);
        BiomeMapping16.replaceBiomes(BiomeMapping16.Biome.DEEP_WARM_OCEAN, BiomeMapping16.Biome.FOREST);
        BiomeMapping16.replaceBiomes(BiomeMapping16.Biome.LUKEWARM_OCEAN, BiomeMapping16.Biome.PLAINS);
        BiomeMapping16.replaceBiomes(BiomeMapping16.Biome.DEEP_LUKEWARM_OCEAN, BiomeMapping16.Biome.FOREST);
        BiomeMapping16.replaceBiomes(BiomeMapping16.Biome.COLD_OCEAN, BiomeMapping16.Biome.PLAINS);
        BiomeMapping16.replaceBiomes(BiomeMapping16.Biome.DEEP_COLD_OCEAN, BiomeMapping16.Biome.FOREST);
    }
}