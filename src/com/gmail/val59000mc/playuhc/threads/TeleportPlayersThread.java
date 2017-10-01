package com.gmail.val59000mc.playuhc.threads;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.val59000mc.playuhc.game.GameManager;
import com.gmail.val59000mc.playuhc.players.UhcPlayer;
import com.gmail.val59000mc.playuhc.players.UhcTeam;

public class TeleportPlayersThread implements Runnable {
	
	UhcTeam team;
	
	public TeleportPlayersThread(UhcTeam team) {
		this.team = team;
	}

	@Override
	public void run() {
		
		for(UhcPlayer uhcPlayer : team.getMembers()){
			Player player = Bukkit.getPlayer(uhcPlayer.getName());
			if(player != null){
				Bukkit.getLogger().info("[PlayUHC] Teleporting "+player.getName());
				for(PotionEffect effect : GameManager.getGameManager().getConfiguration().getPotionEffectOnStart()){
					player.addPotionEffect(effect);
				}
				player.teleport(team.getStartingLocation());
				player.removePotionEffect(PotionEffectType.BLINDNESS);
				player.setFireTicks(0);
				uhcPlayer.setHasBeenTeleportedToLocation(true);
			}
		}
		
		
	}

}
