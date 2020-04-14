package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.scenarios.ScenarioListener;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
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

    UhcCore plugin = UhcCore.pl;
    private int DoorsPlaced;
    private HashMap<Integer, Location> DoorLocs = new HashMap<>();

    @EventHandler (ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {

        Block block = e.getBlock();
        Location loc = e.getBlock().getLocation();

        if(!block.getType().toString().contains("TRAP") && block.getType().toString().contains("DOOR")) {
            block.setMetadata("DoorNum", new FixedMetadataValue(plugin, DoorsPlaced));
            DoorLocs.put(DoorsPlaced, loc);
            DoorsPlaced++;
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
            if (action == Action.RIGHT_CLICK_BLOCK){
                if (door.getHalf().toString() == "TOP" || door.getHalf().toString() == "UPPER") {
                    block = block.getRelative(BlockFace.DOWN, 1);
                }
                if (block.hasMetadata("DoorNum")) {
                    List<MetadataValue> values = block.getMetadata("DoorNum");
                    doorClicked = values.get(0).asInt();
                }
                if (DoorsPlaced > 1){
                    do {
                        randomDoor = (int) (Math.random() * DoorLocs.size());
                    } while (randomDoor == doorClicked);
                    Location gotoloc = DoorLocs.get(randomDoor);
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
