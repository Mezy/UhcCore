package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerHungerGainListener implements Listener {

    private final PlayerManager playerManager;

    public PlayerHungerGainListener(PlayerManager playerManager){
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent e){

        if (!(e.getEntity() instanceof Player)){
            return;
        }

        UhcPlayer player = playerManager.getUhcPlayer((Player) e.getEntity());

        // Cancel hunger when the game is not in playing state.
        if (player.getState() != PlayerState.PLAYING){
            e.setCancelled(true);
        }
    }

}
