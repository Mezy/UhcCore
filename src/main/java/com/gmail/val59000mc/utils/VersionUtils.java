package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

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

    public abstract Objective registerObjective(Scoreboard scoreboard, String name, String criteria);

    public abstract void setPlayerMaxHealth(Player player, double maxHealth);

    public abstract void replaceOceanBiomes();

    public abstract void setGameRuleValue(World world, String gameRule, Object value);

    public abstract boolean hasEye(Block block);

    public abstract void setEye(Block block, boolean eye);

    public abstract void setEndPortalFrameOrientation(Block block, BlockFace blockFace);

}