package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BloodDiamondsListener extends ScenarioListener{

    public BloodDiamondsListener(){
        super(Scenario.BLOODDIAMONDS);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();

        if (e.getBlock().getType().equals(Material.DIAMOND_ORE)) {

            p.getWorld().playSound(p.getLocation(), UniversalSound.PLAYER_HURT.getSound(), 1, 1);

            if (p.getHealth() < 1){
                p.setHealth(0);
            }else {
                p.setHealth(p.getHealth() - 1);
            }
        }

    }

}