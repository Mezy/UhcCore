package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;

public class StartDeathmatchThread implements Runnable{

	private final GameManager gameManager;
	private int timeBeforePVP;
	private final boolean shrinkBorder;

	public StartDeathmatchThread(GameManager gameManager, boolean shrinkBorder){
		this.gameManager = gameManager;
		this.timeBeforePVP = 31;
		this.shrinkBorder = shrinkBorder;
	}
	
	@Override
	public void run() {
		timeBeforePVP --;

		if(timeBeforePVP == 0){
			gameManager.setPvp(true);
			gameManager.broadcastInfoMessage(Lang.PVP_ENABLED);
			gameManager.getPlayersManager().playSoundToAll(UniversalSound.WITHER_SPAWN);
			gameManager.getPlayersManager().setLastDeathTime();

			for (UhcPlayer uhcPlayer : gameManager.getPlayersManager().getPlayersList()){
				uhcPlayer.releasePlayer();
			}

			// If center deathmatch move border.
			if (shrinkBorder){
				gameManager.getLobby().getLoc().getWorld().getWorldBorder().setSize(gameManager.getConfiguration().getDeathmatchEndSize(), gameManager.getConfiguration().getDeathmatchTimeToShrink());
				gameManager.getLobby().getLoc().getWorld().getWorldBorder().setDamageBuffer(1);
			}
		}else{

			if(timeBeforePVP <= 5 || (timeBeforePVP%5 == 0)){
				gameManager.broadcastInfoMessage(Lang.PVP_START_IN+" "+timeBeforePVP+"s");
				gameManager.getPlayersManager().playSoundToAll(UniversalSound.CLICK);
			}

			if(timeBeforePVP > 0){
				Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this,20);
			}
		}
	}

}