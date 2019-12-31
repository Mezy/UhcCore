package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public enum UniversalMaterial{
    WHITE_WOOL("WOOL", "WHITE_WOOL", (short) 0),
    ORANGE_WOOL("WOOL", "ORANGE_WOOL", (short) 1),
    MAGENTA_WOOL("WOOL", "MAGENTA_WOOL", (short) 2),
    LIGHT_BLUE_WOOL("WOOL", "LIGHT_BLUE_WOOL", (short) 3),
    YELLOW_WOOL("WOOL", "YELLOW_WOOL", (short) 4),
    LIME_WOOL("WOOL", "LIME_WOOL", (short) 5),
    PINK_WOOL("WOOL", "PINK_WOOL", (short) 6),
    GRAY_WOOL("WOOL", "GRAY_WOOL", (short) 7),
    LIGHT_GRAY_WOOL("WOOL", "LIGHT_GRAY_WOOL", (short) 8),
    CYAN_WOOL("WOOL", "CYAN_WOOL", (short) 9),
    PURPLE_WOOL("WOOL", "PURPLE_WOOL", (short) 10),
    BLUE_WOOL("WOOL", "BLUE_WOOL", (short) 11),
    BROWN_WOOL("WOOL", "BROWN_WOOL", (short) 12),
    GREEN_WOOL("WOOL", "GREEN_WOOL", (short) 13),
    RED_WOOL("WOOL", "RED_WOOL", (short) 14),
    BLACK_WOOL("WOOL", "BLACK_WOOL", (short) 15),

    STATIONARY_WATER("STATIONARY_WATER", "WATER"),
    STATIONARY_LAVA("STATIONARY_LAVA", "LAVA"),
    SUGAR_CANE_BLOCK("SUGAR_CANE_BLOCK", "SUGAR_CANE"),

    SKELETON_SKULL("SKULL_ITEM","SKELETON_SKULL", (short) 0),
    WITHER_SKELETON_SKULL("SKULL_ITEM","WITHER_SKELETON_SKULL", (short) 1),
    ZOMBIE_HEAD("SKULL_ITEM","ZOMBIE_HEAD", (short) 2),
    PLAYER_HEAD("SKULL_ITEM","PLAYER_HEAD", (short) 3),
    PLAYER_HEAD_BLOCK("SKULL","PLAYER_HEAD", (short) 3),
    CREEPER_HEAD("SKULL_ITEM", "CREEPER_HEAD", (short) 4),
    DRAGON_HEAD("SKULL_ITEM", "DRAGON_HEAD", (short) 5),

    OAK_FENCE("FENCE", "OAK_FENCE"),

    PUFFERFISH("RAW_FISH", "PUFFERFISH", (short) 3),

    WHITE_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "WHITE_STAINED_GLASS_PANE", (short) 0),
    BLACK_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "BLACK_STAINED_GLASS_PANE", (short) 15),

    IRON_INGOT,
    LAVA_BUCKET,
    BOW,
    FISHING_ROD,
    SHIELD("SHIELD", "SHIELD"),
    DIAMOND_ORE,
    SADDLE,
    TRAPPED_CHEST,
    FEATHER,
    FURNACE,
    REDSTONE,
    REDSTONE_ORE,
    CHEST,
    QUARTZ,
    DIAMOND_PICKAXE,
    BOOK,
    GOLD_INGOT,
    GOLD_ORE,
    ARROW,
    COAL_ORE,
    GLOWING_REDSTONE_ORE("GLOWING_REDSTONE_ORE", "REDSTONE_ORE"),
    NETHER_QUARTZ_ORE("QUARTZ_ORE", "NETHER_QUARTZ_ORE"),
    LAPIS_LAZULI("INK_SACK", "LAPIS_LAZULI", (short) 4),
    RED_DYE("INK_SACK", "RED_DYE", (short) 1),
    DRAGON_EGG,
    END_PORTAL_FRAME("ENDER_PORTAL_FRAME", "END_PORTAL_FRAME"),
    END_PORTAL("ENDER_PORTAL", "END_PORTAL"),
    NETHER_STAR,
    NAME_TAG,
    ENCHANTING_TABLE("ENCHANTMENT_TABLE", "ENCHANTING_TABLE"),
    WOLF_SPAWN_EGG("MONSTER_EGG", "WOLF_SPAWN_EGG", (short) 95),
    CLOCK("WATCH", "CLOCK"),
    IRON_SWORD,
    DIAMOND_SWORD,
    EGG,
    ENCHANTED_BOOK,
    PAPER,
    BARRIER,
    AIR,
    COMPASS,
    NETHER_BRICK,

    // Flowers
    POPPY("RED_ROSE", "POPPY", (short) 0),
    BLUE_ORCHID("RED_ROSE", "BLUE_ORCHID", (short) 1),
    ALLIUM("RED_ROSE", "ALLIUM", (short) 2),
    AZURE_BLUET("RED_ROSE", "AZURE_BLUET", (short) 3),
    RED_TULIP("RED_ROSE", "RED_TULIP", (short) 4),
    ORANGE_TULIP("RED_ROSE", "ORANGE_TULIP", (short) 5),
    WHITE_TULIP("RED_ROSE", "WHITE_TULIP", (short) 6),
    PINK_TULIP("RED_ROSE", "PINK_TULIP", (short) 7),
    OXEYE_DAISY("RED_ROSE", "OXEYE_DAISY", (short) 8),
    DANDELION("YELLOW_FLOWER", "DANDELION"),

    SUNFLOWER("DOUBLE_PLANT", "SUNFLOWER", (short) 0),
    LILAC("DOUBLE_PLANT", "LILAC", (short) 1),
    ROSE_BUSH("DOUBLE_PLANT", "ROSE_BUSH", (short) 4),
    PEONY("DOUBLE_PLANT", "PEONY", (short) 5),
    DEAD_BUSH,

    WOODEN_PICKAXE("WOOD_PICKAXE", "WOODEN_PICKAXE"),
    GOLDEN_PICKAXE("GOLD_PICKAXE", "GOLDEN_PICKAXE"),
    IRON_PICKAXE,

    WOODEN_SHOVEL("WOOD_SPADE", "WOODEN_SHOVEL"),
    STONE_SHOVEL("STONE_SPADE", "STONE_SHOVEL"),
    IRON_SHOVEL("IRON_SPADE", "IRON_SHOVEL"),
    GOLDEN_SHOVEL("GOLD_SPADE", "GOLDEN_SHOVEL"),
    DIAMOND_SHOVEL("DIAMOND_SPADE", "DIAMOND_SHOVEL"),

    OAK_LEAVES("LEAVES", "OAK_LEAVES", (short) 0),
    SPRUCE_LEAVES("LEAVES", "SPRUCE_LEAVES", (short) 1),
    BIRCH_LEAVES("LEAVES", "BIRCH_LEAVES", (short) 2),
    JUNGLE_LEAVES("LEAVES", "JUNGLE_LEAVES", (short) 3),
    ACACIA_LEAVES("LEAVES_2", "ACACIA_LEAVES", (short) 0),
    DARK_OAK_LEAVES("LEAVES_2", "DARK_OAK_LEAVES", (short) 1),

    OAK_LOG("LOG", "OAK_LOG", (short) 0),
    SPRUCE_LOG("LOG", "SPRUCE_LOG", (short) 1),
    BIRCH_LOG("LOG", "BIRCH_LOG", (short) 2),
    JUNGLE_LOG("LOG", "JUNGLE_LOG", (short) 3),
    ACACIA_LOG("LOG_2", "ACACIA_LOG", (short) 0),
    DARK_OAK_LOG("LOG_2", "DARK_OAK_LOG", (short) 1),

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

    private boolean loaded;
    private Material material;

    UniversalMaterial(String name8, String name13, short id8){
        this.name8 = name8;
        this.name13 = name13;
        this.id8 = id8;
        loaded = false;
    }

    UniversalMaterial(String name8, String name13){
        this.name8 = name8;
        this.name13 = name13;
        id8 = 0;
        loaded = false;
    }

    UniversalMaterial(){
        this.name8 = name();
        this.name13 = name();
        id8 = 0;
        loaded = false;
    }

    @Nullable
    public Material getType(){
        if (!loaded){
            try {
                material = Material.valueOf(getTypeName());
            }catch (IllegalArgumentException ex){
                material = null;
            }
            loaded = true;
        }
        return material;
    }

    private String getTypeName(){
        return (UhcCore.getVersion() < 13) ? name8 : name13;
    }

    public short getData(){
        return UhcCore.getVersion() < 13 ? id8 : 0;
    }

    @SuppressWarnings("deprecation")
    public ItemStack getStack(int amount){
        Validate.notNull(getType(), getTypeName() + " could not be found on this version.");
        return new ItemStack(getType(), amount, getData());
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

    public static boolean isLog(Material material){
        return (
                material.equals(UniversalMaterial.ACACIA_LOG.getType()) ||
                        material.equals(UniversalMaterial.BIRCH_LOG.getType()) ||
                        material.equals(UniversalMaterial.DARK_OAK_LOG.getType()) ||
                        material.equals(UniversalMaterial.JUNGLE_LOG.getType()) ||
                        material.equals(UniversalMaterial.OAK_LOG.getType()) ||
                        material.equals(UniversalMaterial.SPRUCE_LOG.getType())
        );
    }

    public static boolean isLeaves(Material material){
        return (
                material.equals(UniversalMaterial.ACACIA_LEAVES.getType()) ||
                        material.equals(UniversalMaterial.BIRCH_LEAVES.getType()) ||
                        material.equals(UniversalMaterial.DARK_OAK_LEAVES.getType()) ||
                        material.equals(UniversalMaterial.JUNGLE_LEAVES.getType()) ||
                        material.equals(UniversalMaterial.OAK_LEAVES.getType()) ||
                        material.equals(UniversalMaterial.SPRUCE_LEAVES.getType())
        );
    }

    @SuppressWarnings("deprecation")
    public boolean equals(Block block){
        return block.getType() == getType() && block.getData() == id8;
    }

}