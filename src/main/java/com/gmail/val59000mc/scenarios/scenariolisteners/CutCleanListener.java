package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

public class CutCleanListener extends ScenarioListener{

    private ItemStack lapis;

    public CutCleanListener(){
        super(Scenario.CUTCLEAN);

        Dye d = new Dye();
        d.setColor(DyeColor.BLUE);
        this.lapis = d.toItemStack();
        this.lapis.setAmount(64);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        for(int i=0 ; i<e.getDrops().size() ; i++){
            UniversalMaterial replaceBy = null;
            UniversalMaterial type = UniversalMaterial.ofType(e.getDrops().get(i).getType());
            if (type != null) {
                switch (type) {
                    case RAW_BEEF:
                        replaceBy = UniversalMaterial.COOKED_BEEF;
                        break;
                    case RAW_CHICKEN:
                        replaceBy = UniversalMaterial.COOKED_CHICKEN;
                        break;
                    case RAW_MUTTON:
                        replaceBy = UniversalMaterial.COOKED_MUTTON;
                        break;
                    case RAW_RABBIT:
                        replaceBy = UniversalMaterial.COOKED_RABBIT;
                        break;
                    case RAW_PORK:
                        replaceBy = UniversalMaterial.COOKED_PORKCHOP;
                        break;
                    default:
                        break;
                }
            }
            if(replaceBy != null){
                ItemStack cookedFood = e.getDrops().get(i).clone();
                cookedFood.setType(replaceBy.getType());
                e.getDrops().set(i, cookedFood);
            }
        }
        if (e.getEntityType() == EntityType.COW) {
            e.getDrops().add(new ItemStack(Material.LEATHER));
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e){

        if (isActivated(Scenario.TRIPLEORES) || (isActivated(Scenario.VEINMINER) && e.getPlayer().isSneaking())){
            return;
        }

        Block block = e.getBlock();
        Location loc = e.getBlock().getLocation().add(0.5, 0, 0.5);

        switch (block.getType()){
            case IRON_ORE:
                block.setType(Material.AIR);
                loc.getWorld().dropItem(loc,new ItemStack(Material.IRON_INGOT));
                UhcItems.spawnExtraXp(loc,2);
                break;
            case GOLD_ORE:
                block.setType(Material.AIR);
                loc.getWorld().dropItem(loc,new ItemStack(Material.GOLD_INGOT));
                if (isActivated(Scenario.DOUBLEGOLD)){
                    loc.getWorld().dropItem(loc,new ItemStack(Material.GOLD_INGOT));
                }
                UhcItems.spawnExtraXp(loc,3);
                break;
            case SAND:
                block.setType(Material.AIR);
                loc.getWorld().dropItem(loc,new ItemStack(Material.GLASS));
                break;
            case GRAVEL:
                block.setType(Material.AIR);
                loc.getWorld().dropItem(loc,new ItemStack(Material.FLINT));
                break;
        }
    }

    @EventHandler
    public void openInventoryEvent(InventoryOpenEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, lapis);
        }
    }

    @EventHandler
    public void closeInventoryEvent(InventoryCloseEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, null);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Inventory inv = e.getInventory();
        ItemStack item = e.getCurrentItem();
        if (inv == null || item == null) return;

        if (inv instanceof EnchantingInventory){

            if (item.getType().equals(lapis.getType())){
                e.setCancelled(true);
            }else {
                e.getInventory().setItem(1, lapis);
            }
        }
    }

}