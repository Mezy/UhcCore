package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import org.bukkit.Bukkit;

public class StopRestartThread implements Runnable{

	private long timeBeforeStop;
	
	public StopRestartThread(){
		this.timeBeforeStop = GameManager.getGameManager().getConfiguration().getTimeBeforeRestartAfterEnd();
	}
	
	@Override
	public void run() {
		if (timeBeforeStop < 0){
			return;
		}

		GameManager gm = GameManager.getGameManager();
			
		if(timeBeforeStop == 0){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
		}else{
			if(timeBeforeStop<5 || timeBeforeStop%10 == 0){
				Bukkit.getLogger().info("[UhcCore] Server will shutdown in "+timeBeforeStop+"s");
				gm.broadcastInfoMessage(Lang.GAME_SHUTDOWN.replace("%time%", ""+timeBeforeStop));
			}

			timeBeforeStop--;
			Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), this,20);
		}
	}

}