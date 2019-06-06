package com.gmail.val59000mc.mc1_13.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.mc1_13.game.GameManager;
import com.gmail.val59000mc.mc1_13.game.GameState;
import com.gmail.val59000mc.mc1_13.languages.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

public class EnablePVPThread implements Runnable{

	int timeBeforePVP;
	EnablePVPThread task;
	GameManager gm;
	
	public EnablePVPThread(){
		timeBeforePVP = GameManager.getGameManager().getConfiguration().getTimeBeforePvp();;
		task = this;
		gm = GameManager.getGameManager();
	}
	
	@Override
	public void run() {
		Bukkit.getScheduler().runTask(UhcCore.getPlugin(), new Runnable(){

			@Override
			public void run() {
				
				if(gm.getGameState().equals(GameState.PLAYING)){

					if(timeBeforePVP == 0){
						GameManager.getGameManager().setPvp(true);
						GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_ENABLED);
						GameManager.getGameManager().getPlayersManager().playSoundToAll(Sound.ENTITY_WITHER_SPAWN);
					}else{
						
						if(timeBeforePVP <= 10 || timeBeforePVP%60 == 0){
							if(timeBeforePVP%60 == 0)
								GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_START_IN+" "+(timeBeforePVP/60)+"m");
							else
								GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_START_IN+" "+timeBeforePVP+"s");
							
							GameManager.getGameManager().getPlayersManager().playSoundToAll(Sound.UI_BUTTON_CLICK);
						}
						
						if(timeBeforePVP >= 20){
							timeBeforePVP -= 10;
							Bukkit.getScheduler().runTaskLaterAsynchronously(UhcCore.getPlugin(), task,200);
						}else{
							timeBeforePVP --;
							Bukkit.getScheduler().runTaskLaterAsynchronously(UhcCore.getPlugin(), task,20);
						}
					}
				}

				
			}});
	}
}
