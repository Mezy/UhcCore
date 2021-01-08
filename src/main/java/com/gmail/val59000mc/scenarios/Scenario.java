package com.gmail.val59000mc.scenarios;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.scenarios.scenariolisteners.*;
import com.gmail.val59000mc.utils.UniversalMaterial;
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
    public static final Scenario BLOODDIAMONDS = new Scenario("blooddiamonds", UniversalMaterial.DIAMOND_ORE, BloodDiamondsListener.class);
    public static final Scenario TIMBER = new Scenario("timber", UniversalMaterial.OAK_LOG, TimberListener.class);
    public static final Scenario HORSELESS = new Scenario("horseless", UniversalMaterial.SADDLE, HorselessListener.class);
    public static final Scenario TIMEBOMB = new Scenario("timebomb", UniversalMaterial.TRAPPED_CHEST, TimebombListener.class);
    public static final Scenario NOFALL = new Scenario("nofall", UniversalMaterial.FEATHER, NoFallListener.class);
    public static final Scenario BESTPVE = new Scenario("bestpve", UniversalMaterial.REDSTONE, BestPvEListener.class);
    public static final Scenario TRIPLEORES = new Scenario("tripleores", UniversalMaterial.REDSTONE_ORE, TripleOresListener.class);
    public static final Scenario DOUBLEORES = new Scenario("doubleores", UniversalMaterial.REDSTONE_ORE, DoubleOresListener.class);
    public static final Scenario TEAMINVENTORY = new Scenario("teaminventory", UniversalMaterial.CHEST, TeamInventoryListener.class);
    public static final Scenario NOCLEAN = new Scenario("noclean", UniversalMaterial.QUARTZ, NoCleanListener.class);
    public static final Scenario HASTEYBOYS = new Scenario("hasteyboys", UniversalMaterial.DIAMOND_PICKAXE, HasteyBoysListener.class);
    public static final Scenario LUCKYLEAVES = new Scenario("luckyleaves", UniversalMaterial.OAK_LEAVES, LuckyLeavesListener.class);
    public static final Scenario BLEEDINGSWEETS = new Scenario("bleedingsweets", UniversalMaterial.BOOK, BleedingSweetsListener.class);
    public static final Scenario DOUBLEGOLD = new Scenario("doublegold", UniversalMaterial.GOLD_INGOT, DoubleGoldListener.class);
    public static final Scenario GOLDLESS = new Scenario("goldless", UniversalMaterial.GOLD_ORE, GoldLessListener.class);
    public static final Scenario FLOWERPOWER = new Scenario("flowerpower", UniversalMaterial.SUNFLOWER, FlowerPowerListener.class);
    public static final Scenario SWITCHEROO = new Scenario("switcheroo", UniversalMaterial.ARROW, SwitcherooListener.class);
    public static final Scenario VEINMINER = new Scenario("veinminer", UniversalMaterial.COAL_ORE, VeinMinerListener.class);
    public static final Scenario DRAGONRUSH = new Scenario("dragonrush", UniversalMaterial.DRAGON_EGG, DragonRushListener.class);
    public static final Scenario LOVEATFIRSTSIGHT = new Scenario("loveatfirstsight", UniversalMaterial.POPPY, LoveAtFirstSightListener.class);
    public static final Scenario FASTLEAVESDECAY = new Scenario("fastleavesdecay", UniversalMaterial.ACACIA_LEAVES, FastLeavesDecayListener.class);
    public static final Scenario SKYHIGH = new Scenario("skyhigh", UniversalMaterial.FEATHER, SkyHighListener.class);
    public static final Scenario FASTSMELTING = new Scenario("fastsmelting", UniversalMaterial.FURNACE, FastSmeltingListener.class);
    public static final Scenario SUPERHEROES = new Scenario("superheroes", UniversalMaterial.NETHER_STAR, SuperHeroesListener.class);
    public static final Scenario ANONYMOUS = new Scenario("anonymous", UniversalMaterial.NAME_TAG, AnonymousListener.class);
    public static final Scenario GONEFISHING = new Scenario("gonefishing", UniversalMaterial.FISHING_ROD, GoneFishingListener.class);
    public static final Scenario INFINITEENCHANTS = new Scenario("infiniteenchants", UniversalMaterial.ENCHANTING_TABLE, InfiniteEnchantsListener.class);
    public static final Scenario CHILDRENLEFTUNATTENDED = new Scenario("childrenleftunattended", UniversalMaterial.WOLF_SPAWN_EGG, ChildrenLeftUnattended.class);
    public static final Scenario SILENTNIGHT = new Scenario("silentnight", UniversalMaterial.CLOCK, SilentNightListener.class);
    // TODO: Fix bugs before releasing. public static final Scenario SHAREDHEALTH = new Scenario("sharedhealth", UniversalMaterial.RED_DYE, SharedHealthListener.class);
    public static final Scenario PERMAKILL = new Scenario("permakill", UniversalMaterial.IRON_SWORD, PermaKillListener.class);
    public static final Scenario WEAKESTLINK = new Scenario("weakestlink", UniversalMaterial.DIAMOND_SWORD, WeakestLinkListener.class);
    public static final Scenario EGGS = new Scenario("eggs", UniversalMaterial.EGG, EggsScenarioListener.class);
    public static final Scenario NOGOINGBACK = new Scenario("nogoingback", UniversalMaterial.NETHER_BRICK);
    public static final Scenario DOUBLEDATES = new Scenario("doubledates", UniversalMaterial.RED_BANNER, DoubleDatesListener.class);
    public static final Scenario FLYHIGH = new Scenario("flyhigh", UniversalMaterial.ELYTRA, FlyHighListener.class, 9);
    public static final Scenario RANDOMIZEDDROPS = new Scenario("randomizeddrops", UniversalMaterial.EXPERIENCE_BOTTLE, RandomizedDropsListener.class);
    public static final Scenario UPSIDEDOWNCRAFTING = new Scenario("upsidedowncrafting", UniversalMaterial.CRAFTING_TABLE, UpsideDownCraftsListener.class, 13);
    public static final Scenario RANDOMIZEDCRAFTS = new Scenario("randomizedcrafts", UniversalMaterial.CRAFTING_TABLE, RandomizedCraftsListener.class, 13);
    public static final Scenario MONSTERSINC = new Scenario("monstersinc", UniversalMaterial.IRON_DOOR, MonstersIncListener.class);
    public static final Scenario ACHIEVEMENTHUNTER = new Scenario("achievementhunter", UniversalMaterial.BOOK, AchievementHunter.class);
    public static final Scenario NINESLOTS = new Scenario("nineslots", UniversalMaterial.BARRIER, NineSlotsListener.class);
    public static final Scenario NETHERSTART = new Scenario("netherstart", UniversalMaterial.LAVA_BUCKET, NetherStartListener.class);

    public static final Scenario[] BUILD_IN_SCENARIOS = new Scenario[]{
            CUTCLEAN,
            FIRELESS,
            BOWLESS,
            RODLESS,
            SHIELDLESS,
            BLOODDIAMONDS,
            TIMBER,
            HORSELESS,
            TIMEBOMB,
            NOFALL,
            BESTPVE,
            TRIPLEORES,
            DOUBLEORES,
            TEAMINVENTORY,
            NOCLEAN,
            HASTEYBOYS,
            LUCKYLEAVES,
            BLEEDINGSWEETS,
            DOUBLEGOLD,
            GOLDLESS,
            FLOWERPOWER,
            SWITCHEROO,
            VEINMINER,
            DRAGONRUSH,
            LOVEATFIRSTSIGHT,
            FASTLEAVESDECAY,
            SKYHIGH,
            FASTSMELTING,
            SUPERHEROES,
            ANONYMOUS,
            GONEFISHING,
            INFINITEENCHANTS,
            CHILDRENLEFTUNATTENDED,
            SILENTNIGHT,
            PERMAKILL,
            WEAKESTLINK,
            EGGS,
            NOGOINGBACK,
            DOUBLEDATES,
            FLYHIGH,
            RANDOMIZEDDROPS,
            UPSIDEDOWNCRAFTING,
            RANDOMIZEDCRAFTS,
            MONSTERSINC,
            ACHIEVEMENTHUNTER,
            NINESLOTS,
            NETHERSTART
    };

    private final String key;
    private final UniversalMaterial material;
    private final Class<? extends ScenarioListener> listener;
    private final int fromVersion;

    private String name;
    private List<String> description;

    Scenario(String key, UniversalMaterial material){
        this(key, material, null);
    }

    Scenario(String key, UniversalMaterial material, Class<? extends ScenarioListener> listener){
        this(key, material, listener, 8);
    }

    Scenario(String key, UniversalMaterial material, Class<? extends ScenarioListener> listener, int fromVersion){
        this.key = key;
        this.material = material;
        this.listener = listener;
        this.fromVersion = fromVersion;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public UniversalMaterial getMaterial() {
        return material;
    }

    @Nullable
    public Class<? extends ScenarioListener> getListener() {
        return listener;
    }

    public boolean equals(String name){
        return name.contains(getName()) || name.replace(" ", "").toLowerCase().equals(key);
    }

    public ItemStack getScenarioItem(){
        ItemStack item = material.getStack();
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(Lang.SCENARIO_GLOBAL_ITEM_COLOR + name);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(Collections.singletonList(Lang.SCENARIO_GLOBAL_ITEM_INFO));

        item.setItemMeta(meta);
        return item;
    }

    public boolean isCompatibleWithVersion(){
        return fromVersion <= UhcCore.getVersion();
    }

}
