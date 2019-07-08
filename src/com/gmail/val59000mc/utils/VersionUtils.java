package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public abstract class VersionUtils{

    private static VersionUtils versionUtils = null;

    public static VersionUtils getVersionUtils(){
        if (versionUtils == null) {
            if (UhcCore.getVersion() < 12) {
                versionUtils = new VersionUtils_1_8();
            } else {
                versionUtils = new VersionUtils_1_12();
            }
        }
        return versionUtils;
    }

    public abstract ShapedRecipe createShapedRecipe(ItemStack craft, String craftKey);

}