package com.gmail.val59000mc.playuhc.mc1_8.threads;

import com.gmail.val59000mc.playuhc.PlayUhc;
import com.gmail.val59000mc.playuhc.mc1_8.game.GameManager;
import com.gmail.val59000mc.playuhc.mc1_8.game.GameState;
import com.gmail.val59000mc.playuhc.mc1_8.sounds.UhcSound;
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
			gm.getPlayersManager().playSoundToAll(UhcSound.CLICK);
		}
		
		if(remainingTime > 0 && (gm.getGameState().equals(GameState.PLAYING) || gm.getGameState().equals(GameState.DEATHMATCH)))
			Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), task, 20);		
	}
	
}
