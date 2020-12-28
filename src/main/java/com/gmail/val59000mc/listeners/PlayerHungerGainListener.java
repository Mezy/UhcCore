package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerHungerGainListener implements Listener {

    private final PlayersManager playersManager;

    public PlayerHungerGainListener(PlayersManager playersManager){
        this.playersManager = playersManager;
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent e){

        if (!(e.getEntity() instanceof Player)){
            return;
        }

        UhcPlayer player = playersManager.getUhcPlayer((Player) e.getEntity());

        // Cancel hunger when the game is not in playing state.
        if (player.getState() != PlayerState.PLAYING){
            e.setCancelled(true);
        }
    }

}
