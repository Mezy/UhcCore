package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.FileUtils;
import com.gmail.val59000mc.utils.RandomUtils;
import com.gmail.val59000mc.utils.UniversalMaterial;
import com.gmail.val59000mc.configuration.YamlFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

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

    private List<FlowerDrop> flowerDrops;
    private int expPerFlower;

    public FlowerPowerListener(){
        super(Scenario.FLOWERPOWER);
        flowerDrops = new ArrayList<>();

        YamlFile cfg = FileUtils.saveResourceIfNotAvailable("flowerpower.yml");

        for (String drop : cfg.getStringList("drops")){
            try {
                FlowerDrop flowerDrop = new FlowerDrop(drop);
                flowerDrops.add(flowerDrop);
            }catch (Exception ex){
                Bukkit.getLogger().severe("[UhcCore] Failed to parse FlowerPower item: "+drop+"!");
                Bukkit.getLogger().severe(ex.getMessage());
            }
        }
        expPerFlower = cfg.getInt("exp-per-flower", 2);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Block block = e.getBlock();

        if (isFlower(block)){
            Location blockLoc = block.getLocation().add(.5,.5,.5);
            block.setType(Material.AIR);
            UhcItems.spawnExtraXp(blockLoc, expPerFlower);

            int random = RandomUtils.randomInteger(0, flowerDrops.size()-1);
            flowerDrops.get(random).drop(blockLoc);
        }
    }

    private boolean isFlower(Block block){
        for (UniversalMaterial flower : FLOWERS){
            if (flower.equals(block)) return true;
        }
        return false;
    }

    private class FlowerDrop{
        private Material material;
        private short data;
        private int min, max;

        public FlowerDrop(String string) throws Exception{
            String[] args = string.split("/");
            if (args.length != 4){
                throw new IllegalArgumentException("Invalid drop: " + string);
            }

            material = Material.valueOf(args[0]);
            data = Short.valueOf(args[1]);
            min = Integer.valueOf(args[2]);
            max = Integer.valueOf(args[3]);
        }

        @SuppressWarnings("deprecation")
        public void drop(Location location){
            int amount = RandomUtils.randomInteger(min, max);
            location.getWorld().dropItem(location, new ItemStack(material, amount, data));
        }
    }

}