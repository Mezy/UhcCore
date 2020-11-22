package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.maploader.BiomeMapping;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerPortalEvent;

public class VersionUtils_1_14 extends VersionUtils_1_13{

    @Override
    public void handleNetherPortalEvent(PlayerPortalEvent event){
        Location loc = event.getFrom();
        MainConfiguration cfg = GameManager.getGameManager().getConfiguration();

        if (event.getFrom().getWorld().getEnvironment() == World.Environment.NETHER){
            loc.setWorld(Bukkit.getWorld(cfg.getOverworldUuid()));
            loc.setX(loc.getX() * 2d);
            loc.setZ(loc.getZ() * 2d);
            event.setTo(loc);
        }else{
            loc.setWorld(Bukkit.getWorld(cfg.getNetherUuid()));
            loc.setX(loc.getX() / 2d);
            loc.setZ(loc.getZ() / 2d);
            event.setTo(loc);
        }
    }

    @Override
    public void replaceOceanBiomes(){
        if (PaperLib.isVersion(16) && PaperLib.getMinecraftPatchVersion() > 1){
            Bukkit.getLogger().warning("[UhcCore] Ocean biomes won't be replaced, this feature is not supported on your version!");
            return;
        }

        BiomeMapping.replaceBiomes(BiomeMapping.Biome.OCEAN, BiomeMapping.Biome.PLAINS);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.DEEP_OCEAN, BiomeMapping.Biome.FOREST);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.FROZEN_OCEAN, BiomeMapping.Biome.PLAINS);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.DEEP_FROZEN_OCEAN, BiomeMapping.Biome.FOREST);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.WARM_OCEAN, BiomeMapping.Biome.PLAINS);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.DEEP_WARM_OCEAN, BiomeMapping.Biome.FOREST);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.LUKEWARM_OCEAN, BiomeMapping.Biome.PLAINS);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.DEEP_LUKEWARM_OCEAN, BiomeMapping.Biome.FOREST);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.COLD_OCEAN, BiomeMapping.Biome.PLAINS);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.DEEP_COLD_OCEAN, BiomeMapping.Biome.FOREST);
    }

}