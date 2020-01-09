package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.scenarios.Option;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceBurnEvent;

public class FastSmeltingListener extends ScenarioListener{

    @Option
    private int speed = 10;

    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent e){
        Block block = e.getBlock();

        Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), new Runnable() {
            @Override
            public void run() {
                // If the furnace is broken stop thread.
                if (block.getType() == Material.AIR){
                    return;
                }

                Furnace furnace = (Furnace) block.getState();

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
                short newCookTime = (short) (furnace.getCookTime() + speed);

                // If new cook time is greater than the max cook time of item set to 199.
                if (newCookTime >= 200){
                    newCookTime = 199;
                }

                furnace.setCookTime(newCookTime);
                furnace.update();
                Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this, 2);
            }
        }, 1);
    }

}