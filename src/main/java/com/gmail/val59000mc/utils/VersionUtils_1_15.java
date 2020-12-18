package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.exceptions.ParseException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;

import javax.annotation.Nullable;

public class VersionUtils_1_15 extends VersionUtils_1_14{

    @Override
    public void removeRecipe(ItemStack item, Recipe recipe){
        NamespacedKey key;

        if (recipe instanceof Keyed){
            key = ((Keyed) recipe).getKey();
        }else{
            key = item.getType().getKey();
        }

        boolean removed = Bukkit.removeRecipe(key);

        if (removed){
            Bukkit.getLogger().info("[UhcCore] Removed recipe for "+key.toString());
        }else {
            Bukkit.getLogger().info("[UhcCore] Failed to remove recipe for " + key.toString() + "!");
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

    @Override
    public void killPlayer(Player player) {
        player.damage(player.getHealth() + player.getAbsorptionAmount());
    }

}