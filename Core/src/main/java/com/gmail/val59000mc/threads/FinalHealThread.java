package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;

public class FinalHealThread implements Runnable{

	private GameManager gm;
	private PlayersManager pm;

	public FinalHealThread(){
		gm = GameManager.getGameManager();
		pm = gm.getPlayersManager();
	}
	
	@Override
	public void run() {

		for (UhcPlayer uhcPlayer : pm.getOnlinePlayingPlayers()){
			try {
				Player bukkitPlayer = uhcPlayer.getPlayer();
				bukkitPlayer.setHealth(bukkitPlayer.getMaxHealth());
			}catch (UhcPlayerNotOnlineException ex){
				// no heal for offline players
			}
		}

		gm.broadcastInfoMessage(Lang.GAME_FINAL_HEAL);
	}

}