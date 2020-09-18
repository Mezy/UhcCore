package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.events.UhcPreTeleportEvent;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.PlayersManager;
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
        if (!GameManager.getGameManager().getConfiguration().getEnableNether()){
            Bukkit.broadcastMessage(ChatColor.RED + "[UhcCore] For NetherStart the nether needs to be enabled first!");
            getScenarioManager().removeScenario(Scenario.NETHERSTART);
        }
    }

    @EventHandler
    public void onPreTeleport(UhcPreTeleportEvent e){
        GameManager gm = e.getGameManager();
        PlayersManager pm = gm.getPlayersManager();

        World nether = Bukkit.getWorld(gm.getConfiguration().getNetherUuid());
        double maxDistance = 0.9 * (nether.getWorldBorder().getSize()/2);

        for(UhcTeam team : pm.listUhcTeams()){
            Location newLoc = pm.findRandomSafeLocation(nether, maxDistance);
            team.setStartingLocation(newLoc);
        }
    }

}