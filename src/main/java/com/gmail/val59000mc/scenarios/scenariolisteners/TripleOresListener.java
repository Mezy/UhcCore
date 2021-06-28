package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.OreUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class TripleOresListener extends ScenarioListener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

        if (isEnabled(Scenario.VEIN_MINER)) {
            return;
        }

        Block block = e.getBlock();
        Location loc = e.getBlock().getLocation().add(0.5, 0, 0.5);
        Material type = block.getType();

        ItemStack drop = null;
        int xp = 0;

        if (OreUtils.isIronOre(type)) {
            drop = new ItemStack(Material.IRON_INGOT,3);
            xp = 2;
        } else if (OreUtils.isGoldOre(type)) {
            drop = new ItemStack(Material.GOLD_INGOT,3);
            if (isEnabled(Scenario.DOUBLE_GOLD)){
                drop = new ItemStack(Material.GOLD_INGOT,3);
            }
            xp = 3;
        } else if (OreUtils.isDiamondOre(type)) {
            drop = new ItemStack(Material.DIAMOND,3);
            xp = 4;
        } else if (type == Material.SAND) {
            drop = new ItemStack(Material.GLASS);
        } else if (type == Material.GRAVEL) {
            drop = new ItemStack(Material.FLINT);
        }

        if (drop != null) {
            block.setType(Material.AIR);
            loc.getWorld().dropItem(loc, drop);
            UhcItems.spawnExtraXp(loc,xp);
        }
    }

}