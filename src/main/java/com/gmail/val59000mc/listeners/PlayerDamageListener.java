package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PlayerDamageListener implements Listener{

	private final GameManager gameManager;
	private final boolean friendlyFire;

	public PlayerDamageListener(GameManager gameManager){
		this.gameManager = gameManager;
		friendlyFire = gameManager.getConfig().get(MainConfig.ENABLE_FRIENDLY_FIRE);
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerDamage(EntityDamageByEntityEvent event){
		handlePvpAndFriendlyFire(event);
		handleLightningStrike(event);
		// Handle arrows, tridents, and splash potions
		handleProjectile(event);
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerDamage(EntityDamageEvent event){
		handleAnyDamage(event);
	}
	
	///////////////////////
	// EntityDamageEvent //
	///////////////////////

	private void handleAnyDamage(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			Player player = (Player) event.getEntity();
			PlayerManager pm = gameManager.getPlayerManager();
			UhcPlayer uhcPlayer = pm.getUhcPlayer(player);

			PlayerState uhcPlayerState = uhcPlayer.getState();
			if(uhcPlayerState.equals(PlayerState.WAITING) || uhcPlayerState.equals(PlayerState.DEAD)){
				event.setCancelled(true);
			}

			if (uhcPlayer.isFrozen()){
				event.setCancelled(true);
			}
		}
	}
	
	///////////////////////////////
	// EntityDamageByEntityEvent //
	///////////////////////////////
	
	private void handlePvpAndFriendlyFire(EntityDamageByEntityEvent event){

		PlayerManager pm = gameManager.getPlayerManager();
		
		
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
			if(!gameManager.getPvp()){
				event.setCancelled(true);
				return;
			}
			
			Player damager = (Player) event.getDamager();
			Player damaged = (Player) event.getEntity();
			UhcPlayer uhcDamager = pm.getUhcPlayer(damager);
			UhcPlayer uhcDamaged = pm.getUhcPlayer(damaged);

			if(!friendlyFire && uhcDamager.getState().equals(PlayerState.PLAYING) && uhcDamager.isInTeamWith(uhcDamaged)){
				damager.sendMessage(Lang.PLAYERS_FF_OFF);
				event.setCancelled(true);
			}
		}
	}
	
	private void handleLightningStrike(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof LightningStrike && event.getEntity() instanceof Player){
			event.setCancelled(true);
		}
	}
	
	private void handleProjectile(EntityDamageByEntityEvent event){

		PlayerManager pm = gameManager.getPlayerManager();
		
		if(event.getEntity() instanceof Player && (event.getDamager() instanceof Arrow || event.getDamager() instanceof Trident || event.getDamager() instanceof ThrownPotion)){
			Projectile projectile = (Projectile) event.getDamager();
			final Player shot = (Player) event.getEntity();
			if(projectile.getShooter() instanceof Player){
				
				if(!gameManager.getPvp()){
					event.setCancelled(true);
					return;
				}

				UhcPlayer uhcDamager = pm.getUhcPlayer((Player) projectile.getShooter());
				UhcPlayer uhcDamaged = pm.getUhcPlayer(shot);

				if(!friendlyFire && uhcDamager.getState().equals(PlayerState.PLAYING) && uhcDamager.isInTeamWith(uhcDamaged)){
					uhcDamager.sendMessage(Lang.PLAYERS_FF_OFF);
					event.setCancelled(true);
				}
			}
		}
	}

	// Handle effects of lingering potions
	@EventHandler(priority=EventPriority.NORMAL)
	private void handleLingeringPotion(AreaEffectCloudApplyEvent event){

		PlayerManager pm = gameManager.getPlayerManager();

		// If this area effect cloud was caused by a player (they threw a lingering potion)
		if(event.getEntity().getSource() instanceof Player){

			// Check if this area effect cloud applies a negative effect
			boolean isNegativeEffectPotion = false;
			PotionEffectType potionEffectType = event.getEntity().getBasePotionData().getType().getEffectType();
			if(potionEffectType != null) {
				if (potionEffectType.equals(PotionEffectType.POISON) ||
						potionEffectType.equals(PotionEffectType.BLINDNESS) ||
						potionEffectType.equals(PotionEffectType.HARM) ||
						potionEffectType.equals(PotionEffectType.CONFUSION) ||
						potionEffectType.equals(PotionEffectType.HUNGER) ||
						potionEffectType.equals(PotionEffectType.SLOW) ||
						potionEffectType.equals(PotionEffectType.SLOW_DIGGING) ||
						potionEffectType.equals(PotionEffectType.WITHER) ||
						potionEffectType.equals(PotionEffectType.WEAKNESS)) {
					isNegativeEffectPotion = true;
					}
			}

			// If this area effect cloud does not apply any negative effect(s), no need to cancel it
			if(!isNegativeEffectPotion) {
				return;
			}

			// List of all affected living entities by this area effect cloud
			List<LivingEntity> affectedLivingEntities = event.getAffectedEntities();

			// Make all affected players unaffected if pvp is off
			if(!gameManager.getPvp()){
				affectedLivingEntities.removeIf(livingEntity -> livingEntity instanceof Player);
				return;
			}

			// Make a copy of the affected players list so it can used for the loop while  modifying the original list
			// This is required to not cause a ConcurrentModificationException
			List<LivingEntity> affectedLivingEntitiesCopy = new ArrayList<LivingEntity>(affectedLivingEntities);

			// Go through all the players that this area effect cloud could apply a negative effect to
			for (LivingEntity livingEntity : affectedLivingEntitiesCopy) {
				if(livingEntity instanceof Player) {
					Player affectedPlayer = (Player) livingEntity;

					UhcPlayer uhcDamager = pm.getUhcPlayer((Player) event.getEntity().getSource());
					UhcPlayer uhcDamaged = pm.getUhcPlayer(affectedPlayer);

					// Don't cancel effects for the player who threw the lingering potion
					if(uhcDamager.getName().equals(uhcDamaged.getName())) {
						continue;
					}

					// If friendly fire is off and the player is on the same team, cancel the negative effects being applied to this player and send a warning
					if(!friendlyFire && uhcDamager.getState().equals(PlayerState.PLAYING) && uhcDamager.isInTeamWith(uhcDamaged)){
						uhcDamager.sendMessage(Lang.PLAYERS_FF_OFF);
						affectedLivingEntities.remove(livingEntity);
					}

				}
			}

		}
	}

}