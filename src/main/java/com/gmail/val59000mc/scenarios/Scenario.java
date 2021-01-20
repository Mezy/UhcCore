package com.gmail.val59000mc.scenarios;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.scenarios.scenariolisteners.*;
import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class Scenario {
    public static final Scenario CUTCLEAN = new Scenario("cutclean", UniversalMaterial.IRON_INGOT, CutCleanListener.class);
    public static final Scenario FIRELESS = new Scenario("fireless", UniversalMaterial.LAVA_BUCKET, FirelessListener.class);
    public static final Scenario BOWLESS = new Scenario("bowless", UniversalMaterial.BOW, BowlessListener.class);
    public static final Scenario RODLESS = new Scenario("rodless", UniversalMaterial.FISHING_ROD, RodlessListener.class);
    public static final Scenario SHIELDLESS = new Scenario("shieldless", UniversalMaterial.SHIELD, ShieldlessListener.class, 9);
    public static final Scenario BLOOD_DIAMONDS = new Scenario("blood_diamonds", UniversalMaterial.DIAMOND_ORE, BloodDiamondsListener.class);
    public static final Scenario TIMBER = new Scenario("timber", UniversalMaterial.OAK_LOG, TimberListener.class);
    public static final Scenario HORSELESS = new Scenario("horseless", UniversalMaterial.SADDLE, HorselessListener.class);
    public static final Scenario TIMEBOMB = new Scenario("timebomb", UniversalMaterial.TRAPPED_CHEST, TimebombListener.class);
    public static final Scenario NO_FALL = new Scenario("no_fall", UniversalMaterial.FEATHER, NoFallListener.class);
    public static final Scenario BEST_PVE = new Scenario("best_pve", UniversalMaterial.REDSTONE, BestPvEListener.class);
    public static final Scenario TRIPLE_ORES = new Scenario("triple_ores", UniversalMaterial.REDSTONE_ORE, TripleOresListener.class);
    public static final Scenario DOUBLE_ORES = new Scenario("double_ores", UniversalMaterial.REDSTONE_ORE, DoubleOresListener.class);
    public static final Scenario TEAM_INVENTORY = new Scenario("team_inventory", UniversalMaterial.CHEST, TeamInventoryListener.class);
    public static final Scenario NO_CLEAN = new Scenario("no_clean", UniversalMaterial.QUARTZ, NoCleanListener.class);
    public static final Scenario HASTEY_BOYS = new Scenario("hastey_boys", UniversalMaterial.DIAMOND_PICKAXE, HasteyBoysListener.class);
    public static final Scenario LUCKY_LEAVES = new Scenario("lucky_leaves", UniversalMaterial.OAK_LEAVES, LuckyLeavesListener.class);
    public static final Scenario BLEEDING_SWEETS = new Scenario("bleeding_sweets", UniversalMaterial.BOOK, BleedingSweetsListener.class);
    public static final Scenario DOUBLE_GOLD = new Scenario("double_gold", UniversalMaterial.GOLD_INGOT, DoubleGoldListener.class);
    public static final Scenario GOLDLESS = new Scenario("goldless", UniversalMaterial.GOLD_ORE, GoldLessListener.class);
    public static final Scenario FLOWER_POWER = new Scenario("flower_power", UniversalMaterial.SUNFLOWER, FlowerPowerListener.class);
    public static final Scenario SWITCHEROO = new Scenario("switcheroo", UniversalMaterial.ARROW, SwitcherooListener.class);
    public static final Scenario VEIN_MINER = new Scenario("vein_miner", UniversalMaterial.COAL_ORE, VeinMinerListener.class);
    public static final Scenario DRAGON_RUSH = new Scenario("dragon_rush", UniversalMaterial.DRAGON_EGG, DragonRushListener.class);
    public static final Scenario LOVE_AT_FIRST_SIGHT = new Scenario("love_at_first_sight", UniversalMaterial.POPPY, LoveAtFirstSightListener.class);
    public static final Scenario FAST_LEAVES_DECAY = new Scenario("fast_leaves_decay", UniversalMaterial.ACACIA_LEAVES, FastLeavesDecayListener.class);
    public static final Scenario SKY_HIGH = new Scenario("sky_high", UniversalMaterial.FEATHER, SkyHighListener.class);
    public static final Scenario FAST_SMELTING = new Scenario("fast_smelting", UniversalMaterial.FURNACE, FastSmeltingListener.class);
    public static final Scenario SUPERHEROES = new Scenario("superheroes", UniversalMaterial.NETHER_STAR, SuperHeroesListener.class);
    public static final Scenario ANONYMOUS = new Scenario("anonymous", UniversalMaterial.NAME_TAG, AnonymousListener.class);
    public static final Scenario GONE_FISHING = new Scenario("gone_fishing", UniversalMaterial.FISHING_ROD, GoneFishingListener.class);
    public static final Scenario INFINITE_ENCHANTS = new Scenario("infinite_enchants", UniversalMaterial.ENCHANTING_TABLE, InfiniteEnchantsListener.class);
    public static final Scenario CHILDREN_LEFT_UNATTENDED = new Scenario("children_left_unattended", UniversalMaterial.WOLF_SPAWN_EGG, ChildrenLeftUnattended.class);
    public static final Scenario SILENT_NIGHT = new Scenario("silent_night", UniversalMaterial.CLOCK, SilentNightListener.class);
    public static final Scenario PERMAKILL = new Scenario("permakill", UniversalMaterial.IRON_SWORD, PermaKillListener.class);
    public static final Scenario WEAKEST_LINK = new Scenario("weakest_link", UniversalMaterial.DIAMOND_SWORD, WeakestLinkListener.class);
    public static final Scenario EGGS = new Scenario("eggs", UniversalMaterial.EGG, EggsScenarioListener.class);
    public static final Scenario NO_GOING_BACK = new Scenario("no_going_back", UniversalMaterial.NETHER_BRICK);
    public static final Scenario DOUBLE_DATES = new Scenario("double_dates", UniversalMaterial.RED_BANNER, DoubleDatesListener.class);
    public static final Scenario FLY_HIGH = new Scenario("fly_high", UniversalMaterial.ELYTRA, FlyHighListener.class, 9);
    public static final Scenario RANDOMIZED_DROPS = new Scenario("randomized_drops", UniversalMaterial.EXPERIENCE_BOTTLE, RandomizedDropsListener.class);
    public static final Scenario UPSIDE_DOWN_CRAFTING = new Scenario("upside_down_crafting", UniversalMaterial.CRAFTING_TABLE, UpsideDownCraftsListener.class, 13);
    public static final Scenario RANDOMIZED_CRAFTS = new Scenario("randomized_crafts", UniversalMaterial.CRAFTING_TABLE, RandomizedCraftsListener.class, 13);
    public static final Scenario MONSTERS_INC = new Scenario("monsters_inc", UniversalMaterial.IRON_DOOR, MonstersIncListener.class);
    public static final Scenario ACHIEVEMENT_HUNTER = new Scenario("achievement_hunter", UniversalMaterial.BOOK, AchievementHunter.class);
    public static final Scenario NINE_SLOTS = new Scenario("nine_slots", UniversalMaterial.BARRIER, NineSlotsListener.class);
    public static final Scenario NETHER_START = new Scenario("nether_start", UniversalMaterial.LAVA_BUCKET, NetherStartListener.class);

    public static final Scenario[] BUILD_IN_SCENARIOS = new Scenario[]{
            CUTCLEAN,
            FIRELESS,
            BOWLESS,
            RODLESS,
            SHIELDLESS,
            BLOOD_DIAMONDS,
            TIMBER,
            HORSELESS,
            TIMEBOMB,
            NO_FALL,
            BEST_PVE,
            TRIPLE_ORES,
            DOUBLE_ORES,
            TEAM_INVENTORY,
            NO_CLEAN,
            HASTEY_BOYS,
            LUCKY_LEAVES,
            BLEEDING_SWEETS,
            DOUBLE_GOLD,
            GOLDLESS,
            FLOWER_POWER,
            SWITCHEROO,
            VEIN_MINER,
            DRAGON_RUSH,
            LOVE_AT_FIRST_SIGHT,
            FAST_LEAVES_DECAY,
            SKY_HIGH,
            FAST_SMELTING,
            SUPERHEROES,
            ANONYMOUS,
            GONE_FISHING,
            INFINITE_ENCHANTS,
            CHILDREN_LEFT_UNATTENDED,
            SILENT_NIGHT,
            PERMAKILL,
            WEAKEST_LINK,
            EGGS,
            NO_GOING_BACK,
            DOUBLE_DATES,
            FLY_HIGH,
            RANDOMIZED_DROPS,
            UPSIDE_DOWN_CRAFTING,
            RANDOMIZED_CRAFTS,
            MONSTERS_INC,
            ACHIEVEMENT_HUNTER,
            NINE_SLOTS,
            NETHER_START
    };

    private final String key;
    private final Material material;
    private final Class<? extends ScenarioListener> listener;
    private final int fromVersion;

    private Info info;

    public Scenario(String key, UniversalMaterial material){
        this(key, material.getType());
    }

    public Scenario(String key, Material material){
        this(key, material, null);
    }

    public Scenario(String key, UniversalMaterial material, Class<? extends ScenarioListener> listener){
        this(key, material.getType(), listener);
    }

    public Scenario(String key, Material material, Class<? extends ScenarioListener> listener){
        this(key, material, listener, 8);
    }

    public Scenario(String key, UniversalMaterial material, Class<? extends ScenarioListener> listener, int fromVersion){
        this(key, material.getType(), listener, fromVersion);
    }

    public Scenario(String key, Material material, Class<? extends ScenarioListener> listener, int fromVersion){
        this.key = key;
        this.material = material;
        this.listener = listener;
        this.fromVersion = fromVersion;
    }

    public String getKey() {
        return key;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public Material getMaterial() {
        return material;
    }

    @Nullable
    public Class<? extends ScenarioListener> getListener() {
        return listener;
    }

    public ItemStack getScenarioItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(Lang.SCENARIO_GLOBAL_ITEM_COLOR + getInfo().getName());
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(Collections.singletonList(Lang.SCENARIO_GLOBAL_ITEM_INFO));

        item.setItemMeta(meta);
        return item;
    }

    public boolean isCompatibleWithVersion(){
        return fromVersion <= UhcCore.getVersion();
    }

    public static class Info {
        private final String name;
        private final List<String> description;

        public Info(String name, List<String> description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public List<String> getDescription() {
            return description;
        }

    }

}
