package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class VersionUtils_1_8 extends VersionUtils{

    @Override
    public ShapedRecipe createShapedRecipe(ItemStack craft, String craftKey) {
        return new ShapedRecipe(craft);
    }

    @Override
    public ItemStack createPlayerSkull(String name, UUID uuid) {
        ItemStack item = UniversalMaterial.PLAYER_HEAD.getStack();
        SkullMeta im = (SkullMeta) item.getItemMeta();
        im.setOwner(name);
        item.setItemMeta(im);
        return item;
    }

    @Override
    public Objective registerObjective(Scoreboard scoreboard, String name, String criteria) {
        return scoreboard.registerNewObjective(name, criteria);
    }

    @Override
    public void setPlayerMaxHealth(Player player, double maxHealth) {
        player.setMaxHealth(maxHealth);
    }

    @Override
    public void replaceOceanBiomes() {
        int version = UhcCore.getVersion();
        if (version > 8){
            Bukkit.getLogger().warning("[UhcCore] Ocean biomes won't be replaced, this feature is not supported on 1." + version);
            return;
        }

        try {
            Class<?> biomeBase = NMSUtils.getNMSClass("BiomeBase");
            Field biomesField = biomeBase.getDeclaredField("biomes");
            Field idField = biomeBase.getDeclaredField("id");
            Field modifiers = Field.class.getDeclaredField("modifiers");

            biomesField.setAccessible(true);
            idField.setAccessible(true);
            modifiers.setAccessible(true);

            modifiers.set(biomesField, biomesField.getModifiers() & ~Modifier.FINAL);

            Object[] biomes = (Object[]) biomesField.get(null);
            Object DEEP_OCEAN = biomeBase.getDeclaredField("DEEP_OCEAN").get(null);
            Object OCEAN = biomeBase.getDeclaredField("OCEAN").get(null);
            Object PLAINS = biomeBase.getDeclaredField("PLAINS").get(null);
            Object FOREST = biomeBase.getDeclaredField("FOREST").get(null);

            biomes[(int) idField.get(DEEP_OCEAN)] = PLAINS;
            biomes[(int) idField.get(OCEAN)] = FOREST;
            biomesField.set(null, biomes);
        }catch (IllegalAccessException | NoSuchFieldException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void setGameRuleValue(World world, String gameRule, Object value) {
        world.setGameRuleValue(gameRule, value.toString());
    }

    @Override
    public boolean hasEye(Block block) {
        return block.getData() > 3;
    }

    @Override
    public void setEye(Block block, boolean eye){
        byte data = block.getData();
        if (eye && data < 4){
            data += 4;
        }else if (!eye && data > 3){
            data -= 4;
        }

        setBlockData(block, data);
    }

    @Override
    public void setEndPortalFrameOrientation(Block block, BlockFace blockFace){
        byte data = -1;
        switch (blockFace){
            case NORTH:
                data = 2;
                break;
            case EAST:
                data = 3;
                break;
            case SOUTH:
                data = 0;
                break;
            case WEST:
                data = 1;
                break;
        }

        setBlockData(block, data);
    }

    private void setBlockData(Block block, byte data){
        try {
            Method setData = NMSUtils.getMethod(Block.class, "setData",1);
            setData.invoke(block, data);
        }catch (IllegalAccessException | InvocationTargetException ex){
            ex.printStackTrace();
        }
    }

}