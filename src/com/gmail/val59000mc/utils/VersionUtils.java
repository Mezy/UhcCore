package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.UUID;

public abstract class VersionUtils{

    private static VersionUtils versionUtils = null;

    public static VersionUtils getVersionUtils(){
        if (versionUtils == null) {
            int version = UhcCore.getVersion();
            if (version < 12) {
                versionUtils = new VersionUtils_1_8();
            } else if (version == 12){
                versionUtils = new VersionUtils_1_12();
            }else {
                versionUtils = new VersionUtils_1_13();
            }
        }
        return versionUtils;
    }

    public abstract ShapedRecipe createShapedRecipe(ItemStack craft, String craftKey);

    public abstract ItemStack createPlayerSkull(String name, UUID uuid);

}