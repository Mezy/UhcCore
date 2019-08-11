package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.spigotmc.event.entity.EntityMountEvent;

public class HorselessListener extends ScenarioListener{

    public HorselessListener(){
        super(Scenario.HORSELESS);
    }

    @EventHandler
    public void onHorseRide(EntityMountEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = ((Player) e.getEntity()).getPlayer();

            if (e.getMount().getType().equals(EntityType.HORSE)) {
                p.sendMessage(Lang.SCENARIO_HORSELESS_ERROR);
                e.setCancelled(true);
            }
        }
    }

}