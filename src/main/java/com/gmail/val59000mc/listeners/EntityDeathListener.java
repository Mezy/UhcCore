package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.LootConfiguration;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
	private final Map<EntityType, LootConfiguration<EntityType>> mobLoots;
	
	public EntityDeathListener(PlayersManager playersManager, MainConfig configuration) {
		this.playersManager = playersManager;
		min = configuration.get(MainConfig.MIN_GOLD_DROPS);
		max = configuration.get(MainConfig.MAX_GOLD_DROPS);
		chance = configuration.get(MainConfig.GOLD_DROP_PERCENTAGE);
		affectedMobs = configuration.get(MainConfig.AFFECTED_GOLD_DROP_MOBS);
		allowGhastTearDrop = configuration.get(MainConfig.ALLOW_GHAST_TEARS_DROPS);
		enableGoldDrops = configuration.get(MainConfig.ENABLE_GOLD_DROPS);
		mobLoots = configuration.get(MainConfig.ENABLE_MOB_LOOT) ? configuration.get(MainConfig.MOB_LOOT) : new HashMap<>();
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
			LootConfiguration<EntityType> lootConfig = mobLoots.get(entity);
			event.getDrops().clear();
			event.getDrops().add(lootConfig.getLoot().clone());
			event.setDroppedExp(lootConfig.getAddXp());
			UhcItems.spawnExtraXp(event.getEntity().getLocation(),lootConfig.getAddXp());
		}
	}
	
	private void handleGoldDrop(EntityDeathEvent event){
		if(enableGoldDrops && affectedMobs.contains(event.getEntityType())){
			if(RandomUtils.randomInteger(0, 100) < chance){
				int drop = RandomUtils.randomInteger(min, max);
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