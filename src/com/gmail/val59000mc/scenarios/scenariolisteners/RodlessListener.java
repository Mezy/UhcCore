package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.ChatColor;
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
            e.getWhoClicked().sendMessage(ChatColor.RED + "Rodless is turned on.");
            e.setCancelled(true);
        }
    }

}