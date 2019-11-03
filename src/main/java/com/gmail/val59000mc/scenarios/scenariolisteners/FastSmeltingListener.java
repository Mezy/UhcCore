package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceBurnEvent;

public class FastSmeltingListener extends ScenarioListener{

    public FastSmeltingListener(){
        super(Scenario.FASTSMELTING);
    }

    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent e){
        Furnace furnace = (Furnace) e.getBlock().getState();

        Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), new Runnable() {
            @Override
            public void run() {
                // If the furnace is broken stop thread.
                if (furnace.getBlock().getType() == Material.AIR){
                    return;
                }

                // If furnace almost stopped burning stop thread. A new FurnaceBurnEvent will be called.
                if (furnace.getBurnTime() <= 10) {
                    return;
                }

                // If no item is cooking don't speed up cooking time but show down thread while waiting for the burning to stop.
                if (furnace.getCookTime() <= 0){
                    Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this, 5);
                    return;
                }

                // Speed up cooking time by 10 ticks, this happens every 2 ticks (5x the default speed).
                furnace.setCookTime((short) (furnace.getCookTime() + 10));
                furnace.update();
                Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this, 2);
            }
        }, 1);

    }

}