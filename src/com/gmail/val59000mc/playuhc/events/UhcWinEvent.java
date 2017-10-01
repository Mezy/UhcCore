package com.gmail.val59000mc.playuhc.events;

import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gmail.val59000mc.playuhc.players.UhcPlayer;

public class UhcWinEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private Set<UhcPlayer> winners;
	
	public UhcWinEvent(Set<UhcPlayer> winners){
		this.winners = winners;
	}

	public Set<UhcPlayer> getWinners(){
		return winners;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
