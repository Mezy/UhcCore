package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.events.UhcStartedEvent;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GoneFishingListener extends ScenarioListener{

    @EventHandler
    public void onGameStarted(UhcStartedEvent e){
        ItemStack rod = new ItemStack(Material.FISHING_ROD);
        rod.addUnsafeEnchantment(Enchantment.LURE, 8);
        rod.addUnsafeEnchantment(Enchantment.LUCK, 255);
        ItemMeta meta = rod.getItemMeta();
        // Set item unbreakable code change in 1.15 and older versions
        if(UhcCore.getVersion() < 15){
            meta.spigot().setUnbreakable(true);
        }else{
            meta.setUnbreakable(true);
        }
        rod.setItemMeta(meta);
        ItemStack anvils = new ItemStack(Material.ANVIL, 64);
        for (UhcPlayer uhcPlayer : e.getPlayersManager().getOnlinePlayingPlayers()){
            try {
                uhcPlayer.getPlayer().getInventory().addItem(rod);
                // Give player 10000 xl levels
                uhcPlayer.getPlayer().setLevel(10000);
                // Give player 64 anvils
                uhcPlayer.getPlayer().getInventory().addItem(anvils);
            }catch (UhcPlayerNotOnlineException ex){
                // No rod for offline players
            }
        }
    }

}
