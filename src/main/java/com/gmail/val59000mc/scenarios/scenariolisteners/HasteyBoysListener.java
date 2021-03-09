package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.scenarios.Option;
import com.gmail.val59000mc.customitems.CraftsManager;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class HasteyBoysListener extends ScenarioListener{

    @Option(key = "efficiency")
    private int efficiency = 3;
    
    @Option(key = "durability")
    private int durability = 1;

    @EventHandler
    public void onPlayerCraft(CraftItemEvent e){
        ItemStack item = e.getCurrentItem();

        // Don't apply hastey boy effets to custom crafted items.
        if (CraftsManager.isCraftItem(item)){
            return;
        }

        try {
            item.addEnchantment(Enchantment.DIG_SPEED,efficiency);
            item.addEnchantment(Enchantment.DURABILITY,durability);
        }catch (IllegalArgumentException ex){
            // Nothing
        }

    }

}