package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

    @Override
    public void replaceOceanBiomes() {
        Bukkit.getLogger().warning("[UhcCore] Ocean biomes won't be replaced, this feature is not supported on 1." + UhcCore.getVersion());
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
            TravelAgent travelAgent;

            try{
                Method getPortalTravelAgent = NMSUtils.getMethod(event.getClass(), "getPortalTravelAgent");
                travelAgent = (TravelAgent) getPortalTravelAgent.invoke(event);
            }catch (ReflectiveOperationException ex){
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

                meta.addAttributeModifier(attribute, new AttributeModifier(name, amount, AttributeModifier.Operation.valueOf(operation)));
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

}