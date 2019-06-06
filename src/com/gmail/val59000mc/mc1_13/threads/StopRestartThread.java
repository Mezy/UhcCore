package com.gmail.val59000mc.mc1_13.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.mc1_13.game.GameManager;
import com.gmail.val59000mc.mc1_13.languages.Lang;
import org.bukkit.Bukkit;

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
					Bukkit.getLogger().info("[UHC Core] Server will shutdown in "+timeBeforeStop+"s");
					gm.broadcastInfoMessage(Lang.GAME_SHUTDOWN.replace("%time%", ""+timeBeforeStop));
				}
				
				timeBeforeStop--;
				Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), this,20);
				}
	}

}
