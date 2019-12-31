package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.game.GameManager;
import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Skull;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.UUID;

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
    public void setSkullOwner(Skull skull, Player player) {
        skull.setOwningPlayer(player);
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

    @Override
    public void setChestSide(Chest chest, org.bukkit.block.data.type.Chest.Type side) {
        org.bukkit.block.data.type.Chest chestData = (org.bukkit.block.data.type.Chest) chest.getBlockData();
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