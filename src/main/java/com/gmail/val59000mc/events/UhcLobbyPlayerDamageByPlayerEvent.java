package com.gmail.val59000mc.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UhcLobbyPlayerDamageByPlayerEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final Player damager;

    private final double finalDamage;

    private boolean canceled;
    private boolean passOriginal;

    public UhcLobbyPlayerDamageByPlayerEvent(Player player, Player damager, double finalDamage) {
        this.player = player;
        this.damager = damager;

        this.finalDamage = finalDamage;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getDamager() {
        return damager;
    }

    public double getFinalDamage() {
        return finalDamage;
    }

    @Override
    public boolean isCancelled() {
        return this.canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }

    public boolean isPassOriginal() {
        return passOriginal;
    }

    public void setPassOriginal(boolean passOriginal) {
        this.passOriginal = passOriginal;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
