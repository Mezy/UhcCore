package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KillDisconnectedPlayerThread implements Runnable{
	
	private UUID uuid;
	private int timeLeft;
	
	public KillDisconnectedPlayerThread(UUID playerUuid){
		uuid = playerUuid;
		timeLeft = GameManager.getGameManager().getConfiguration().getMaxDisconnectPlayersTime();
	}

	@Override
	public void run() {
		GameManager gm = GameManager.getGameManager();

		if(!gm.getGameState().equals(GameState.PLAYING)) {
			return;
		}

		Player player = Bukkit.getPlayer(uuid);

		if (player != null){
			return; // Player is back online
		}

		if(timeLeft <= 0){
			UhcPlayer uhcPlayer;
			PlayersManager pm = gm.getPlayersManager();
			try {
				uhcPlayer = pm.getUhcPlayer(uuid);
				gm.broadcastInfoMessage(Lang.PLAYERS_ELIMINATED.replace("%player%", uhcPlayer.getName()));
				uhcPlayer.setState(PlayerState.DEAD);
				pm.strikeLightning(uhcPlayer);
				pm.playSoundPlayerDeath();
				pm.checkIfRemainingPlayers();
			} catch (UhcPlayerDoesntExistException e){
				e.printStackTrace();
			}
		}else{
			timeLeft-=5;
			Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), this, 100);
		}
	}

}