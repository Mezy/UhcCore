package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.game.GameManager;
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

}