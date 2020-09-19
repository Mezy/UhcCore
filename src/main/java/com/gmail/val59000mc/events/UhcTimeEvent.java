package com.gmail.val59000mc.events;

import com.gmail.val59000mc.players.UhcPlayer;

import java.util.Set;

public class UhcTimeEvent extends UhcEvent {

	/**
	 * Playing players
	 */
	private final Set<UhcPlayer> playingPlayers;

	/**
	 * Time played in seconds since beginning
	 */
	private final long totalTime;

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