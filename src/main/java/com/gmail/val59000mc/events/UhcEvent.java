package com.gmail.val59000mc.events;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.PlayersManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class UhcEvent extends Event {

    private static HandlerList handlers;

    static{
        handlers = new HandlerList();
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GameManager getGameManager(){
        return GameManager.getGameManager();
    }

    public PlayersManager getPlayersManager(){
        return getGameManager().getPlayersManager();
    }

}