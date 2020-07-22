package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.scenarios.Option;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.UniversalMaterial;
import com.gmail.val59000mc.utils.UniversalSound;
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

        if (!UniversalMaterial.isCorrectTool(block.getType(), player.getItemInHand().getType())){
            return;
        }

        // find all surrounding blocks
        Vein vein = new Vein(block);
        vein.process();

        player.getWorld().dropItem(player.getLocation().getBlock().getLocation().add(.5,.5,.5), vein.getDrops(getVeinMultiplier(vein.getDropType())));

        if (vein.getTotalXp() != 0){
            UhcItems.spawnExtraXp(player.getLocation(), vein.getTotalXp());
        }

        // Process blood diamonds.
        if (isActivated(Scenario.BLOODDIAMONDS) && vein.getDropType() == Material.DIAMOND){
            player.getWorld().playSound(player.getLocation(), UniversalSound.PLAYER_HURT.getSound(), 1, 1);

            if (player.getHealth() < vein.getOres()){
                player.setHealth(0);
            }else {
                player.setHealth(player.getHealth() - vein.getOres());
            }
        }

        int newDurability = tool.getDurability()-vein.getOres();
        if (newDurability<1) newDurability = 1;

        tool.setDurability((short) newDurability);
        player.setItemInHand(tool);
    }

    private int getVeinMultiplier(Material material){
        int multiplier = 1;
        if (getScenarioManager().isActivated(Scenario.TRIPLEORES)){
            multiplier *= 3;
        }
        if (getScenarioManager().isActivated(Scenario.DOUBLEORES)){
            multiplier *= 2;
        }
        if (material == Material.GOLD_INGOT && getScenarioManager().isActivated(Scenario.DOUBLEGOLD)){
            multiplier *= 2;
        }
        return multiplier;
    }

    private static class Vein{
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
            return getDrops(1);
        }

        public ItemStack getDrops(int multiplier){
            Material material = getDropType();
            if (material == null) return null;

            if (material == Material.LAPIS_ORE){
                return UniversalMaterial.LAPIS_LAZULI.getStack(ores*multiplier);
            }

            return new ItemStack(material, ores*multiplier);
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
            
            // Max ores per vein is 20 to avoid server lag when mining sand / gravel.
            if (i > 0 && ores < 20){
                for (BlockFace face : BLOCK_FACES) {
                    getVeinBlocks(block.getRelative(face), type, i, maxBlocks-1);
                }
            }
        }

        private Material getDropType(){
            if (type == UniversalMaterial.NETHER_QUARTZ_ORE.getType()){
                return Material.QUARTZ;
            }

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
            if (type == UniversalMaterial.NETHER_QUARTZ_ORE.getType()){
                return 3;
            }

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