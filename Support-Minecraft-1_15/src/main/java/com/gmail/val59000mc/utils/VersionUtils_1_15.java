package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.exceptions.ParseException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;

import javax.annotation.Nullable;
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

    @Nullable
    @Override
    public JsonArray getSuspiciousStewEffects(ItemMeta meta){
        if (!(meta instanceof SuspiciousStewMeta)){
            return null;
        }

        SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) meta;

        JsonArray customEffects = new JsonArray();
        for (PotionEffect effect : stewMeta.getCustomEffects()){
            customEffects.add(JsonItemUtils.getPotionEffectJson(effect));
        }

        return customEffects;
    }

    @Override
    public ItemMeta applySuspiciousStewEffects(ItemMeta meta, JsonArray effects) throws ParseException{
        SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) meta;

        for (JsonElement jsonElement : effects){
            JsonObject effect = jsonElement.getAsJsonObject();
            stewMeta.addCustomEffect(JsonItemUtils.parsePotionEffect(effect), true);
        }

        return stewMeta;
    }

}