package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.ParseException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.maploader.MapLoader;
import com.gmail.val59000mc.players.UhcPlayer;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Skull;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;

public class VersionUtils_1_13 extends VersionUtils{

    @Override
    public ShapedRecipe createShapedRecipe(ItemStack craft, String craftKey) {
        NamespacedKey namespacedKey = new NamespacedKey(UhcCore.getPlugin(), craftKey);
        return new ShapedRecipe(namespacedKey, craft);
    }

    @Override
    public ItemStack createPlayerSkull(String name, UUID uuid) {
        ItemStack item = UniversalMaterial.PLAYER_HEAD.getStack();
        SkullMeta im = (SkullMeta) item.getItemMeta();
        im.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        item.setItemMeta(im);
        return item;
    }

    @Override
    public void setSkullOwner(Skull skull, UhcPlayer player) {
        skull.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUuid()));
    }

    @Override
    public Objective registerObjective(Scoreboard scoreboard, String name, String criteria) {
        if (criteria.equals("health")){
            return scoreboard.registerNewObjective(name, criteria, name, RenderType.HEARTS);
        }
        return scoreboard.registerNewObjective(name, criteria, name);
    }

    @Override
    public void setPlayerMaxHealth(Player player, double maxHealth) {
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
    }

    @Override @SuppressWarnings("unchecked")
    public void setGameRuleValue(World world, String name, Object value){
        GameRule gameRule = GameRule.getByName(name);
        world.setGameRule(gameRule, value);
    }

    @Override
    public boolean hasEye(Block block) {
        EndPortalFrame portalFrame = (EndPortalFrame) block.getBlockData();
        return portalFrame.hasEye();
    }

    @Override
    public void setEye(Block block, boolean eye) {
        EndPortalFrame portalFrame = (EndPortalFrame) block.getBlockData();
        portalFrame.setEye(eye);
        block.setBlockData(portalFrame);
    }

    @Override
    public void setEndPortalFrameOrientation(Block block, BlockFace blockFace) {
        EndPortalFrame portalFrame = (EndPortalFrame) block.getBlockData();
        portalFrame.setFacing(blockFace);
        block.setBlockData(portalFrame);
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
    public void setChestSide(Chest chest, boolean left){
        org.bukkit.block.data.type.Chest chestData = (org.bukkit.block.data.type.Chest) chest.getBlockData();

        org.bukkit.block.data.type.Chest.Type side = left ? org.bukkit.block.data.type.Chest.Type.LEFT : org.bukkit.block.data.type.Chest.Type.RIGHT;

        chestData.setType(side);
        chest.getBlock().setBlockData(chestData, true);
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
        if (!meta.hasAttributeModifiers()){
            return null;
        }

        JsonObject attributesJson = new JsonObject();
        Multimap<Attribute, AttributeModifier> attributeModifiers = meta.getAttributeModifiers();

        for (Attribute attribute : attributeModifiers.keySet()){
            JsonArray modifiersJson = new JsonArray();
            Collection<AttributeModifier> modifiers = attributeModifiers.get(attribute);

            for (AttributeModifier modifier : modifiers){
                JsonObject modifierObject = new JsonObject();
                modifierObject.addProperty("name", modifier.getName());
                modifierObject.addProperty("amount", modifier.getAmount());
                modifierObject.addProperty("operation", modifier.getOperation().name());
                if (modifier.getSlot() != null){
                    modifierObject.addProperty("slot", modifier.getSlot().name());
                }
                modifiersJson.add(modifierObject);
            }

            attributesJson.add(attribute.name(), modifiersJson);
        }

        return attributesJson;
    }

    @Override
    public ItemMeta applyItemAttributes(ItemMeta meta, JsonObject attributes){
        Set<Map.Entry<String, JsonElement>> entries = attributes.entrySet();

        for (Map.Entry<String, JsonElement> attributeEntry : entries){
            Attribute attribute = Attribute.valueOf(attributeEntry.getKey());

            for (JsonElement jsonElement : attributeEntry.getValue().getAsJsonArray()) {
                JsonObject modifier = jsonElement.getAsJsonObject();

                String name = modifier.get("name").getAsString();
                double amount = modifier.get("amount").getAsDouble();
                String operation = modifier.get("operation").getAsString();
                EquipmentSlot slot = null;

                if (modifier.has("slot")){
                    slot = EquipmentSlot.valueOf(modifier.get("slot").getAsString());
                }

                meta.addAttributeModifier(attribute, new AttributeModifier(
                        UUID.randomUUID(),
                        name,
                        amount,
                        AttributeModifier.Operation.valueOf(operation),
                        slot
                ));
            }
        }

        return meta;
    }

    @Override
    public String getEnchantmentKey(Enchantment enchantment){
        return enchantment.getKey().getKey();
    }

    @Nullable
    @Override
    public Enchantment getEnchantmentFromKey(String key){
        Enchantment enchantment = Enchantment.getByName(key);

        if (enchantment != null){
            Bukkit.getLogger().warning("[UhcCore] Using old deprecated enchantment names, replace: " + key + " with " + enchantment.getKey().getKey());
            return enchantment;
        }

        NamespacedKey namespace;

        try{
            namespace = NamespacedKey.minecraft(key);
        }catch (IllegalArgumentException ex){
            return null;
        }

        return Enchantment.getByKey(namespace);
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
    public ItemMeta applySuspiciousStewEffects(ItemMeta meta, JsonArray effects) throws ParseException{
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