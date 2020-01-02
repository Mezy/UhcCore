package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.*;
import com.gmail.val59000mc.configuration.YamlFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlowerPowerListener extends ScenarioListener{

    private static final UniversalMaterial[] FLOWERS = new UniversalMaterial[]{
            UniversalMaterial.POPPY,
            UniversalMaterial.BLUE_ORCHID,
            UniversalMaterial.ALLIUM,
            UniversalMaterial.AZURE_BLUET,
            UniversalMaterial.RED_TULIP,
            UniversalMaterial.ORANGE_TULIP,
            UniversalMaterial.WHITE_TULIP,
            UniversalMaterial.PINK_TULIP,
            UniversalMaterial.OXEYE_DAISY,
            UniversalMaterial.SUNFLOWER,
            UniversalMaterial.LILAC,
            UniversalMaterial.ROSE_BUSH,
            UniversalMaterial.PEONY,
            UniversalMaterial.DEAD_BUSH,
            UniversalMaterial.DANDELION
    };

    private List<JsonItemStack> flowerDrops;
    private int expPerFlower;
    private boolean containedOldFormat;

    @Override
    public void onEnable(){
        flowerDrops = new ArrayList<>();
        containedOldFormat = false;

        String source = UhcCore.getVersion() < 13 ? "flowerpower-1.8.yml" : "flowerpower-1.13.yml";
        YamlFile cfg = FileUtils.saveResourceIfNotAvailable("flowerpower.yml", source);

        expPerFlower = cfg.getInt("exp-per-flower", 2);

        for (String drop : cfg.getStringList("drops")){
            try {
                JsonItemStack flowerDrop = parseDropItem(drop);
                flowerDrops.add(flowerDrop);
            }catch (Exception ex){
                Bukkit.getLogger().severe("[UhcCore] Failed to parse FlowerPower item: "+drop+"!");
                Bukkit.getLogger().severe(ex.getMessage());
            }
        }

        // Update flowerpower.yml to new item format.
        if (containedOldFormat){
            List<String> drops = new ArrayList<>();

            for (JsonItemStack drop : flowerDrops){
                drops.add(drop.toString());
            }

            cfg.set("drops", drops);

            try {
                cfg.saveWithComments();
                Bukkit.getLogger().info("[UhcCore] Updated flowerpower.yml to the new json format.");
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Block block = e.getBlock();

        if (isFlower(block)){
            Location blockLoc = block.getLocation().add(.5,.5,.5);
            block.setType(Material.AIR);
            UhcItems.spawnExtraXp(blockLoc, expPerFlower);

            int random = RandomUtils.randomInteger(0, flowerDrops.size()-1);
            ItemStack drop = flowerDrops.get(random);
            blockLoc.getWorld().dropItem(blockLoc, drop);
        }
    }

    private boolean isFlower(Block block){
        for (UniversalMaterial flower : FLOWERS){
            if (flower.equals(block)) return true;
        }

        if (UhcCore.getVersion() >= 14){
            String material = block.getType().toString();
            return material.equals("LILY_OF_THE_VALLEY") || material.equals("CORNFLOWER");
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private JsonItemStack parseDropItem(String string) throws Exception{
        // New format
        if (string.startsWith("{") && string.endsWith("}")){
            return JsonItemUtils.getItemFromJson(string);
        }

        // Old format
        containedOldFormat = true;
        String[] args = string.split("/");
        if (args.length != 4){
            throw new IllegalArgumentException("Invalid drop: " + string);
        }

        JsonItemStack drop = new JsonItemStack(Material.valueOf(args[0]));
        drop.setDurability(Short.parseShort(args[1]));
        drop.setMinimum(Integer.parseInt(args[2]));
        drop.setMaximum(Integer.parseInt(args[3]));
        return drop;
    }

    private static class FlowerDrop{
        private Material material;
        private short data;
        private int min, max;

        public FlowerDrop(String string) throws Exception{
            String[] args = string.split("/");
            if (args.length != 4){
                throw new IllegalArgumentException("Invalid drop: " + string);
            }

            material = Material.valueOf(args[0]);
            data = Short.parseShort(args[1]);
            min = Integer.parseInt(args[2]);
            max = Integer.parseInt(args[3]);
        }


        public void drop(Location location){
            int amount = RandomUtils.randomInteger(min, max);
            location.getWorld().dropItem(location, new ItemStack(material, amount, data));
        }
    }

}