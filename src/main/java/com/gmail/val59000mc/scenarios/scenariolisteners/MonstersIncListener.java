package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.scenarios.ScenarioListener;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import com.gmail.val59000mc.languages.Lang;

public class MonstersIncListener extends ScenarioListener {

    private final List<Location> doorLocs;

    public MonstersIncListener(){
        doorLocs = new ArrayList<>();
    }

    @EventHandler (ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        Location loc = e.getBlock().getLocation();

        if(isDoor(block)) {
            doorLocs.add(loc);
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onBlockClick(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        Player player = e.getPlayer();
        Location goToLoc;

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK){
            return;
        }

        if (block == null){
            return;
        }

        if(isDoor(block)) {
            Block below = block.getRelative(BlockFace.DOWN, 1);
            if (isDoor(below)) {
                block = below;
            }

            if (doorLocs.size() > 1) {
                do {
                    goToLoc = doorLocs.get((int) (Math.random() * doorLocs.size()));
                    // Door loc is no longer valid.
                    if (!isValidDoorLocation(goToLoc)){
                        doorLocs.remove(goToLoc);
                        goToLoc = null;
                    }
                } while ((goToLoc == null || goToLoc.equals(block.getLocation())) && doorLocs.size() > 1);
                if (goToLoc != null) {
                    player.teleport(goToLoc.clone().add(0.5, 0, 0.5));
                }
            }
        }
    }

    private boolean isValidDoorLocation(Location loc){
        return isDoor(loc.getBlock()) && GameManager.getGameManager().getWorldBorder().isWithinBorder(loc);
    }

    @EventHandler (ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        if(isDoor(block)) {
            e.getPlayer().sendMessage(Lang.SCENARIO_MONSTERSINC_ERROR);
            e.setCancelled(true);
        }
    }

    private boolean isDoor(Block b) {
        return !b.getType().toString().contains("TRAP") && b.getType().toString().contains("DOOR");
    }

}