package com.gmail.val59000mc.playuhc.mc1_8.threads;

import org.bukkit.Bukkit;

import com.gmail.val59000mc.playuhc.PlayUhc;
import com.gmail.val59000mc.playuhc.mc1_8.game.GameManager;
import com.gmail.val59000mc.playuhc.mc1_8.languages.Lang;

public class StopRestartThread implements Runnable{

	long timeBeforeStop;
	
	
	public StopRestartThread(){
		this.timeBeforeStop = GameManager.getGameManager().getConfiguration().getTimeBeforeRestartAfterEnd();
	}
	
	@Override
	public void run() {
		GameManager gm = GameManager.getGameManager();
			
			if(timeBeforeStop == 0){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
			}else{
				if(timeBeforeStop<5 || timeBeforeStop%10 == 0){
					Bukkit.getLogger().info("[PlayUhc] Server will shutdown in "+timeBeforeStop+"s");
					gm.broadcastInfoMessage(Lang.GAME_SHUTDOWN.replace("%time%", ""+timeBeforeStop));
				}
				
				timeBeforeStop--;
				Bukkit.getScheduler().scheduleSyncDelayedTask(PlayUhc.getPlugin(), this,20);
				}
	}

}
