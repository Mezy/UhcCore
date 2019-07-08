package com.gmail.val59000mc.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class VersionUtils_1_8 extends VersionUtils{

    @Override @SuppressWarnings("Deprecation")
    public ShapedRecipe createShapedRecipe(ItemStack craft, String craftKey) {
        return new ShapedRecipe(craft);
    }

}