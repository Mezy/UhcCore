package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import org.bukkit.Bukkit;


public class CheckRemainingPlayerThread implements Runnable{

	private final GameManager gameManager;

	public CheckRemainingPlayerThread(GameManager gameManager){
		this.gameManager = gameManager;
	}

	@Override
	public void run() {
		gameManager.getPlayerManager().checkIfRemainingPlayers();
		GameState state = gameManager.getGameState();

		if(state.equals(GameState.PLAYING) || state.equals(GameState.DEATHMATCH)) {
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this, 40);
		}
	}

}