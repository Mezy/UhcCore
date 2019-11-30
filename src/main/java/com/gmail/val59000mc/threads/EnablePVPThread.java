package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;

public class EnablePVPThread implements Runnable{

	private GameManager gm;
	private int timeBeforePvp;
	
	public EnablePVPThread(){
		gm = GameManager.getGameManager();
		timeBeforePvp = gm.getConfiguration().getTimeBeforePvp();
	}
	
	@Override
	public void run() {
		if(!gm.getGameState().equals(GameState.PLAYING)) {
			return; // Stop thread
		}

		if(timeBeforePvp == 0){
			GameManager.getGameManager().setPvp(true);
			GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_ENABLED);
			GameManager.getGameManager().getPlayersManager().playSoundToAll(UniversalSound.WITHER_SPAWN);
			return; // Stop thread
		}

		if(timeBeforePvp <= 10 || timeBeforePvp%60 == 0){
			if(timeBeforePvp%60 == 0) {
				gm.broadcastInfoMessage(Lang.PVP_START_IN + " " + (timeBeforePvp / 60) + "m");
			}else{
				gm.broadcastInfoMessage(Lang.PVP_START_IN + " " + timeBeforePvp + "s");
			}

			gm.getPlayersManager().playSoundToAll(UniversalSound.CLICK);
		}

		if(timeBeforePvp >= 20){
			timeBeforePvp -= 10;
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this,200);
		}else{
			timeBeforePvp --;
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this,20);
		}

	}

}