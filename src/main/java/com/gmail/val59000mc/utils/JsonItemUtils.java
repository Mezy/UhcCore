package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.exceptions.ParseException;
import com.google.gson.*;
import com.google.gson.JsonArray;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class JsonItemUtils{

    public static String getItemJson(ItemStack item){
        JsonObject json = new JsonObject();
        json.addProperty("type", item.getType().toString());
        if (item.getAmount() != 1){
            json.addProperty("amount", item.getAmount());
        }
        if (item.getDurability() != 0){
            json.addProperty("durability", item.getDurability());
        }
        if (item.hasItemMeta()){
            ItemMeta meta = item.getItemMeta();

            if (meta.hasDisplayName()){
                json.addProperty("display-name", meta.getDisplayName().replace('\u00a7', '&'));
            }
            if (meta.hasLore()){
                JsonArray lore = new JsonArray();
                meta.getLore().forEach(line -> lore.add(new JsonPrimitive(line.replace('\u00a7', '&'))));
                json.add("lore", lore);
            }
            if (meta.hasEnchants()){
                Map<Enchantment, Integer> enchantments = meta.getEnchants();
                JsonArray jsonEnchants = new JsonArray();
                for (Enchantment enchantment : enchantments.keySet()){
                    JsonObject jsonEnchant = new JsonObject();
                    jsonEnchant.addProperty("type", enchantment.getName());
                    jsonEnchant.addProperty("level", enchantments.get(enchantment));
                    jsonEnchants.add(jsonEnchant);
                }
                json.add("enchantments", jsonEnchants);
            }
        }
        return json.toString();
    }

    public static ItemStack getItemFromJson(String jsonString) throws ParseException{
        try {
            JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
            Material material;

            try {
                material = Material.valueOf(json.get("type").getAsString());
            }catch (IllegalArgumentException ex){
                Bukkit.getLogger().severe("[UhcCore] Failed to parse: " + jsonString);
                Bukkit.getLogger().severe(ex.getMessage());
                return null;
            }

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()){
                switch (entry.getKey()){
                    case "type":
                        continue;
                    case "amount":
                        item.setAmount(entry.getValue().getAsInt());
                        break;
                    case "durability":
                        item.setDurability(entry.getValue().getAsShort());
                        break;
                    case "display-name":
                        meta.setDisplayName(entry.getValue().getAsString());
                        break;
                    case "lore":
                        meta = parseLore(meta, entry.getValue().getAsJsonArray());
                        break;
                    case "enchantments":
                        meta = parseEnchantments(meta, entry.getValue().getAsJsonArray());
                        break;
                }
            }

            item.setItemMeta(meta);
            return item;
        }catch (Exception ex){
            throw new ParseException(ex.getMessage());
        }
    }

    private static ItemMeta parseLore(ItemMeta meta, JsonArray jsonArray){
        Iterator<JsonElement> lines = jsonArray.iterator();
        List<String> lore = new ArrayList<>();
        while (lines.hasNext()){
            lore.add(lines.next().getAsString());
        }
        meta.setLore(lore);
        return meta;
    }

    private static ItemMeta parseEnchantments(ItemMeta meta, JsonArray jsonArray){
        Iterator<JsonElement> enchants = jsonArray.iterator();
        while (enchants.hasNext()){
            JsonObject enchant = enchants.next().getAsJsonObject();
            Enchantment enchantment = Enchantment.getByName(enchant.get("type").getAsString());
            meta.addEnchant(enchantment, enchant.get("level").getAsInt(), true);
        }
        return meta;
    }

}