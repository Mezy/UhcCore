package com.gmail.val59000mc.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UhcLobbyPlayerKilledByPlayerEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final Player player;
    private final Player killer;

    public UhcLobbyPlayerKilledByPlayerEvent(Player player, Player killer) {
        this.player = player;
        this.killer = killer;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getKiller() {
        return killer;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
