package com.gmail.val59000mc.playuhc.mc1_8.threads;

import org.bukkit.Bukkit;

import com.gmail.val59000mc.playuhc.PlayUhc;
import com.gmail.val59000mc.playuhc.mc1_8.game.GameManager;
import com.gmail.val59000mc.playuhc.mc1_8.languages.Lang;

public class Auto20MinBroadcastThread implements Runnable {
	
	boolean broadcast;
	Auto20MinBroadcastThread task;
	
	public Auto20MinBroadcastThread() {
		broadcast = GameManager.getGameManager().getConfiguration().getAuto20MinBroadcast();
		task = this;
	}

	@Override
	public void run() {
		Bukkit.getScheduler().runTask(PlayUhc.getPlugin(), new Runnable(){

			@Override
			public void run() {

				if(broadcast){
					GameManager.getGameManager().broadcastInfoMessage(Lang.DISPLAY_YOUTUBER_MARK);
					Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), task, 24000);
				}
				
			}});
	}

}
