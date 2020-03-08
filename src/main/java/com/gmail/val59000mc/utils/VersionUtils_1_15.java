package com.gmail.val59000mc.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class VersionUtils_1_15 extends VersionUtils_1_14{

    @Override
    public void removeRecipeFor(ItemStack item){
        try{
            Method removeRecipe = NMSUtils.getMethod(Bukkit.class, "removeRecipe", NamespacedKey.class);
            boolean removed = (boolean) removeRecipe.invoke(null, item.getType().getKey());
            Validate.isTrue(removed, "Failed to remove recipe.");

            Bukkit.getLogger().info("[UhcCore] Banned item "+JsonItemUtils.getItemJson(item)+" registered");
        }catch (Exception ex){
            Bukkit.getLogger().warning("[UhcCore] Failed to register "+JsonItemUtils.getItemJson(item)+" banned craft, make sure your on 1.15.2+!");
            ex.printStackTrace();
        }
    }

}