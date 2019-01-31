package com.gmail.val59000mc.playuhc.mc1_8.events;

import com.gmail.val59000mc.playuhc.mc1_8.players.UhcPlayer;

public final class UhcPlayerKillEvent extends UhcEvent {

	private UhcPlayer killer;
	private UhcPlayer killed;

	public UhcPlayerKillEvent(UhcPlayer killer, UhcPlayer killed){
		this.killer = killer;
		this.killed = killed;
	}

	public UhcPlayer getKiller(){
		return killer;
	}

	public UhcPlayer getKilled(){
		return killed;
	}

}