package com.gmail.val59000mc.adapters.impl;

import com.gmail.val59000mc.adapters.VersionAdapter;
import com.pieterdebot.biomemapping.Biome;
import com.pieterdebot.biomemapping.BiomeMappingAPI;

import io.papermc.lib.PaperLib;

/**
 * A default {@link VersionAdapter} implementation, used as a fallback.
 */
public class DefaultVersionAdapterImpl extends VersionAdapter {

    @Override
    public void removeOceans() {
        final int version = PaperLib.getMinecraftVersion();
        if (8 <= version && version <= 17) {
            removeOceansUsingBiomeMapping();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private void removeOceansUsingBiomeMapping() {
        final BiomeMappingAPI biomeMapping = new BiomeMappingAPI();
        Biome replacementBiome = Biome.PLAINS;

        for (Biome biome : Biome.values()) {
            if (biome.isOcean() && biomeMapping.biomeSupported(biome)) {
                try {
                    biomeMapping.replaceBiomes(biome, replacementBiome);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                replacementBiome = replacementBiome == Biome.PLAINS ? Biome.FOREST : Biome.PLAINS;
            }
        }
    }

}
