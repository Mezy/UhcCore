package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum UniversalMaterial{
    RED_WOOL("WOOL", "RED_WOOL", (short) 14),
    LIME_WOOL("WOOL", "LIME_WOOL", (short) 5),

    STATIONARY_WATER("STATIONARY_WATER", "WATER"),

    SKELETON_SKULL("SKULL_ITEM","SKELETON_SKULL", (short) 0),
    WITHER_SKELETON_SKULL("SKULL_ITEM","WITHER_SKELETON_SKULL", (short) 1),
    ZOMBIE_HEAD("SKULL_ITEM","ZOMBIE_HEAD", (short) 2),
    PLAYER_HEAD("SKULL_ITEM","PLAYER_HEAD", (short) 3),
    CREEPER_HEAD("SKULL_ITEM", "CREEPER_HEAD", (short) 4),
    DRAGON_HEAD("SKULL_ITEM", "DRAGON_HEAD", (short) 5),

    PUFFERFISH("RAW_FISH", "PUFFERFISH", (short) 3),

    WHITE_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "WHITE_STAINED_GLASS_PANE", (short) 0),
    BLACK_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "BLACK_STAINED_GLASS_PANE", (short) 15),

    OAK_LEAVES("LEAVES", "OAK_LEAVES", (short) 0),
    SPRUCE_LEAVES("LEAVES", "SPRUCE_LEAVES", (short) 1),
    BIRCH_LEAVES("LEAVES", "BIRCH_LEAVES", (short) 2),
    JUNGLE_LEAVES("LEAVES", "JUNGLE_LEAVES", (short) 3),
    ACACIA_LEAVES("LEAVES_2", "ACACIA_LEAVES", (short) 0),
    DARK_OAK_LEAVES("LEAVES_2", "DARK_OAK_LEAVES", (short) 1),

    OAK_LOG("LOG", "OAK_LEAVES", (short) 0),
    SPRUCE_LOG("LOG", "SPRUCE_LEAVES", (short) 1),
    BIRCH_LOG("LOG", "BIRCH_LEAVES", (short) 2),
    JUNGLE_LOG("LOG", "JUNGLE_LEAVES", (short) 3),
    ACACIA_LOG("LOG_2", "ACACIA_LEAVES", (short) 0),
    DARK_OAK_LOG("LOG_2", "DARK_OAK_LEAVES", (short) 1),

    COOKED_BEEF("COOKED_BEEF", "COOKED_BEEF"),
    COOKED_CHICKEN("COOKED_CHICKEN", "COOKED_CHICKEN"),
    COOKED_MUTTON("COOKED_MUTTON", "COOKED_MUTTON"),
    COOKED_RABBIT("COOKED_RABBIT", "COOKED_RABBIT"),
    COOKED_PORKCHOP("GRILLED_PORK", "COOKED_PORKCHOP"),

    RAW_BEEF("RAW_BEEF", "BEEF"),
    RAW_CHICKEN("RAW_CHICKEN", "CHICKEN"),
    RAW_MUTTON("MUTTON", "MUTTON"),
    RAW_RABBIT("RABBIT", "RABBIT"),
    RAW_PORK("PORK", "PORKCHOP");

    private String name8, name13;
    private short id8;

    private Material material;

    UniversalMaterial(String name8, String name13, short id8){
        this.name8 = name8;
        this.name13 = name13;
        this.id8 = id8;

        material = null;
    }

    UniversalMaterial(String name8, String name13){
        this.name8 = name8;
        this.name13 = name13;
        id8 = 0;

        material = null;
    }

    public Material getType(){
        if (material == null){
            if (UhcCore.getVersion() < 13) {
                material = Material.valueOf(name8);
            }else {
                material = Material.valueOf(name13);
            }
        }
        return material;
    }

    public ItemStack getStack(int amount){
        return new ItemStack(getType(), amount, UhcCore.getVersion() < 13 ? id8 : 0);
    }

    public ItemStack getStack(){
        return getStack(1);
    }

    public static UniversalMaterial ofType(Material material){
        for (UniversalMaterial universalMaterial : values()){
            if (universalMaterial.getType() == material){
                return universalMaterial;
            }
        }
        return null;
    }

}