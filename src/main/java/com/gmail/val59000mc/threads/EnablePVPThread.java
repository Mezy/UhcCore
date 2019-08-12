package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;

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
						GameManager.getGameManager().getPlayersManager().playSoundToAll(UniversalSound.WITHER_SPAWN);
					}else{
						
						if(timeBeforePVP <= 10 || timeBeforePVP%60 == 0){
							if(timeBeforePVP%60 == 0)
								GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_START_IN+" "+(timeBeforePVP/60)+"m");
							else
								GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_START_IN+" "+timeBeforePVP+"s");
							
							GameManager.getGameManager().getPlayersManager().playSoundToAll(UniversalSound.CLICK);
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
