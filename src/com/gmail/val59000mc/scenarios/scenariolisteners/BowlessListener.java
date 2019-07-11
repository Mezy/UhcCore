package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class BowlessListener extends ScenarioListener{

    public BowlessListener(){
        super(Scenario.BOWLESS);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {
        ItemStack item = e.getCurrentItem();

        if (item.getType().equals(Material.BOW) || item.getType().equals(Material.ARROW)) {
            e.getWhoClicked().sendMessage(Lang.SCENARIO_BOWLESS_ERROR);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        ItemStack item = e.getEntity().getItemStack();

        if ((item.getType().equals(Material.BOW) || item.getType().equals(Material.ARROW))) {
            e.setCancelled(true);
        }
    }

}