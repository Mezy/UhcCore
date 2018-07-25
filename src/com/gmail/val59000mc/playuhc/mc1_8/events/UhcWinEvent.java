package com.gmail.val59000mc.playuhc.mc1_8.events;

import com.gmail.val59000mc.playuhc.mc1_8.players.UhcPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

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
