package com.gmail.val59000mc.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;

public class OreUtils {

    public static boolean isCoalOre(Material type) {
        Validate.notNull(type);
        return type == UniversalMaterial.COAL_ORE.getType() ||
                type == UniversalMaterial.DEEPSLATE_COAL_ORE.getType();
    }

    public static boolean isCopperOre(Material type) {
        Validate.notNull(type);
        return type == UniversalMaterial.COPPER_ORE.getType() ||
                type == UniversalMaterial.DEEPSLATE_COPPER_ORE.getType();
    }

    public static boolean isLapisOre(Material type) {
        Validate.notNull(type);
        return type == Material.LAPIS_ORE ||
                type == UniversalMaterial.DEEPSLATE_LAPIS_ORE.getType();
    }

    public static boolean isIronOre(Material type) {
        Validate.notNull(type);
        return type == Material.IRON_ORE ||
                type == UniversalMaterial.DEEPSLATE_IRON_ORE.getType();
    }

    public static boolean isGoldOre(Material type) {
        Validate.notNull(type);
        return type == UniversalMaterial.GOLD_ORE.getType() ||
                type == UniversalMaterial.DEEPSLATE_GOLD_ORE.getType() ||
                type == UniversalMaterial.NETHER_GOLD_ORE.getType();
    }

    public static boolean isRedstoneOre(Material type) {
        Validate.notNull(type);
        return type == UniversalMaterial.GLOWING_REDSTONE_ORE.getType() ||
                type == UniversalMaterial.REDSTONE_ORE.getType() ||
                type == UniversalMaterial.DEEPSLATE_REDSTONE_ORE.getType();
    }

    public static boolean isDiamondOre(Material type) {
        Validate.notNull(type);
        return type == UniversalMaterial.DIAMOND_ORE.getType() ||
                type == UniversalMaterial.DEEPSLATE_DIAMOND_ORE.getType();
    }

    public static boolean isEmeraldOre(Material type) {
        Validate.notNull(type);
        return type == Material.EMERALD_ORE ||
                type == UniversalMaterial.DEEPSLATE_EMERALD_ORE.getType();
    }

    public static boolean isQuartzOre(Material type) {
        Validate.notNull(type);
        return type == UniversalMaterial.NETHER_QUARTZ_ORE.getType();
    }

    public static boolean isAncientDebris(Material type) {
        Validate.notNull(type);
        return type == UniversalMaterial.ANCIENT_DEBRIS.getType();
    }

    public static boolean isOverworldOre(Material type) {
        Validate.notNull(type);
        return isCoalOre(type) ||
                isCopperOre(type) ||
                isLapisOre(type) ||
                isIronOre(type) ||
                isGoldOre(type) ||
                isRedstoneOre(type) ||
                isDiamondOre(type) ||
                isEmeraldOre(type);
    }

    public static int getXpPerOreBlock(Material oreType) {
        Validate.notNull(oreType);

        if (isCoalOre(oreType)){
            return 1;
        }
        else if (isCopperOre(oreType)){
            return 2;
        }
        else if (isLapisOre(oreType)){
            return 3;
        }
        else if (isIronOre(oreType)){
            return 2;
        }
        else if (isGoldOre(oreType)){
            return 3;
        }
        else if (isRedstoneOre(oreType)){
            return 1;
        }
        else if (isDiamondOre(oreType)){
            return 3;
        }
        else if (isEmeraldOre(oreType)){
            return 3;
        }
        else if (isQuartzOre(oreType)){
            return 3;
        }
        else if (isAncientDebris(oreType)){
            return 4;
        }

        return 0;
    }

    public static Material getOreDropType(Material oreType, boolean smeltedType) {
        Validate.notNull(oreType);

        if (isCoalOre(oreType)) {
            return Material.COAL;
        }
        else if (isCopperOre(oreType) && smeltedType) {
            return UniversalMaterial.COPPER_INGOT.getType();
        }
        else if (isLapisOre(oreType)) {
            return UniversalMaterial.LAPIS_LAZULI.getType();
        }
        else if (isIronOre(oreType) && smeltedType) {
            return Material.IRON_INGOT;
        }
        else if (isGoldOre(oreType) && smeltedType) {
            return Material.GOLD_INGOT;
        }
        else if (isRedstoneOre(oreType)) {
            return Material.REDSTONE;
        }
        else if (isDiamondOre(oreType)) {
            return Material.DIAMOND;
        }
        else if (isEmeraldOre(oreType)) {
            return Material.EMERALD;
        }
        else if (isQuartzOre(oreType)) {
            return Material.QUARTZ;
        }
        else if (isAncientDebris(oreType) && smeltedType) {
            return UniversalMaterial.NETHERITE_SCRAP.getType();
        }
        else if (oreType == Material.GRAVEL) {
            return Material.FLINT;
        }

        return oreType;
    }

    public static boolean isCorrectTool(Material block, Material tool) {
        if (
                isDiamondOre(block) ||
                isEmeraldOre(block) ||
                isGoldOre(block) ||
                isRedstoneOre(block)
        ) {
            return tool == Material.DIAMOND_PICKAXE ||
                   tool == UniversalMaterial.GOLDEN_PICKAXE.getType() ||
                   tool == Material.IRON_PICKAXE;
        }
        else if (
                isIronOre(block)
        ) {
            return tool == Material.DIAMOND_PICKAXE ||
                   tool == UniversalMaterial.GOLDEN_PICKAXE.getType() ||
                   tool == Material.IRON_PICKAXE ||
                   tool == Material.STONE_PICKAXE;
        }
        else if (
                isCoalOre(block)
        ) {
            return tool == Material.DIAMOND_PICKAXE ||
                   tool == UniversalMaterial.GOLDEN_PICKAXE.getType() ||
                   tool == Material.IRON_PICKAXE ||
                   tool == Material.STONE_PICKAXE ||
                   tool == UniversalMaterial.WOODEN_PICKAXE.getType();
        }
        else if (
                block == Material.SAND ||
                block == Material.GRAVEL
        ) {
            return tool == UniversalMaterial.WOODEN_SHOVEL.getType() ||
                   tool == UniversalMaterial.STONE_SHOVEL.getType() ||
                   tool == UniversalMaterial.IRON_SHOVEL.getType() ||
                   tool == UniversalMaterial.GOLDEN_SHOVEL.getType() ||
                   tool == UniversalMaterial.DIAMOND_SHOVEL.getType();
        }

        return false;
    }

}
