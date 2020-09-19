package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;

public class TimeBeforeEndThread implements Runnable{

	private final GameManager gameManager;

	public TimeBeforeEndThread(GameManager gameManager) {
		this.gameManager = gameManager;
	}
	
	@Override
	public void run() {
		long remainingTime = gameManager.getRemainingTime();

		remainingTime--;
		gameManager.setRemainingTime(remainingTime);
		
		if(remainingTime >= 0 && remainingTime <= 60 && (remainingTime%10 == 0 || remainingTime <= 10)){
			gameManager.getPlayersManager().playSoundToAll(UniversalSound.CLICK);
		}
		
		if(remainingTime > 0 && (gameManager.getGameState().equals(GameState.PLAYING) || gameManager.getGameState().equals(GameState.DEATHMATCH))) {
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this, 20);
		}
	}
	
}