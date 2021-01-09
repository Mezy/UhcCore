package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.events.UhcPreTeleportEvent;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.UhcTeam;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;

public class NetherStartListener extends ScenarioListener{

    @Override
    public void onEnable(){
        if (!getConfiguration().get(MainConfig.ENABLE_NETHER)){
            Bukkit.broadcastMessage(ChatColor.RED + "[UhcCore] For NetherStart the nether needs to be enabled first!");
            getScenarioManager().disableScenario(Scenario.NETHER_START);
        }
    }

    @EventHandler
    public void onPreTeleport(UhcPreTeleportEvent e){
        World nether = getGameManager().getMapLoader().getUhcWorld(World.Environment.NETHER);
        double maxDistance = 0.9 * (nether.getWorldBorder().getSize()/2);

        for(UhcTeam team : getPlayersManager().listUhcTeams()){
            Location newLoc = getPlayersManager().findRandomSafeLocation(nether, maxDistance);
            team.setStartingLocation(newLoc);
        }
    }

}