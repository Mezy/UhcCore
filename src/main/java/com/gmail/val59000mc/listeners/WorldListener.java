package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.maploader.BiomeTypePopulator;
import com.gmail.val59000mc.maploader.CaveOresOnlyPopulator;
import com.gmail.val59000mc.maploader.SurgarCanePopulator;
import io.papermc.lib.PaperLib;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class WorldListener implements Listener{

    @EventHandler
    public void onWorldInit(WorldInitEvent e){
        World world = e.getWorld();
        GameManager gm = GameManager.getGameManager();
        MainConfig cfg = gm.getConfig();

        String overworldUuid = gm.getMapLoader().getUhcWorldUuid(World.Environment.NORMAL);

        if (world.getName().equals(overworldUuid) && cfg.get(MainConfig.ENABLE_GENERATE_SUGARCANE)){
            world.getPopulators().add(new SurgarCanePopulator(cfg.get(MainConfig.GENERATE_SUGARCANE_PERCENTAGE)));
        }
        if (world.getName().equals(overworldUuid) && cfg.get(MainConfig.REPLACE_OCEAN_BIOMES) && UhcCore.getVersion() >= 14){
            if (!(PaperLib.isVersion(16) && PaperLib.getMinecraftPatchVersion() > 1)){
                world.getPopulators().add(new BiomeTypePopulator());
            }
        }
        if (world.getName().equals(overworldUuid) && cfg.get(MainConfig.CAVE_ORES_ONLY)){
            world.getPopulators().add(new CaveOresOnlyPopulator());
        }
    }

}