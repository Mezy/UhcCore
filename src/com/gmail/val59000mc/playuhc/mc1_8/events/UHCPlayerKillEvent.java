package com.gmail.val59000mc.playuhc.mc1_8.events;

import com.gmail.val59000mc.playuhc.mc1_8.players.UhcPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class UHCPlayerKillEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private UhcPlayer killer;
	private UhcPlayer killed;

	public UHCPlayerKillEvent(UhcPlayer killer, UhcPlayer killed){
		this.killer = killer;
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
