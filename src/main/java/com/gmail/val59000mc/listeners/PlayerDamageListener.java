package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class PlayerDamageListener implements Listener{
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerDamage(EntityDamageByEntityEvent event){
		handlePvpAndFriendlyFire(event);
		handleLightningStrike(event);
		handleArrow(event);
	}
	

	
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerDamage(EntityDamageEvent event){
		handleAnyDamage(event);
		handleFallDamage(event);
	}
	
	
	///////////////////////
	// EntityDamageEvent //
	///////////////////////
	
	private void handleAnyDamage(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			Player player = (Player) event.getEntity();
			PlayersManager pm = GameManager.getGameManager().getPlayersManager();
			PlayerState uhcPlayerState;
			try {
				uhcPlayerState = pm.getUhcPlayer(player).getState();
				if(uhcPlayerState.equals(PlayerState.WAITING) || uhcPlayerState.equals(PlayerState.DEAD)){
					event.setCancelled(true);
				}
			} catch (UhcPlayerDoesntExistException e) {
			}
		}
	}
	
	private void handleFallDamage(EntityDamageEvent event){
		if(event.getCause().equals(DamageCause.FALL)){
			if(GameManager.getGameManager().getConfiguration().getDisableFallDamage()){
				event.setCancelled(true);
			}
		}
	}
	
	///////////////////////////////
	// EntityDamageByEntityEvent //
	///////////////////////////////
	
	private void handlePvpAndFriendlyFire(EntityDamageByEntityEvent event){

		PlayersManager pm = GameManager.getGameManager().getPlayersManager();
		
		
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
			if(!GameManager.getGameManager().getPvp()){
				event.setCancelled(true);
				return;
			}
			
			Player damager = (Player) event.getDamager();
			Player damaged = (Player) event.getEntity();
			UhcPlayer uhcDamager;
			UhcPlayer uhcDamaged;
			try {
				uhcDamager = pm.getUhcPlayer(damager);
				uhcDamaged = pm.getUhcPlayer(damaged);

				if(uhcDamager.getState().equals(PlayerState.PLAYING) && uhcDamager.isInTeamWith(uhcDamaged)){
					damager.sendMessage(ChatColor.GRAY+Lang.PLAYERS_FF_OFF);
					event.setCancelled(true);
				}
			} catch (UhcPlayerDoesntExistException e) {
			}
		}
	}
	
	private void handleLightningStrike(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof LightningStrike && event.getEntity() instanceof Player){
			event.setCancelled(true);
		}
	}
	
	private void handleArrow(EntityDamageByEntityEvent event){

		PlayersManager pm = GameManager.getGameManager().getPlayersManager();
		
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Arrow){
			Projectile arrow = (Projectile) event.getDamager();
			final Player shot = (Player) event.getEntity();
			if(arrow.getShooter() instanceof Player){
				
				if(!GameManager.getGameManager().getPvp()){
					event.setCancelled(true);
					return;
				}
				
				final Player shooter = (Player) arrow.getShooter();
				try {
					UhcPlayer uhcDamager = pm.getUhcPlayer(shooter);
					UhcPlayer uhcDamaged = pm.getUhcPlayer(shot);

					if(uhcDamager.getState().equals(PlayerState.PLAYING) && uhcDamager.isInTeamWith(uhcDamaged)){
						shooter.sendMessage(ChatColor.GRAY+Lang.PLAYERS_FF_OFF);
						event.setCancelled(true);
					}
				} catch (UhcPlayerDoesntExistException e) {
				}
			}
		}
	}
}
