package com.gmail.val59000mc.playuhc.events;

import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gmail.val59000mc.playuhc.players.UhcPlayer;

public class UhcTimeEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * Playing players
	 */
	private Set<UhcPlayer> playingPlayers;
	
	/**
	 * Time played in seconds since beginning
	 */
	private long totalTime;
	
	/**
	 * Time played in seconds since last time event
	 */
	private long time;
	
	public UhcTimeEvent(Set<UhcPlayer> playingPlayers, long time, long totalTime){
		this.playingPlayers = playingPlayers;
		this.time = time;
		this.totalTime = totalTime;
	}

	
	
	public Set<UhcPlayer> getPlayingPlayers() {
		return playingPlayers;
	}
	
	public long getTotalTime() {
		return totalTime;
	}


	public long getTime() {
		return time;
	}


	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
