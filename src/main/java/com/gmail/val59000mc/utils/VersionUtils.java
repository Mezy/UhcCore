package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import com.google.gson.JsonObject;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class VersionUtils{

    private static VersionUtils versionUtils = null;

    public static VersionUtils getVersionUtils(){
        if (versionUtils == null){
            int version = UhcCore.getVersion();
            if (version < 12){
                versionUtils = new VersionUtils_1_8();
            }else if (version == 12){
                versionUtils = new VersionUtils_1_12();
            }else if (version == 13){
                versionUtils = new VersionUtils_1_13();
            }else{
                versionUtils = new VersionUtils_1_14();
            }
        }
        return versionUtils;
    }

    public abstract ShapedRecipe createShapedRecipe(ItemStack craft, String craftKey);

    public abstract ItemStack createPlayerSkull(String name, UUID uuid);

    public abstract void setSkullOwner(Skull skull, Player player);

    public abstract Objective registerObjective(Scoreboard scoreboard, String name, String criteria);

    public abstract void setPlayerMaxHealth(Player player, double maxHealth);

    public abstract void replaceOceanBiomes();

    public abstract void setGameRuleValue(World world, String gameRule, Object value);

    public abstract boolean hasEye(Block block);

    public abstract void setEye(Block block, boolean eye);

    public abstract void setEndPortalFrameOrientation(Block block, BlockFace blockFace);

    public abstract void setTeamNameTagVisibility(Team team, boolean value);

    public abstract void setChestName(Chest chest, String name);

    @Nullable
    public abstract JsonObject getBasePotionEffect(PotionMeta potionMeta);

    public abstract PotionMeta setBasePotionEffect(PotionMeta potionMeta, PotionData potionData);

    public abstract void setChestSide(Chest chest, org.bukkit.block.data.type.Chest.Type side);

    public abstract void removeRecipeFor(ItemStack item);

    public abstract void handleNetherPortalEvent(PlayerPortalEvent event);

}