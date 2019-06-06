package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;

public class TimeBeforeEndThread implements Runnable{

	long remainingTime;
	GameManager gm;
	TimeBeforeEndThread task;
	
	
	public TimeBeforeEndThread() {
		this.gm = GameManager.getGameManager();
		this.remainingTime = gm.getRemainingTime();
		task = this;
	}
	
	@Override
	public void run() {
		
		remainingTime--;
		gm.setRemainingTime(remainingTime);
		
		if(remainingTime >= 0 && remainingTime <= 60 && (remainingTime%10 == 0 || remainingTime <= 10)){
			gm.getPlayersManager().playSoundToAll(UniversalSound.CLICK);
		}
		
		if(remainingTime > 0 && (gm.getGameState().equals(GameState.PLAYING) || gm.getGameState().equals(GameState.DEATHMATCH)))
			Bukkit.getScheduler().runTaskLaterAsynchronously(UhcCore.getPlugin(), task, 20);
	}
	
}
