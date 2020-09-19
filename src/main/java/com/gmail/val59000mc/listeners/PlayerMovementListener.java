package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovementListener implements Listener{

    private final PlayersManager playersManager;

    public PlayerMovementListener(PlayersManager playersManager){
        this.playersManager = playersManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        handleFrozenPlayers(event);
    }

    private void handleFrozenPlayers(PlayerMoveEvent e){
        UhcPlayer uhcPlayer = playersManager.getUhcPlayer(e.getPlayer());
        if (uhcPlayer.isFrozen()){
            Location freezeLoc = uhcPlayer.getFreezeLocation();
            Location toLoc = e.getTo();

            if (toLoc.getBlockX() != freezeLoc.getBlockX() || toLoc.getBlockZ() != freezeLoc.getBlockZ()){
                Location newLoc = toLoc.clone();
                newLoc.setX(freezeLoc.getBlockX() + .5);
                newLoc.setZ(freezeLoc.getBlockZ() + .5);

                e.getPlayer().teleport(newLoc);
            }
        }
    }

}