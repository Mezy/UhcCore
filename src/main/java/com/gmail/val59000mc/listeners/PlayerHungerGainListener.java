package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerHungerGainListener implements Listener {

    private final GameManager gameManager;

    public PlayerHungerGainListener(GameManager gameManager){
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent e){
        // Cancel hunger when the game is not in playing state.
        if (gameManager.getGameState() != GameState.PLAYING){
            e.setCancelled(true);
        }
    }

}
