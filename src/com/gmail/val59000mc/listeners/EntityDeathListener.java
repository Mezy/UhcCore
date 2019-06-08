package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.configuration.MobLootConfiguration;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EntityDeathListener implements Listener {

	// Gold drops
	private final int min;
	private final int max;
	private final int chance;
	private final List<EntityType> affectedMobs;
	private final boolean allowGhastTearDrop;
	private final boolean enableGoldDrops;
	
	// Fast mode cooked food
	private final boolean enableCookedFood;
	private Map<EntityType, MobLootConfiguration> mobLoots;
	
	public EntityDeathListener() {
		MainConfiguration cfg = GameManager.getGameManager().getConfiguration();
		min = cfg.getMinGoldDrops();
		max = cfg.getMaxGoldDrops();
		chance = cfg.getGoldDropPercentage();
		affectedMobs = cfg.getAffectedGoldDropsMobs();
		allowGhastTearDrop = cfg.getAllowGhastTearsDrops();
		enableGoldDrops = cfg.getEnableGoldDrops();
		enableCookedFood = cfg.getCookedDroppedFood();
		mobLoots = cfg.getEnableMobLoots() ? cfg.getMobLoots() : new HashMap<>();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {

		if (!handleMobLoot(event)){
			handleCookedFoodDrop(event);
		}
		handleGoldDrop(event);
		handleGhastTearDrop(event);

	}
	
	private boolean handleMobLoot(EntityDeathEvent event){
		EntityType entity = event.getEntityType();
		if(mobLoots.containsKey(entity)){
			MobLootConfiguration lootConfig = mobLoots.get(entity);
			event.getDrops().clear();
			event.getDrops().add(lootConfig.getLoot().clone());
			event.setDroppedExp(lootConfig.getAddXp());
			UhcItems.spawnExtraXp(event.getEntity().getLocation(),lootConfig.getAddXp());
			return true;
		}
		return false;
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

	
	private void handleCookedFoodDrop(EntityDeathEvent event){
		if(enableCookedFood){
			for(int i=0 ; i<event.getDrops().size() ; i++){
				UniversalMaterial replaceBy = null;
				UniversalMaterial type = UniversalMaterial.ofType(event.getDrops().get(i).getType());
				if (type != null) {
					switch (type) {
						case RAW_BEEF:
							replaceBy = UniversalMaterial.COOKED_BEEF;
							break;
						case RAW_CHICKEN:
							replaceBy = UniversalMaterial.COOKED_CHICKEN;
							break;
						case RAW_MUTTON:
							replaceBy = UniversalMaterial.COOKED_MUTTON;
							break;
						case RAW_RABBIT:
							replaceBy = UniversalMaterial.COOKED_RABBIT;
							break;
						case RAW_PORK:
							replaceBy = UniversalMaterial.COOKED_PORKCHOP;
							break;
						default:
							break;
					}
				}
				if(replaceBy != null){
					ItemStack cookedFood = event.getDrops().get(i).clone();
					cookedFood.setType(replaceBy.getType());
					event.getDrops().set(i, cookedFood);
				}
			}
		}
	}
}
