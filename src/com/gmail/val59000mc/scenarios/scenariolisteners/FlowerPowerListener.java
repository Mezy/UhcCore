package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.FileUtils;
import com.gmail.val59000mc.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class FlowerPowerListener extends ScenarioListener{

    private List<String> flowers;
    private List<String> drops;
    private int expPerFlower;

    public FlowerPowerListener(){
        super(Scenario.FLOWERPOWER);

        File file = FileUtils.saveResourceIfNotAvailable("flowerpower.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        flowers = cfg.getStringList("flowers");
        drops = cfg.getStringList("drops");
        expPerFlower = cfg.getInt("exp-per-flower", 2);
    }

    @EventHandler @SuppressWarnings("deprecation")
    public void onBlockBreak(BlockBreakEvent e){

        Block block = e.getBlock();
        if (isFlower(block)){
            if (block.getData() == 10 && isFlower(block.getLocation().add(0, -1, 0).getBlock())){
                block.getLocation().add(0, -1, 0).getBlock().setType(Material.AIR);
            }
            block.setType(Material.AIR);

            e.getPlayer().giveExp(expPerFlower);
            block.getWorld().spawnEntity(block.getLocation().add(.5, 0, .5), EntityType.EXPERIENCE_ORB);

            int random = RandomUtils.randomInteger(0, drops.size()-1);
            String[] drop = drops.get(random).split("/");
            dropItem(e.getBlock().getLocation().add(.5, 0, .5), drop[0], drop[1], drop[2], drop[3]);
        }
    }

    private void dropItem(Location loc, String itemName, String data, String min, String max){
        short dataShort = Short.valueOf(data);
        int minInt = Integer.parseInt(min);
        int maxInt = Integer.parseInt(max);
        int amount = RandomUtils.randomInteger(minInt, maxInt);
        Material item = Material.getMaterial(itemName);

        if (item == null){
            Bukkit.broadcastMessage(ChatColor.RED + itemName + " is a unknown item! Please tell Mezy.");
            return;
        }

        loc.getWorld().dropItem(loc, new ItemStack(item, amount, dataShort));
    }

    @SuppressWarnings("deprecation")
    private boolean isFlower(Block block){
        String blockString = block.getType().toString() + "/" + block.getData();
        return flowers.contains(blockString);
    }
/*
    @SuppressWarnings("deprecation")
    private boolean isDropBlock(Block block){
        return isFlower(block) ||
                block.getData() == 10 &&
                        isFlower(block.getLocation().add(0, -1, 0).getBlock()) &&
                        block.getType().equals(Material.DOUBLE_PLANT);
    }
*/
}