package com.gmail.val59000mc.playuhc.mc1_13.threads;

import com.gmail.val59000mc.playuhc.PlayUhc;
import com.gmail.val59000mc.playuhc.mc1_13.game.GameManager;
import com.gmail.val59000mc.playuhc.mc1_13.languages.Lang;
import com.gmail.val59000mc.playuhc.mc1_13.listeners.WaitForDeathmatchListener;
import com.gmail.val59000mc.playuhc.mc1_13.sounds.UhcSound;
import org.bukkit.Bukkit;

public class StartDeathmatchThread implements Runnable{

	private int timeBeforePVP;
	private StartDeathmatchThread task;
	private WaitForDeathmatchListener listener;
	
	
	public StartDeathmatchThread(){
		this.timeBeforePVP = 31;
		this.task = this;
		this.listener = new WaitForDeathmatchListener();
		GameManager.getGameManager().getPlayersManager().setAllPlayersStartDeathmatch();
		Bukkit.getPluginManager().registerEvents(listener, PlayUhc.getPlugin());
	}
	
	@Override
	public void run() {
		Bukkit.getScheduler().runTask(PlayUhc.getPlugin(), new Runnable(){

			@Override
			public void run() {

				timeBeforePVP --;
				
				if(timeBeforePVP == 0){
					listener.unregister();
					GameManager.getGameManager().setPvp(true);
					GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_ENABLED);
					GameManager.getGameManager().getPlayersManager().playSoundToAll(UhcSound.WITHER_SPAWN);
				}else{
					
					if(timeBeforePVP <= 5 || (timeBeforePVP >= 5 && timeBeforePVP%5 == 0)){
						GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_START_IN+" "+timeBeforePVP+"s");
						GameManager.getGameManager().getPlayersManager().playSoundToAll(UhcSound.CLICK);
					}
					
					if(timeBeforePVP > 0){
						Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), task,20);
					}
				}
				
			}});
	}
}
