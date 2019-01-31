package com.gmail.val59000mc.playuhc.mc1_8.events;

import com.gmail.val59000mc.playuhc.mc1_8.players.UhcPlayer;

import java.util.Set;

public class UhcTimeEvent extends UhcEvent {

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

}