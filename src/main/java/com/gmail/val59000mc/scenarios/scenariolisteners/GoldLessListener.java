package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.OreUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public class GoldLessListener extends ScenarioListener{

    @EventHandler (priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent e){
        if (OreUtils.isGoldOre(e.getBlock().getType())){
            e.getBlock().setType(Material.AIR);
        }

    }

}