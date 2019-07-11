package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class RodlessListener extends ScenarioListener {

    public RodlessListener(){
        super(Scenario.RODLESS);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {
        ItemStack item = e.getCurrentItem();

        if (item.getType().equals(Material.FISHING_ROD)) {
            e.getWhoClicked().sendMessage(Lang.SCENARIO_RODLESS_ERROR);
            e.setCancelled(true);
        }
    }

}