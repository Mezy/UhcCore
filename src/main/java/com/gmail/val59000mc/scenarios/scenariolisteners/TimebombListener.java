package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.scenarios.threads.TimebombThread;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TimebombListener extends ScenarioListener{

    public TimebombListener(){
        super(Scenario.TIMEBOMB);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity().getPlayer();
        List<ItemStack> drops = new ArrayList<>(e.getDrops());
        e.getDrops().removeAll(e.getDrops());

        Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(),new TimebombThread(drops,p.getLocation().getBlock().getLocation(), p.getName()),1L);
    }

}