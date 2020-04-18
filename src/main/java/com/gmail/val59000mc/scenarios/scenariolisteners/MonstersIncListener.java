package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.scenarios.ScenarioListener;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.event.block.Action;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import com.gmail.val59000mc.languages.Lang;

public class MonstersIncListener extends ScenarioListener {

    private int doorsPlaced;
    private List<Location> doorLocs = new ArrayList<>();

    public static boolean isDoor(Block b) {
        if(!b.getType().toString().contains("TRAP") && b.getType().toString().contains("DOOR")) {
            return true;
        }
        return false;
    }

    @EventHandler (ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {

        Block block = e.getBlock();
        Location loc = e.getBlock().getLocation();

        if(isDoor(block)) {
            doorLocs.add(loc);
            doorsPlaced++;
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onBlockClick(PlayerInteractEvent e) {

        Action action = e.getAction();
        Block block = e.getClickedBlock();
        Player player = e.getPlayer();
        Location goToLoc;
        int randomDoor;

        if(isDoor(block)) {
            Bisected door = (Bisected) block.getBlockData();
            if (action == Action.RIGHT_CLICK_BLOCK) {
                if (door.getHalf().toString().equals("TOP")) {
                    block = block.getRelative(BlockFace.DOWN, 1);
                }
                if (doorsPlaced > 1) {
                    do {
                        randomDoor = (int) (Math.random() * doorLocs.size());
                        goToLoc = doorLocs.get(randomDoor);
                    } while (goToLoc.equals(block.getLocation()));
                    player.teleport(goToLoc.clone().add(0.5, 0, 0.5));
                }
            }
        }

    }

    @EventHandler (ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {

        Block block = e.getBlock();

        if(isDoor(block)) {
            e.getPlayer().sendMessage(Lang.SCENARIO_MONSTERSINC_ERROR);
            e.setCancelled(true);
        }

    }
}
