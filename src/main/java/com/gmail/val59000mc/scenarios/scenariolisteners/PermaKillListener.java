package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PermaKillListener extends ScenarioListener{

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        World world = getGameManager().getMapLoader().getUhcWorld(World.Environment.NORMAL);
        world.setTime(world.getTime() + 12000);
    }

}