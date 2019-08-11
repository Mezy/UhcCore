package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class TimberListener extends ScenarioListener{

    public TimberListener(){
        super(Scenario.TIMBER);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        if (UniversalMaterial.isLog(block.getType())) {
            breakTree(block, 2);
        }
    }

    private void breakTree(Block block, int i) {
        if (UniversalMaterial.isLog(block.getType())){
            block.breakNaturally();
            i = 2;
        }else {
            i--;
        }
        if (i > 0){
            for (BlockFace face : BlockFace.values()) {
                if (face.equals(BlockFace.DOWN) || face.equals(BlockFace.UP) || face.equals(BlockFace.NORTH) ||
                        face.equals(BlockFace.EAST) || face.equals(BlockFace.SOUTH) || face.equals(BlockFace.WEST)) {
                    breakTree(block.getRelative(face), i);
                }
            }
        }
    }

}