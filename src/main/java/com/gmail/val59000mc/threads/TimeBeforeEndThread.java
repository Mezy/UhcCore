package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;

public class TimeBeforeEndThread implements Runnable{

	private GameManager gm;

	public TimeBeforeEndThread() {
		this.gm = GameManager.getGameManager();
	}
	
	@Override
	public void run() {
		long remainingTime = gm.getRemainingTime();

		remainingTime--;
		gm.setRemainingTime(remainingTime);
		
		if(remainingTime >= 0 && remainingTime <= 60 && (remainingTime%10 == 0 || remainingTime <= 10)){
			gm.getPlayersManager().playSoundToAll(UniversalSound.CLICK);
		}
		
		if(remainingTime > 0 && (gm.getGameState().equals(GameState.PLAYING) || gm.getGameState().equals(GameState.DEATHMATCH))) {
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this, 20);
		}
	}
	
}