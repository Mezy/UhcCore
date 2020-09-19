package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;

public class FinalHealThread implements Runnable{

	private final GameManager gameManager;
	private final PlayersManager playersManager;

	public FinalHealThread(GameManager gameManager, PlayersManager playersManager){
		this.gameManager = gameManager;
		this.playersManager = playersManager;
	}
	
	@Override
	public void run() {

		for (UhcPlayer uhcPlayer : playersManager.getOnlinePlayingPlayers()){
			try {
				Player bukkitPlayer = uhcPlayer.getPlayer();
				bukkitPlayer.setHealth(bukkitPlayer.getMaxHealth());
			}catch (UhcPlayerNotOnlineException ex){
				// no heal for offline players
			}
		}

		gameManager.broadcastInfoMessage(Lang.GAME_FINAL_HEAL);
	}

}