package com.gmail.val59000mc.mc1_13.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.mc1_13.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.mc1_13.game.GameManager;
import com.gmail.val59000mc.mc1_13.game.GameState;
import com.gmail.val59000mc.mc1_13.languages.Lang;
import com.gmail.val59000mc.mc1_13.players.PlayerState;
import com.gmail.val59000mc.mc1_13.players.PlayersManager;
import com.gmail.val59000mc.mc1_13.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KillDisconnectedPlayerThread implements Runnable {
	
	String name;
	int timeLeft;
	KillDisconnectedPlayerThread task;
	
	public KillDisconnectedPlayerThread(String playerName) {
		name = playerName;
		timeLeft = GameManager.getGameManager().getConfiguration().getMaxDisconnectPlayersTime();
		task = this;
	}

	@Override
	public void run() {
		if(GameManager.getGameManager().getGameState().equals(GameState.PLAYING)){
			Bukkit.getScheduler().runTask(UhcCore.getPlugin(), new Runnable(){

					@Override
					public void run() {
						Player player = Bukkit.getPlayer(name);
						if(player == null){
							if(timeLeft <= 0){
								UhcPlayer uhcPlayer;
								GameManager gm = GameManager.getGameManager();
								PlayersManager pm = gm.getPlayersManager();
								try {
									uhcPlayer = pm.getUhcPlayer(name);
									gm.broadcastInfoMessage(Lang.PLAYERS_ELIMINATED.replace("%player%", name));
									uhcPlayer.setState(PlayerState.DEAD);
									pm.strikeLightning(uhcPlayer);
									pm.playSoundPlayerDeath();
									pm.checkIfRemainingPlayers();
								} catch (UhcPlayerDoesntExistException e) {
								}
							}else{
								timeLeft-=5;
								Bukkit.getScheduler().runTaskLaterAsynchronously(UhcCore.getPlugin(), task, 100);
							}
						}
						
					}});
		}
		
	}

}
