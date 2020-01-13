package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;

public class StartDeathmatchThread implements Runnable{

	private int timeBeforePVP;
	private boolean shrinkBorder;

	public StartDeathmatchThread(boolean shrinkBorder){
		this.timeBeforePVP = 31;
		this.shrinkBorder = shrinkBorder;
	}
	
	@Override
	public void run() {
		timeBeforePVP --;

		if(timeBeforePVP == 0){
			GameManager gm = GameManager.getGameManager();
			gm.setPvp(true);
			gm.broadcastInfoMessage(Lang.PVP_ENABLED);
			gm.getPlayersManager().playSoundToAll(UniversalSound.WITHER_SPAWN);
			gm.getPlayersManager().setLastDeathTime();

			for (UhcPlayer uhcPlayer : gm.getPlayersManager().getPlayersList()){
				uhcPlayer.releasePlayer();
			}

			// If center deathmatch move border.
			if (shrinkBorder){
				gm.getLobby().getLoc().getWorld().getWorldBorder().setSize(gm.getConfiguration().getDeathmatchEndSize(), gm.getConfiguration().getDeathmatchTimeToShrink());
			}
		}else{

			if(timeBeforePVP <= 5 || (timeBeforePVP%5 == 0)){
				GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_START_IN+" "+timeBeforePVP+"s");
				GameManager.getGameManager().getPlayersManager().playSoundToAll(UniversalSound.CLICK);
			}

			if(timeBeforePVP > 0){
				Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this,20);
			}
		}
	}

}