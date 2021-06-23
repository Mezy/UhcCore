package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.LootConfiguration;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.game.handlers.PlayerDeathHandler;
import com.gmail.val59000mc.players.PlayerManager;
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
import java.util.Map;

public class EntityDeathListener implements Listener {

	private final PlayerManager playerManager;
	private final MainConfig config;
	private final PlayerDeathHandler playerDeathHandler;
	
	// Fast mode mob loots
	private final Map<EntityType, LootConfiguration<EntityType>> mobLoots;
	
	public EntityDeathListener(PlayerManager playerManager, MainConfig config, PlayerDeathHandler playerDeathHandler) {
		this.playerManager = playerManager;
		this.config = config;
		mobLoots = config.get(MainConfig.ENABLE_MOB_LOOT) ? config.get(MainConfig.MOB_LOOT) : new HashMap<>();
		this.playerDeathHandler = playerDeathHandler;
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
			lootConfig.getLoot().forEach(item -> event.getDrops().add(item.clone()));
			event.setDroppedExp(lootConfig.getAddXp());

			UhcItems.spawnExtraXp(event.getEntity().getLocation(),lootConfig.getAddXp());
		}
	}
	
	private void handleGoldDrop(EntityDeathEvent event) {
		if (!config.get(MainConfig.ENABLE_GOLD_DROPS)) {
			return;
		}

		if (!config.get(MainConfig.AFFECTED_GOLD_DROP_MOBS).contains(event.getEntityType())) {
			return;
		}

		int chance = config.get(MainConfig.GOLD_DROP_PERCENTAGE);

		if(RandomUtils.randomInteger(0, 100) < chance){
			int min = config.get(MainConfig.MIN_GOLD_DROPS);
			int max = config.get(MainConfig.MAX_GOLD_DROPS);

			int drop = RandomUtils.randomInteger(min, max);
			if(drop > 0){
				ItemStack gold = new ItemStack(Material.GOLD_INGOT,drop);
				event.getDrops().add(gold);
			}
		}
	}
	
	private void handleGhastTearDrop(EntityDeathEvent event){
		if (event.getEntityType() != EntityType.GHAST) {
			return;
		}

		if (config.get(MainConfig.ALLOW_GHAST_TEARS_DROPS)) {
			return;
		}

		// Remove Ghast Tears from drops
		event.getDrops().removeIf(item -> item.getType() == Material.GHAST_TEAR);
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
		for (UhcPlayer player : playerManager.getPlayersList()){
			if (player.getOfflineZombieUuid() != null && player.getOfflineZombieUuid().equals(zombie.getUniqueId())){
				// found player
				uhcPlayer = player;
				break;
			}
		}

		if (uhcPlayer == null){
			return;
		}

		event.getDrops().clear();
		uhcPlayer.setOfflineZombieUuid(null);
		playerDeathHandler.handleOfflinePlayerDeath(uhcPlayer, zombie.getLocation(), zombie.getKiller());
	}

}
