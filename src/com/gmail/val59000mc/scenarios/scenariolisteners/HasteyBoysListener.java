package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class HasteyBoysListener extends ScenarioListener{

    public HasteyBoysListener() {
        super(Scenario.HASTEYBOYS);
    }

    @EventHandler
    public void onPlayerCraft(CraftItemEvent e){
        ItemStack item = e.getCurrentItem();

        try {
            item.addEnchantment(Enchantment.DIG_SPEED,3);
            item.addEnchantment(Enchantment.DURABILITY,1);
        }catch (IllegalArgumentException ex){
            // Nothing
        }

    }

}