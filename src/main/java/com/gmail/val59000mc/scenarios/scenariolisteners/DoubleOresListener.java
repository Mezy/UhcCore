package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class DoubleOresListener extends ScenarioListener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

        if (isEnabled(Scenario.VEIN_MINER)) {
            return;
        }

        Block block = e.getBlock();
        Location loc = e.getBlock().getLocation().add(0.5, 0, 0.5);

        switch (block.getType()) {
            case IRON_ORE:
                block.setType(Material.AIR);
                loc.getWorld().dropItem(loc, new ItemStack(Material.IRON_INGOT,2));
                UhcItems.spawnExtraXp(loc,2);
                break;
            case GOLD_ORE:
                block.setType(Material.AIR);
                loc.getWorld().dropItem(loc, new ItemStack(Material.GOLD_INGOT,2));
                if (isEnabled(Scenario.DOUBLE_GOLD)){
                    loc.getWorld().dropItem(loc, new ItemStack(Material.GOLD_INGOT,2));
                }
                UhcItems.spawnExtraXp(loc,3);
                break;
            case DIAMOND_ORE:
                block.setType(Material.AIR);
                loc.getWorld().dropItem(loc, new ItemStack(Material.DIAMOND,2));
                UhcItems.spawnExtraXp(loc,4);
                break;
            case SAND:
                block.setType(Material.AIR);
                loc.getWorld().dropItem(loc, new ItemStack(Material.GLASS));
                break;
            case GRAVEL:
                block.setType(Material.AIR);
                loc.getWorld().dropItem(loc,new ItemStack(Material.FLINT));
                break;
        }

    }

}