package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.configuration.MobLootConfiguration;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EntityDeathListener implements Listener {

	private final PlayersManager playersManager;

	// Gold drops
	private final int min;
	private final int max;
	private final int chance;
	private final List<EntityType> affectedMobs;
	private final boolean allowGhastTearDrop;
	private final boolean enableGoldDrops;
	
	// Fast mode mob loots
	private final Map<EntityType, MobLootConfiguration> mobLoots;
	
	public EntityDeathListener(PlayersManager playersManager, MainConfiguration configuration) {
		this.playersManager = playersManager;
		min = configuration.getMinGoldDrops();
		max = configuration.getMaxGoldDrops();
		chance = configuration.getGoldDropPercentage();
		affectedMobs = configuration.getAffectedGoldDropsMobs();
		allowGhastTearDrop = configuration.getAllowGhastTearsDrops();
		enableGoldDrops = configuration.getEnableGoldDrops();
		mobLoots = configuration.getEnableMobLoots() ? configuration.getMobLoots() : new HashMap<>();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		handleMobLoot(event);
		handleGoldDrop(event);
		handleGhastTearDrop(event);
		handleOfflineZombieDeath(event);
	}
	
	private void handleMobLoot(EntityDeathEvent event){
		EntityType entity = event.getEntityType();
		if(mobLoots.containsKey(entity)){
			MobLootConfiguration lootConfig = mobLoots.get(entity);
			event.getDrops().clear();
			event.getDrops().add(lootConfig.getLoot().clone());
			event.setDroppedExp(lootConfig.getAddXp());
			UhcItems.spawnExtraXp(event.getEntity().getLocation(),lootConfig.getAddXp());
		}
	}
	
	private void handleGoldDrop(EntityDeathEvent event){
		if(enableGoldDrops && affectedMobs.contains(event.getEntityType())){
			Random r = new Random();
			if(r.nextInt(100) < chance){
				int drop;
				try{
					drop = min+r.nextInt(1+max-min);
				}catch(IllegalArgumentException e){
					drop=0;
				}
				if(drop > 0){
					ItemStack gold = new ItemStack(Material.GOLD_INGOT,drop);
					event.getDrops().add(gold);
				}
			}			
		}
	}
	
	private void handleGhastTearDrop(EntityDeathEvent event){
		if(event.getEntityType().equals(EntityType.GHAST) && !allowGhastTearDrop){
			for(int i = event.getDrops().size()-1 ; i>=0 ; i--){
				if(event.getDrops().get(i).getType().equals(Material.GHAST_TEAR)){
					event.getDrops().remove(i);
				}
			}
		}
	}

	private void handleOfflineZombieDeath(EntityDeathEvent event){
		if (event.getEntityType() != EntityType.ZOMBIE){
			return;
		}

		Zombie zombie = (Zombie) event.getEntity();

		if (zombie.getCustomName() == null){
			return;
		}

		UhcPlayer uhcPlayer = null;
		for (UhcPlayer player : playersManager.getPlayersList()){
			if (player.getOfflineZombie() != null && player.getOfflineZombie().equals(zombie)){
				// found player
				uhcPlayer = player;
				break;
			}
		}

		if (uhcPlayer == null){
			return;
		}

		event.getDrops().clear();
		uhcPlayer.setOfflineZombie(null);
		playersManager.killOfflineUhcPlayer(uhcPlayer, zombie.getLocation(), new HashSet<>(uhcPlayer.getStoredItems()), zombie.getKiller());
	}

}