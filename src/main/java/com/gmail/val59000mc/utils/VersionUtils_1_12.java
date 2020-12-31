package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.maploader.MapLoader;
import com.gmail.val59000mc.players.UhcPlayer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
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
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("deprecation")
public class VersionUtils_1_12 extends VersionUtils{

    @Override
    public ShapedRecipe createShapedRecipe(ItemStack craft, String craftKey) {
        NamespacedKey namespacedKey = new NamespacedKey(UhcCore.getPlugin(), craftKey);
        return new ShapedRecipe(namespacedKey, craft);
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
        skull.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUuid()));
    }

    @Override
    public Objective registerObjective(Scoreboard scoreboard, String name, String criteria) {
        return scoreboard.registerNewObjective(name, criteria);
    }

    @Override
    public void setPlayerMaxHealth(Player player, double maxHealth) {
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
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
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, value?Team.OptionStatus.ALWAYS:Team.OptionStatus.NEVER);
    }

    @Override
    public void setChestName(Chest chest, String name){
        chest.setCustomName(name);
        chest.update();
    }

    @Override
    public JsonObject getBasePotionEffect(PotionMeta potionMeta) {
        PotionData potionData = potionMeta.getBasePotionData();
        JsonObject baseEffect = new JsonObject();
        baseEffect.addProperty("type", potionData.getType().name());

        if (potionData.isUpgraded()) {
            baseEffect.addProperty("upgraded", true);
        }
        if (potionData.isExtended()) {
            baseEffect.addProperty("extended", true);
        }
        return baseEffect;
    }

    @Override
    public PotionMeta setBasePotionEffect(PotionMeta potionMeta, PotionData potionData) {
        potionMeta.setBasePotionData(potionData);
        return potionMeta;
    }

    @Nullable
    @Override
    public Color getPotionColor(PotionMeta potionMeta){
        if (potionMeta.hasColor()){
            return potionMeta.getColor();
        }

        return null;
    }

    @Override
    public PotionMeta setPotionColor(PotionMeta potionMeta, Color color){
        potionMeta.setColor(color);
        return potionMeta;
    }

    @Override
    public void setChestSide(Chest chest, boolean left) {
        // Not needed on 1.12
    }

    @Override
    public void removeRecipe(ItemStack item, Recipe r){
        Bukkit.getLogger().info("[UhcCore] Removing craft for item "+JsonItemUtils.getItemJson(item));

        try{
            // Minecraft classes
            Class craftingManager = NMSUtils.getNMSClass("CraftingManager");
            Class iRecipe = NMSUtils.getNMSClass("IRecipe");

            // Method to get Bukkit Recipe object
            Method toBukkitRecipe = NMSUtils.getMethod(iRecipe, "toBukkitRecipe");
            toBukkitRecipe.setAccessible(true);

            // RegistryMaterials "map" where recipes are stored.
            Object registryMaterials = craftingManager.getField("recipes").get(null);

            // Value that stores a RegistryID object
            Field a = registryMaterials.getClass().getDeclaredField("a");
            // Value that stores a Map
            Field b = registryMaterials.getClass().getDeclaredField("b");
            a.setAccessible(true);
            b.setAccessible(true);

            // Remove from map
            Map<?, ?> map = (Map) b.get(registryMaterials);

            for (Object value : map.keySet()){
                Recipe recipe = (Recipe) toBukkitRecipe.invoke(value);

                if (recipe.getResult().isSimilar(item)){
                    System.out.println("Found recipe in map! Removing ...");
                    map.remove(value);
                    break;
                }
            }

            b.set(registryMaterials, map);

            // Remove from array
            Object registryId = a.get(registryMaterials);

            Field d = registryId.getClass().getDeclaredField("d");
            d.setAccessible(true);

            Object[] array = (Object[]) d.get(registryId);

            Object mcRecipe;
            for (int i = 0; i < array.length; i++) {
                mcRecipe = array[i];

                if (mcRecipe == null){
                    continue;
                }

                Recipe recipe = (Recipe) toBukkitRecipe.invoke(mcRecipe);
                if (recipe.getResult().isSimilar(item)){
                    System.out.println("Found recipe in array! Removing ...");
                    array[i] = null;
                    break;
                }
            }

            d.set(registryId, array);

            Bukkit.getLogger().info("[UhcCore] Removed recipe for item "+JsonItemUtils.getItemJson(item));
        } catch (Exception ex){
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
        entity.setAI(b);
    }

    @Override
    public List<Material> getItemList(){
        List<Material> items = new ArrayList<>();

        for (Material material : Material.values()){
            if (material.isItem()){
                items.add(material);
            }
        }

        return items;
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
        meta.setUnbreakable(b);
    }

    @Override
    public void killPlayer(Player player) {
        player.damage(player.getHealth());
    }

}