package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.maploader.MapLoader;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;

public class VersionUtils_1_14 extends VersionUtils_1_13{

    @Override
    public void handleNetherPortalEvent(PlayerPortalEvent event){
        Location loc = event.getFrom();
        MapLoader mapLoader = GameManager.getGameManager().getMapLoader();

        if (event.getFrom().getWorld().getEnvironment() == World.Environment.NETHER){
            loc.setWorld(mapLoader.getUhcWorld(World.Environment.NORMAL));
            loc.setX(loc.getX() * 2d);
            loc.setZ(loc.getZ() * 2d);
            event.setTo(loc);
        }else{
            loc.setWorld(mapLoader.getUhcWorld(World.Environment.NETHER));
            loc.setX(loc.getX() / 2d);
            loc.setZ(loc.getZ() / 2d);
            event.setTo(loc);
        }
    }

    @Override
    public void killPlayer(Player player) {
        player.damage(player.getHealth());
    }

}