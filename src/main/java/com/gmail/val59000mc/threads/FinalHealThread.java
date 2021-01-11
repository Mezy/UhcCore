package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;

public class FinalHealThread implements Runnable{

	private final GameManager gameManager;
	private final PlayerManager playerManager;

	public FinalHealThread(GameManager gameManager, PlayerManager playerManager){
		this.gameManager = gameManager;
		this.playerManager = playerManager;
	}
	
	@Override
	public void run() {

		for (UhcPlayer uhcPlayer : playerManager.getOnlinePlayingPlayers()){
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