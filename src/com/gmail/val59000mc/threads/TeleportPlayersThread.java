package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TeleportPlayersThread implements Runnable {
	
	private UhcTeam team;
	
	public TeleportPlayersThread(UhcTeam team) {
		this.team = team;
	}

	@Override
	public void run() {
		
		for(UhcPlayer uhcPlayer : team.getMembers()){
			Player player = Bukkit.getPlayer(uhcPlayer.getName());
			if(player != null){
				Bukkit.getLogger().info("[UhcCore] Teleporting "+player.getName());
				for(PotionEffect effect : GameManager.getGameManager().getConfiguration().getPotionEffectOnStart()){
					player.addPotionEffect(effect);
				}
				player.teleport(team.getStartingLocation());
				player.removePotionEffect(PotionEffectType.BLINDNESS);
				player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
				player.setFireTicks(0);
				uhcPlayer.setHasBeenTeleportedToLocation(true);
			}
		}
		
		
	}

}
