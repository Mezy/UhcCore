package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TeleportPlayersThread implements Runnable{

	private final GameManager gameManager;
	private final UhcTeam team;
	
	public TeleportPlayersThread(GameManager gameManager, UhcTeam team) {
		this.gameManager = gameManager;
		this.team = team;
	}

	@Override
	public void run() {
		
		for(UhcPlayer uhcPlayer : team.getMembers()){
			Player player;
			try {
				player = uhcPlayer.getPlayer();
			}catch (UhcPlayerNotOnlineException ex){
				continue;
			}

			Bukkit.getLogger().info("[UhcCore] Teleporting "+player.getName());

			for(PotionEffect effect : gameManager.getConfiguration().getPotionEffectOnStart()){
				player.addPotionEffect(effect);
			}

			uhcPlayer.freezePlayer(team.getStartingLocation());
			player.teleport(team.getStartingLocation());
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
			player.setFireTicks(0);
			uhcPlayer.setHasBeenTeleportedToLocation(true);
		}
	}

}