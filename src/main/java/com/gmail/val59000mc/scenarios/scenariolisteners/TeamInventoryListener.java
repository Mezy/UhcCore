package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.players.UhcTeam;
import com.gmail.val59000mc.scenarios.Option;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class TeamInventoryListener extends ScenarioListener{

    @Option(key = "drop-on-team-elimination")
    private boolean dropOnLastDeath = false;

    public void dropTeamInventory(UhcTeam team, Location location)
    {
        if (dropOnLastDeath)
        {
            World world = location.getWorld();
            for (ItemStack stack : team.getTeamInventory().getContents())
            {
                if (stack != null)
                {
                    world.dropItemNaturally(location, stack);
                }
            }
            team.getTeamInventory().clear();
        }
    }
}
