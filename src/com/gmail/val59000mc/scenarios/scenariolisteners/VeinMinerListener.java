package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class VeinMinerListener extends ScenarioListener{

    private static final BlockFace[] BLOCK_FACES = new BlockFace[]{
            BlockFace.DOWN,
            BlockFace.UP,
            BlockFace.SOUTH,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.WEST
    };

    public VeinMinerListener(){
        super(Scenario.VEINMINER);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Player player = e.getPlayer();

        if (!player.isSneaking()){
            return;
        }

        Block block = e.getBlock();
        ItemStack tool = player.getItemInHand();

        if (block.getType() == UniversalMaterial.GLOWING_REDSTONE_ORE.getType()){
            block.setType(Material.REDSTONE_ORE);
        }

        if (!rightTool(player.getItemInHand().getType(), block.getType())){
            return;
        }

        // find all surrounding blocks
        Vein vein = new Vein(block);
        vein.process();

        player.getWorld().dropItem(player.getLocation().getBlock().getLocation().add(.5,.5,.5), vein.getDrops());

        if (vein.getTotalXp() != 0){
            UhcItems.spawnExtraXp(player.getLocation(), vein.getTotalXp());
        }

        int newDurability = tool.getDurability()-vein.getOres();
        if (newDurability<1) newDurability = 1;

        tool.setDurability((short) newDurability);
        player.setItemInHand(tool);
    }

    private boolean rightTool(Material tool, Material block){
        if (
                block == Material.DIAMOND_ORE ||
                        block == Material.GOLD_ORE ||
                        block == Material.IRON_ORE ||
                        block == Material.COAL_ORE ||
                        block == Material.LAPIS_ORE ||
                        block == Material.EMERALD_ORE ||
                        block == UniversalMaterial.GLOWING_REDSTONE_ORE.getType() ||
                        block == Material.REDSTONE_ORE
        ){
            if (
                    tool == UniversalMaterial.WOODEN_PICKAXE.getType() ||
                            tool == Material.STONE_PICKAXE ||
                            tool == Material.IRON_PICKAXE ||
                            tool == UniversalMaterial.GOLDEN_PICKAXE.getType() ||
                            tool == Material.DIAMOND_PICKAXE
            ){
                return true;
            }
        }

        if (block == Material.GRAVEL){
            if (
                    tool == UniversalMaterial.WOODEN_SHOVEL.getType() ||
                            tool == UniversalMaterial.STONE_SHOVEL.getType() ||
                            tool == UniversalMaterial.IRON_SHOVEL.getType() ||
                            tool == UniversalMaterial.GOLDEN_SHOVEL.getType() ||
                            tool == UniversalMaterial.DIAMOND_SHOVEL.getType()
            ){
                return true;
            }
        }

        return false;
    }

    private class Vein{
        private Block startBlock;
        private Material type;
        private int ores;

        public Vein(Block startBlock){
            this.startBlock = startBlock;
            type = startBlock.getType();
            ores = 0;
        }

        public void process(){
            getVeinBlocks(startBlock, startBlock.getType(), 2, 10);
        }

        public ItemStack getDrops(){
            Material material = getDropType();
            if (material == null) return null;

            if (material == UniversalMaterial.LAPIS_LAZULI.getType()){
                return UniversalMaterial.LAPIS_LAZULI.getStack(ores);
            }

            return new ItemStack(material, ores);
        }

        public int getTotalXp(){
            return getXpPerBlock()*ores;
        }

        public int getOres() {
            return ores;
        }

        private void getVeinBlocks(Block block, Material type, int i, int maxBlocks) {
            if (maxBlocks == 0) return;

            if (block.getType() == UniversalMaterial.GLOWING_REDSTONE_ORE.getType()){
                block.setType(Material.REDSTONE_ORE);
            }

            if (block.getType() == type){
                block.setType(Material.AIR);
                ores++;
                i = 2;
            }else {
                i--;
            }
            if (i > 0){
                for (BlockFace face : BLOCK_FACES) {
                    getVeinBlocks(block.getRelative(face), type, i, maxBlocks-1);
                }
            }
        }

        private Material getDropType(){
            switch (type){
                case DIAMOND_ORE:
                    return Material.DIAMOND;
                case GOLD_ORE:
                    return Material.GOLD_INGOT;
                case IRON_ORE:
                    return Material.IRON_INGOT;
                case COAL_ORE:
                    return Material.COAL;
                case LAPIS_ORE:
                    return UniversalMaterial.LAPIS_LAZULI.getType();
                case EMERALD_ORE:
                    return Material.EMERALD;
                case REDSTONE_ORE:
                    return Material.REDSTONE;
                case GRAVEL:
                    return Material.FLINT;
            }
            return null;
        }

        private int getXpPerBlock(){
            switch (type){
                case DIAMOND_ORE:
                    return 3;
                case GOLD_ORE:
                    return 3;
                case IRON_ORE:
                    return 2;
                case COAL_ORE:
                    return 1;
                case LAPIS_ORE:
                    return 3;
                case EMERALD_ORE:
                    return 3;
                case REDSTONE_ORE:
                    return 1;
            }
            return 0;
        }

    }

}