package com.gmail.val59000mc.playuhc.mc1_13.customitems;

import com.gmail.val59000mc.playuhc.mc1_13.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.playuhc.mc1_13.players.UhcPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemFactory {

    public static ItemStack createPlayerSkull(UhcPlayer player){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);

        try {
            SkullMeta im = (SkullMeta) item.getItemMeta();
            im.setOwningPlayer(player.getPlayer());
            item.setItemMeta(im);
        }catch (UhcPlayerNotOnlineException ex){
            // No custom skull
        }
        return item;
    }

    public static ItemStack createPlayerSkull(Player player){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);

        SkullMeta im = (SkullMeta) item.getItemMeta();
        im.setOwningPlayer(player);
        item.setItemMeta(im);

        return item;
    }
	
}