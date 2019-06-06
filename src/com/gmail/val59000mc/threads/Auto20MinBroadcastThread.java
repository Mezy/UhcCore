package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import org.bukkit.Bukkit;

public class Auto20MinBroadcastThread implements Runnable {
	
	boolean broadcast;
	Auto20MinBroadcastThread task;
	
	public Auto20MinBroadcastThread() {
		broadcast = GameManager.getGameManager().getConfiguration().getAuto20MinBroadcast();
		task = this;
	}

	@Override
	public void run() {
		Bukkit.getScheduler().runTask(UhcCore.getPlugin(), new Runnable(){

			@Override
			public void run() {

				if(broadcast){
					GameManager.getGameManager().broadcastInfoMessage(Lang.DISPLAY_YOUTUBER_MARK);
					Bukkit.getScheduler().runTaskLaterAsynchronously(UhcCore.getPlugin(), task, 24000);
				}
				
			}});
	}

}
