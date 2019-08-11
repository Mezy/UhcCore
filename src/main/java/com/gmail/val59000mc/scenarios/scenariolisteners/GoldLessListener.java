package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public class GoldLessListener extends ScenarioListener{

    public GoldLessListener(){
        super(Scenario.GOLDLESS);
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent e){
        if (e.getBlock().getType() == Material.GOLD_ORE){
            e.getBlock().setType(Material.AIR);
        }

    }

}