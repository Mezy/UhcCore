package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class ShieldlessListener extends ScenarioListener {

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {
        ItemStack item = e.getCurrentItem();

        if (item.getType() != Material.AIR && item.getType() == UniversalMaterial.SHIELD.getType()){
            e.getWhoClicked().sendMessage(Lang.SCENARIO_SHIELDLESS_ERROR);
            e.setCancelled(true);
        }
    }

}