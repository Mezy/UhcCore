package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.scenarios.ScenarioListener;

import java.util.List;
import java.util.HashMap;

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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class MonstersIncListener extends ScenarioListener {

    private int doorsPlaced;
    private HashMap<Integer, Location> doorLocs;

    @EventHandler (ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {

        Block block = e.getBlock();
        Location loc = e.getBlock().getLocation();

        if(!block.getType().toString().contains("TRAP") && block.getType().toString().contains("DOOR")) {
            block.setMetadata("DoorNum", new FixedMetadataValue(UhcCore.getPlugin(UhcCore.class), doorsPlaced));
            doorLocs.put(doorsPlaced, loc);
            doorsPlaced++;
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onBlockClick(PlayerInteractEvent e) {

        Action action = e.getAction();
        Block block = e.getClickedBlock();
        Player player = e.getPlayer();
        int doorClicked = -1;
        int randomDoor;

        if(!block.getType().toString().contains("TRAP") && block.getType().toString().contains("DOOR")) {
            Bisected door = (Bisected) block.getBlockData();
            if (action == Action.RIGHT_CLICK_BLOCK) {
                if (door.getHalf().toString().equals("TOP") || door.getHalf().toString().equals("UPPER")) {
                    block = block.getRelative(BlockFace.DOWN, 1);
                }
                if (block.hasMetadata("DoorNum")) {
                    List<MetadataValue> values = block.getMetadata("DoorNum");
                    doorClicked = values.get(0).asInt();
                }
                if (doorsPlaced > 1) {
                    do {
                        randomDoor = (int) (Math.random() * doorLocs.size());
                    } while (randomDoor == doorClicked);
                    Location gotoloc = doorLocs.get(randomDoor);
                    player.teleport(gotoloc.clone().add(0.5, 0, 0.5));
                }
            }
        }

    }

    @EventHandler (ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {

        Block block = e.getBlock();

        if(!block.getType().toString().contains("TRAP") && block.getType().toString().contains("DOOR")) {
            e.getPlayer().sendMessage("Stop that!");
            e.setCancelled(true);
        }

    }
}
