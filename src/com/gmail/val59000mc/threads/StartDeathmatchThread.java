package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.listeners.WaitForDeathmatchListener;
import com.gmail.val59000mc.utils.UniversalSound;
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
		Bukkit.getPluginManager().registerEvents(listener, UhcCore.getPlugin());
	}
	
	@Override
	public void run() {
		Bukkit.getScheduler().runTask(UhcCore.getPlugin(), new Runnable(){

			@Override
			public void run() {

				timeBeforePVP --;
				
				if(timeBeforePVP == 0){
					listener.unregister();
					GameManager.getGameManager().setPvp(true);
					GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_ENABLED);
					GameManager.getGameManager().getPlayersManager().playSoundToAll(UniversalSound.WITHER_SPAWN);
				}else{
					
					if(timeBeforePVP <= 5 || (timeBeforePVP >= 5 && timeBeforePVP%5 == 0)){
						GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_START_IN+" "+timeBeforePVP+"s");
						GameManager.getGameManager().getPlayersManager().playSoundToAll(UniversalSound.CLICK);
					}
					
					if(timeBeforePVP > 0){
						Bukkit.getScheduler().runTaskLaterAsynchronously(UhcCore.getPlugin(), task,20);
					}
				}
				
			}});
	}
}
