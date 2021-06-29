package com.gmail.val59000mc.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Optional;

public enum OreType {
    COAL(           Material.COAL,                      UniversalMaterial.COAL_ORE,     UniversalMaterial.DEEPSLATE_COAL_ORE,       false),
    COPPER(         UniversalMaterial.COPPER_INGOT,     UniversalMaterial.COPPER_ORE,   UniversalMaterial.DEEPSLATE_COPPER_ORE,     true),
    LAPIS_LAZULi(   UniversalMaterial.LAPIS_LAZULI,     Material.LAPIS_ORE,             UniversalMaterial.DEEPSLATE_LAPIS_ORE,      false),
    IRON(           UniversalMaterial.IRON_INGOT,       Material.IRON_ORE,              UniversalMaterial.DEEPSLATE_IRON_ORE,       true),
    GOLD(           UniversalMaterial.GOLD_INGOT,       UniversalMaterial.GOLD_ORE,     UniversalMaterial.DEEPSLATE_GOLD_ORE,       true),
    REDSTONE(       UniversalMaterial.REDSTONE,         UniversalMaterial.REDSTONE_ORE, UniversalMaterial.DEEPSLATE_REDSTONE_ORE,   false),
    DIAMOND(        Material.DIAMOND,                   UniversalMaterial.DIAMOND_ORE,  UniversalMaterial.DEEPSLATE_DIAMOND_ORE,    false),
    EMERALD(        Material.EMERALD,                   Material.EMERALD_ORE,           UniversalMaterial.DEEPSLATE_EMERALD_ORE,    false),
    NETHER_QUARTZ(  UniversalMaterial.QUARTZ,           UniversalMaterial.NETHER_QUARTZ_ORE,    null,                       false),
    NETHER_GOLD(    UniversalMaterial.GOLD_INGOT,       UniversalMaterial.NETHER_GOLD_ORE,      null,                       true),
    ANCIENT_DEBRIS( UniversalMaterial.NETHERITE_SCRAP,  UniversalMaterial.ANCIENT_DEBRIS,       null,                       true);

    private final Material drop;
    private final Material normal;
    private final Material deepslate;
    private final boolean needsSmelting;

    OreType(Material drop, Material normal, UniversalMaterial deepslate, boolean needsSmelting) {
        this.drop = drop;
        this.normal = normal;
        this.deepslate = deepslate  == null ? null : deepslate.getType();
        this.needsSmelting = needsSmelting;
    }

    OreType(Material drop, UniversalMaterial normal, UniversalMaterial deepslate, boolean needsSmelting) {
        this(drop, normal.getType(), deepslate, needsSmelting);
    }

    OreType(UniversalMaterial drop, UniversalMaterial normal, UniversalMaterial deepslate, boolean needsSmelting) {
        this(drop.getType(), normal.getType(), deepslate, needsSmelting);
    }

    OreType(UniversalMaterial drop, Material normal, UniversalMaterial deepslate, boolean needsSmelting) {
        this(drop.getType(), normal, deepslate, needsSmelting);
    }

    public static Optional<OreType> valueOf(Material material) {
        Validate.notNull(material);
        return Arrays.stream(values())
                .filter(ore -> ore.equals(material))
                .findFirst();
    }

    public static boolean isGold(Material material) {
        Validate.notNull(material);
        return GOLD.equals(material) || NETHER_GOLD.equals(material);
    }

    public boolean isCorrectTool(Material tool) {
        Validate.notNull(tool);

        switch (this) {
            case COAL:
            case NETHER_QUARTZ:
            case NETHER_GOLD:
                return tool == UniversalMaterial.NETHERITE_PICKAXE.getType() ||
                        tool == Material.DIAMOND_PICKAXE ||
                        tool == UniversalMaterial.GOLDEN_PICKAXE.getType() ||
                        tool == Material.IRON_PICKAXE ||
                        tool == Material.STONE_PICKAXE ||
                        tool == UniversalMaterial.WOODEN_PICKAXE.getType();
            case COPPER:
            case LAPIS_LAZULi:
            case IRON:
                return tool == UniversalMaterial.NETHERITE_PICKAXE.getType() ||
                        tool == Material.DIAMOND_PICKAXE ||
                        tool == UniversalMaterial.GOLDEN_PICKAXE.getType() ||
                        tool == Material.IRON_PICKAXE ||
                        tool == Material.STONE_PICKAXE;
            case GOLD:
            case REDSTONE:
            case DIAMOND:
            case EMERALD:
                return tool == UniversalMaterial.NETHERITE_PICKAXE.getType() ||
                        tool == Material.DIAMOND_PICKAXE ||
                        tool == UniversalMaterial.GOLDEN_PICKAXE.getType() ||
                        tool == Material.IRON_PICKAXE;
            case ANCIENT_DEBRIS:
                return tool == UniversalMaterial.NETHERITE_PICKAXE.getType() ||
                        tool == Material.DIAMOND_PICKAXE;
            default:
                return false;
        }
    }

    public int getXpPerBlock() {
        switch (this) {
            case COAL:
            case REDSTONE:
                return 1;
            case COPPER:
            case IRON:
            case NETHER_GOLD:
                return 2;
            case LAPIS_LAZULi:
            case GOLD:
            case DIAMOND:
            case EMERALD:
            case NETHER_QUARTZ:
                return 3;
            case ANCIENT_DEBRIS:
                return 4;
            default:
                return 0;
        }
    }

    public boolean equals(Material material) {
        Validate.notNull(material);
        return normal == material || deepslate == material;
    }

    public Material getDrop() {
        return drop;
    }

    public boolean needsSmelting() {
        return needsSmelting;
    }
}
