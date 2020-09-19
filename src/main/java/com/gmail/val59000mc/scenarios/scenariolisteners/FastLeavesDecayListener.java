package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.UniversalMaterial;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;

public class FastLeavesDecayListener extends ScenarioListener{

    private final static int DECAY_RANGE = 6;
    private final static BlockFace[] NEIGHBOURS = new BlockFace[]{
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST
    };

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        final Block block = e.getBlock();

        if (!UniversalMaterial.isLog(block.getType())){
            return; // Not a log so breaking it won't cause leaves to decay
        }

        // Delaying as right now the block is still a log and therefor leaves won't decay
        Bukkit.getScheduler().runTask(UhcCore.getPlugin(), () -> onBlockBreak(block));
    }

    @EventHandler
    public void onLeaveDecay(LeavesDecayEvent e){
        onBlockBreak(e.getBlock());
    }

    private void onBlockBreak(Block block){
        for (BlockFace face : NEIGHBOURS) {
            final Block relative = block.getRelative(face);

            if (!UniversalMaterial.isLeaves(relative.getType())){
                continue; // Not a leave so don't fast decay
            }

            if (findLog(relative, DECAY_RANGE)){
                continue; // A log is too close so don't fast decay
            }

            Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), () -> {
                if (!UniversalMaterial.isLeaves(relative.getType())){
                    return; // Double check to make sure the block hasn't changed since
                }

                LeavesDecayEvent event = new LeavesDecayEvent(relative);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    relative.breakNaturally();
                    relative.getWorld().playSound(relative.getLocation(), UniversalSound.BLOCK_GRASS_BREAK.getSound(), 1, 1);
                }
            }, 5);
        }
    }

    private boolean findLog(Block block, int i) {
        if (UniversalMaterial.isLog(block.getType())){
            return true;
        }else if (UniversalMaterial.isLeaves(block.getType())){
            i--;
        }else {
            return false;
        }
        if (i > 0){
            boolean result = false;
            for (BlockFace face : BlockFace.values()) {
                if (face.equals(BlockFace.DOWN) || face.equals(BlockFace.UP) || face.equals(BlockFace.NORTH) ||
                        face.equals(BlockFace.EAST) || face.equals(BlockFace.SOUTH) || face.equals(BlockFace.WEST)) {
                    boolean b = findLog(block.getRelative(face), i);
                    if (b) result = b;
                }
            }
            return result;
        }
        return false;
    }

}