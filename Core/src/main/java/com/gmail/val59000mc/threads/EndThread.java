package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import org.bukkit.Bukkit;

public class EndThread implements Runnable{

	private static final EndThread instance;

	private int timeBeforeEnd;
	private boolean run;

	private EndThread(){
		timeBeforeEnd = 61;
		run = false;
	}

	static{
		instance = new EndThread();
	}
	
	@Override
	public void run() {
		if (!run){
			return; // Stop thread
		}

		GameManager gm = GameManager.getGameManager();

		if(timeBeforeEnd <= 0){
			gm.endGame();
		}else{
			if(timeBeforeEnd%10 == 0 || timeBeforeEnd <= 5){
				Bukkit.getLogger().info(Lang.DISPLAY_MESSAGE_PREFIX+" "+Lang.PLAYERS_ALL_HAVE_LEFT+" "+timeBeforeEnd);
				gm.broadcastInfoMessage(Lang.PLAYERS_ALL_HAVE_LEFT+" "+timeBeforeEnd);
			}
			timeBeforeEnd--;
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), EndThread.this,20);
		}
	}

	public static void start(){
		if(instance.run){
			return; // Already running
		}

		instance.run = true;
		instance.timeBeforeEnd = 61;
		Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), instance, 20);
	}

	public static void stop(){
		if(instance.run){
			instance.run = false;
			GameManager.getGameManager().broadcastInfoMessage(Lang.GAME_END_STOPPED);
			Bukkit.getLogger().info(Lang.DISPLAY_MESSAGE_PREFIX+" "+Lang.GAME_END_STOPPED);
		}
	}

}