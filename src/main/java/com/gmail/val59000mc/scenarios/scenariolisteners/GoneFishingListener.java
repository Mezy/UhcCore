package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.events.UhcStartedEvent;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;

public class GoneFishingListener extends ScenarioListener{

    public static boolean isMC114(){
        return Bukkit.getBukkitVersion().contains("1.14");
    }
    public static boolean isMC113(){
        return Bukkit.getBukkitVersion().contains("1.13");
    }
    public static boolean isMC112(){
        return Bukkit.getBukkitVersion().contains("1.12");
    }
    public static boolean isMC111(){
        return Bukkit.getBukkitVersion().contains("1.11");
    }
    public static boolean isMC110(){
        return Bukkit.getBukkitVersion().contains("1.10");
    }

    public static boolean isMC19(){
        return Bukkit.getBukkitVersion().contains("1.9");
    }

    public static boolean isMC18(){
        return Bukkit.getBukkitVersion().contains("1.8");
    }

    public static boolean isMC114OrOlder(){
        if (isMC18() ||isMC19() || isMC110() || isMC111() || isMC112() || isMC113() || isMC114())
            return true;
        return false;
    }

    @EventHandler
    public void onGameStarted(UhcStartedEvent e){
        ItemStack rod = new ItemStack(Material.FISHING_ROD);
        rod.addUnsafeEnchantment(Enchantment.LURE, 8);
        rod.addUnsafeEnchantment(Enchantment.LUCK, 255);

        ItemMeta meta = rod.getItemMeta();
        // Set item unbreakable code change in 1.15 and older versions
        if(isMC114OrOlder()){
            meta.spigot().setUnbreakable(true);
        }else{
            meta.setUnbreakable(true);
        }

        meta.setDisplayName("" + ChatColor.RED + "The Road");
        rod.setItemMeta(meta);

        for (UhcPlayer uhcPlayer : e.getPlayersManager().getOnlinePlayingPlayers()){
            try {
                uhcPlayer.getPlayer().getInventory().addItem(rod);
                // Give player 10000 xl levels
                uhcPlayer.getPlayer().setLevel(10000);
                // Give player 64 anvils
                ItemStack anvil = new ItemStack(Material.ANVIL);
                anvil.setAmount(64);
                uhcPlayer.getPlayer().getInventory().addItem(anvil);
            }catch (UhcPlayerNotOnlineException ex){
                // No rod for offline players
            }
        }
    }

}
