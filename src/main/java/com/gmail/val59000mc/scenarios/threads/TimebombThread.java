package com.gmail.val59000mc.scenarios.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.utils.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.List;

public class TimebombThread implements Runnable{

    private ArmorStand armorStand;
    private Block chest1;
    private Block chest2;
    private TimebombThread thread;
    private long timeLeft;
    private Location loc;
    private List<ItemStack> drops;
    private String name;
    private boolean spawned;

    public TimebombThread(List<ItemStack> drops, Location loc, String name){
        this.drops = drops;
        this.loc = loc;
        this.name = name;
        thread = this;
        timeLeft = 30;
        spawned = false;
    }

    @Override
    public void run() {

        if (!spawned){
            spawnChest();
        }

        if (timeLeft > 0){
            armorStand.setCustomName(ChatColor.GOLD + "" + timeLeft);
            timeLeft--;
            Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(),thread,20L);
        }else {
            armorStand.remove();
            chest1.setType(Material.AIR);
            chest2.setType(Material.AIR);
            chest1.getWorld().createExplosion(chest1.getLocation(), 10, false);
        }

    }

    private void spawnChest(){

        spawned = true;

        chest1 = loc.getBlock();
        loc.add(1, 0, 0);
        chest2 = loc.getBlock();

        chest1.setType(Material.CHEST);
        chest2.setType(Material.CHEST);

        Chest chest = (Chest) chest1.getState();
        setChestName(chest, ChatColor.GOLD + "" + ChatColor.BOLD + name + "'s Timebomb");

        Inventory inv = chest.getInventory();

        for (ItemStack drop : drops){
            inv.addItem(drop);
        }

        loc.add(0,-1,.5);

        armorStand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setCustomName("");
    }

    private void setChestName(Chest chest, String chestName){
        try {
            Class craftChest = NMSUtils.getNMSClass("block.CraftChest");
            Method getTileEntity = NMSUtils.getMethod(craftChest, "getTileEntity");
            Object tileChest = getTileEntity.invoke(chest);
            Method a = NMSUtils.getMethod(tileChest.getClass(), "a");
            a.invoke(tileChest, chestName);
        }catch (Exception ex){ // todo find a way to change the chest name on other versions.
            ex.printStackTrace();
        }
    }

}