package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.maploader.MapLoader;
import com.gmail.val59000mc.players.UhcPlayer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.papermc.lib.PaperLib;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Skull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;

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
    public void setSkullOwner(Skull skull, UhcPlayer player) {
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
        }catch (ReflectiveOperationException ex){
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
            Method a = NMSUtils.getMethod(tileChest.getClass(), "a", String.class);
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

    @Nullable
    @Override
    public Color getPotionColor(PotionMeta potionMeta){
        return null;
    }

    @Override
    public PotionMeta setPotionColor(PotionMeta potionMeta, Color color){
        return potionMeta;
    }

    @Override
    public void setChestSide(Chest chest, boolean left) {
        // Not needed on 1.8
    }

    @Override
    public void removeRecipe(ItemStack item, Recipe recipe){
        Iterator<Recipe> iterator = Bukkit.recipeIterator();

        try {
            while (iterator.hasNext()){
                if (iterator.next().getResult().isSimilar(item)){
                    iterator.remove();
                    Bukkit.getLogger().info("[UhcCore] Removed recipe for item "+JsonItemUtils.getItemJson(item));
                }
            }
        }catch (Exception ex){
            Bukkit.getLogger().warning("[UhcCore] Failed to remove recipe for item "+JsonItemUtils.getItemJson(item)+"!");
            ex.printStackTrace();
        }
    }

    @Override
    public void handleNetherPortalEvent(PlayerPortalEvent event){
        if (event.getTo() != null){
            return;
        }

        Location loc = event.getFrom();
        MapLoader mapLoader = GameManager.getGameManager().getMapLoader();

        try{
            Class<?> travelAgent = Class.forName("org.bukkit.TravelAgent");
            Method getPortalTravelAgent = NMSUtils.getMethod(event.getClass(), "getPortalTravelAgent");
            Method findOrCreate = NMSUtils.getMethod(travelAgent, "findOrCreate", Location.class);
            Object travelAgentInstance = getPortalTravelAgent.invoke(event);

            if (event.getFrom().getWorld().getEnvironment() == World.Environment.NETHER){
                loc.setWorld(mapLoader.getUhcWorld(World.Environment.NORMAL));
                loc.setX(loc.getX() * 2d);
                loc.setZ(loc.getZ() * 2d);
                Location to = (Location) findOrCreate.invoke(travelAgentInstance, loc);
                Validate.notNull(to, "TravelAgent returned null location!");
                event.setTo(to);
            }else{
                loc.setWorld(mapLoader.getUhcWorld(World.Environment.NETHER));
                loc.setX(loc.getX() / 2d);
                loc.setZ(loc.getZ() / 2d);
                Location to = (Location) findOrCreate.invoke(travelAgentInstance, loc);
                Validate.notNull(to, "TravelAgent returned null location!");
                event.setTo(to);
            }
        }catch (ReflectiveOperationException ex){
            ex.printStackTrace();
        }
    }

    @Nullable
    @Override
    public JsonObject getItemAttributes(ItemMeta meta){
        return null;
    }

    @Override
    public ItemMeta applyItemAttributes(ItemMeta meta, JsonObject attributes){
        return meta;
    }

    @Override
    public String getEnchantmentKey(Enchantment enchantment){
        return enchantment.getName();
    }

    @Nullable
    @Override
    public Enchantment getEnchantmentFromKey(String key){
        return Enchantment.getByName(key);
    }

    @Override
    public void setEntityAI(LivingEntity entity, boolean b){
        try{
            // Get Minecraft entity class
            Object mcEntity = NMSUtils.getHandle(entity);
            Method getNBTTag = NMSUtils.getMethod(mcEntity.getClass(), "getNBTTag");
            Class NBTTagCompound = NMSUtils.getNMSClass("NBTTagCompound");
            // Get NBT tag of zombie
            Object tag = getNBTTag.invoke(mcEntity);

            if (tag == null){
                tag = NBTTagCompound.newInstance();
            }

            // Methods to apply NBT data to the zombie
            Method c = NMSUtils.getMethod(mcEntity.getClass(), "c", NBTTagCompound);
            Method f = NMSUtils.getMethod(mcEntity.getClass(), "f", NBTTagCompound);

            // Method to set NBT values
            Method setInt = NMSUtils.getMethod(NBTTagCompound, "setInt", String.class, int.class);

            c.invoke(mcEntity, tag);
            setInt.invoke(tag, "NoAI", b?0:1);
            f.invoke(mcEntity, tag);
        }catch (Exception ex){
            // This will only work on 1.8 (Not 1.9-1.11, 0.5% of servers)
            ex.printStackTrace();
        }
    }

    @Override
    public List<Material> getItemList() {
        // Arrays.asList() returns a AbstractList where no objects can be removed from.
        return new ArrayList<>(Arrays.asList(Material.values()));
    }

    @Nullable
    @Override
    public JsonArray getSuspiciousStewEffects(ItemMeta meta){
        return null;
    }

    @Override
    public ItemMeta applySuspiciousStewEffects(ItemMeta meta, JsonArray effects){
        return meta;
    }

    @Override
    public void setItemUnbreakable(ItemMeta meta, boolean b){
        if (!PaperLib.isSpigot()){
            return; // Unable to set item as unbreakable on a none spigot server.
        }

        try {
            Method spigot = NMSUtils.getMethod(meta.getClass(), "spigot");
            Object spigotInstance = spigot.invoke(meta);
            Method setUnbreakable = NMSUtils.getMethod(spigotInstance.getClass(), "setUnbreakable", boolean.class);
            setUnbreakable.invoke(spigotInstance, b);
        }catch (ReflectiveOperationException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void killPlayer(Player player) {
        player.damage(player.getHealth());
    }

}