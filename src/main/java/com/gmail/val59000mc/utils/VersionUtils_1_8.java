package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.game.GameManager;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
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
    public void setSkullOwner(Skull skull, Player player) {
        skull.setOwner(player.getName());
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

    @Override
    public void setTeamNameTagVisibility(Team team, boolean value){
        team.setNameTagVisibility(value?NameTagVisibility.ALWAYS:NameTagVisibility.NEVER);
    }

    @Override
    public void setChestName(Chest chest, String name){
        try {
            Class craftChest = NMSUtils.getNMSClass("block.CraftChest");
            Method getTileEntity = NMSUtils.getMethod(craftChest, "getTileEntity");
            Object tileChest = getTileEntity.invoke(chest);
            Method a = NMSUtils.getMethod(tileChest.getClass(), "a", new Class<?>[]{String.class});
            a.invoke(tileChest, name);
        }catch (Exception ex){ // todo find a way to change the chest name on other versions up to 1.11
            Bukkit.getLogger().severe("[UhcCore] Failed to rename chest! Are you on 1.9-1.11?");
            ex.printStackTrace();
        }
    }

    @Override
    public JsonObject getBasePotionEffect(PotionMeta potionMeta){
        return null;
    }

    @Override
    public PotionMeta setBasePotionEffect(PotionMeta potionMeta, PotionData potionData){
        return potionMeta;
    }

    @Override
    public void setChestSide(Chest chest, org.bukkit.block.data.type.Chest.Type side) {
        // Not needed on 1.8
    }

    @Override
    public void removeRecipeFor(ItemStack item){
        Iterator<Recipe> iterator = Bukkit.recipeIterator();

        try {
            while (iterator.hasNext()){
                if (iterator.next().getResult().isSimilar(item)){
                    iterator.remove();
                    Bukkit.getLogger().info("[UhcCore] Banned item "+JsonItemUtils.getItemJson(item)+" registered");
                }
            }
        }catch (Exception ex){
            Bukkit.getLogger().warning("[UhcCore] Failed to register "+JsonItemUtils.getItemJson(item)+" banned craft");
            ex.printStackTrace();
        }
    }

    @Override
    public void handleNetherPortalEvent(PlayerPortalEvent event){
        if (event.getTo() == null){
            Location loc = event.getFrom();
            MainConfiguration cfg = GameManager.getGameManager().getConfiguration();

            // TravelAgent
            Method getPortalTravelAgent = NMSUtils.getMethod(event.getClass(), "getPortalTravelAgent");
            TravelAgent travelAgent;

            try{
                travelAgent = (TravelAgent) getPortalTravelAgent.invoke(event);
            }catch (Exception ex){
                ex.printStackTrace();
                return;
            }

            if (event.getFrom().getWorld().getEnvironment() == World.Environment.NETHER){
                loc.setWorld(Bukkit.getWorld(cfg.getOverworldUuid()));
                loc.setX(loc.getX() * 2d);
                loc.setZ(loc.getZ() * 2d);
                event.setTo(travelAgent.findOrCreate(loc));
            }else{
                loc.setWorld(Bukkit.getWorld(cfg.getNetherUuid()));
                loc.setX(loc.getX() / 2d);
                loc.setZ(loc.getZ() / 2d);
                event.setTo(travelAgent.findOrCreate(loc));
            }
        }
    }

}