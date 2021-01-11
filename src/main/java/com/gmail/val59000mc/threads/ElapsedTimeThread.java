package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.events.UhcTimeEvent;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.game.handlers.CustomEventHandler;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;

import java.util.Set;

public class ElapsedTimeThread implements Runnable{

	private final GameManager gameManager;
	private final CustomEventHandler customEventHandler;
	private final ElapsedTimeThread task;
	
	public ElapsedTimeThread(GameManager gameManager, CustomEventHandler customEventHandler) {
		this.gameManager = gameManager;
		this.customEventHandler = customEventHandler;
		this.task = this;
	}
	
	@Override
	public void run() {
		
		long time = gameManager.getElapsedTime() + 1;
		gameManager.setElapsedTime(time);

		Set<UhcPlayer> playingPlayers = gameManager.getPlayerManager().getOnlinePlayingPlayers();

		// Call time event
		UhcTimeEvent event = new UhcTimeEvent(playingPlayers,time);
		Bukkit.getScheduler().runTask(UhcCore.getPlugin(), () -> Bukkit.getServer().getPluginManager().callEvent(event));

		customEventHandler.handleTimeEvent(playingPlayers, time);

		if(!gameManager.getGameState().equals(GameState.ENDED)){
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), task, 20);
		}
	}

}