package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.scenarios.Option;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class TimberListener extends ScenarioListener {

    @Option(key = "calculate-axe-damage")
    private boolean calculateAxeDamage = true;
    
    @Option(key = "drop-planks")
    private boolean dropPlanks = false;


    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        if (UniversalMaterial.isLog(block.getType())) {
            int brokenLogs = breakTree(block, 2);
            if (calculateAxeDamage) {
                ItemStack tool = e.getPlayer().getItemInHand();
                if (UniversalMaterial.isAxe(tool.getType())) {
                    tool.setDurability((short) (tool.getDurability() + brokenLogs));
                }
            }
        }
    }

    private int breakTree(Block block, int i) {
        int broken = 0;
        if (UniversalMaterial.isLog(block.getType())){
            if (dropPlanks){
                block.setType(Material.AIR);
                block.getWorld().dropItem(block.getLocation(), new ItemStack(UniversalMaterial.OAK_PLANKS.getType(), 4));
            }else {
                block.breakNaturally();
            }
            broken++;
            i = 2;
        }else {
            i--;
        }
        if (i > 0){
            for (BlockFace face : BlockFace.values()) {
                if (face.equals(BlockFace.DOWN) || face.equals(BlockFace.UP) || face.equals(BlockFace.NORTH) ||
                        face.equals(BlockFace.EAST) || face.equals(BlockFace.SOUTH) || face.equals(BlockFace.WEST)) {
                    broken += breakTree(block.getRelative(face), i);
                }
            }
        }

        return broken;
    }

}