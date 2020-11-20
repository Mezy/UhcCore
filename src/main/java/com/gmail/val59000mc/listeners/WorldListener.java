package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.maploader.BiomeTypePopulator;
import com.gmail.val59000mc.maploader.CaveOresOnlyPopulator;
import com.gmail.val59000mc.maploader.SurgarCanePopulator;
import com.gmail.val59000mc.maploader.VeinGeneratorPopulator;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class WorldListener implements Listener{

    @EventHandler
    public void onWorldInit(WorldInitEvent e){
        World world = e.getWorld();
        MainConfiguration cfg = GameManager.getGameManager().getConfiguration();
        if (world.getName().equals(cfg.getOverworldUuid()) && cfg.getEnableGenerateSugarcane()){
            world.getPopulators().add(new SurgarCanePopulator(cfg.getGenerateSugarcanePercentage()));
        }
        if (world.getName().equals(cfg.getOverworldUuid()) && cfg.getReplaceOceanBiomes() && UhcCore.getVersion() >= 14){
            world.getPopulators().add(new BiomeTypePopulator());
        }
        if (world.getName().equals(cfg.getOverworldUuid()) && cfg.getCaveOresOnly()){
            world.getPopulators().add(new CaveOresOnlyPopulator());
        }
        if (world.getName().equals(cfg.getOverworldUuid()) && cfg.getEnableGenerateVein()){
            world.getPopulators().add(new VeinGeneratorPopulator(cfg.getGenerateVeins()));
        }
    }

}