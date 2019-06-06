package com.gmail.val59000mc.mc1_13.events;

import com.gmail.val59000mc.mc1_13.players.UhcPlayer;
import java.util.Set;

public class UhcTimeEvent extends UhcEvent{
	
	/**
	 * Playing players
	 */
	private Set<UhcPlayer> playingPlayers;
	
	/**
	 * Time played in seconds since beginning
	 */
	private long totalTime;
	
	public UhcTimeEvent(Set<UhcPlayer> playingPlayers, long totalTime){
		this.playingPlayers = playingPlayers;
		this.totalTime = totalTime;
	}

	public Set<UhcPlayer> getPlayingPlayers() {
		return playingPlayers;
	}
	
	public long getTotalTime() {
		return totalTime;
	}

}