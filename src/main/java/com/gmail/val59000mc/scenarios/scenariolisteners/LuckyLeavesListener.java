package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

public class LuckyLeavesListener extends ScenarioListener{

    public LuckyLeavesListener(){
        super(Scenario.LUCKYLEAVES);
    }

    @EventHandler
    public void onLeaveDecay(LeavesDecayEvent e){
        int random = RandomUtils.randomInteger(0, 200);

        if (random > 1){
            return;
        }

        // add gapple
        e.getBlock().getWorld().dropItem(e.getBlock().getLocation().add(.5,0,.5),new ItemStack(Material.GOLDEN_APPLE));
    }

}