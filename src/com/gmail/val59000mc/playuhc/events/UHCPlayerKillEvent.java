package com.gmail.val59000mc.playuhc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gmail.val59000mc.playuhc.players.UhcPlayer;

public final class UHCPlayerKillEvent extends Event{
	
	private static final HandlerList handlers = new HandlerList();
	private UhcPlayer killer;
	private UhcPlayer killed;
	
	public UHCPlayerKillEvent(UhcPlayer killer, UhcPlayer killed){
		this.killed = killer;
		this.killed = killed;
	}
	
	public UhcPlayer getKiller(){
		return killer;
	}
	
	public UhcPlayer getKilled(){
		return killed;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
