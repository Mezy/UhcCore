package com.gmail.val59000mc.scenarios;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.scenarios.scenariolisteners.*;
import com.gmail.val59000mc.utils.UniversalMaterial;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Scenario{
    public static final Scenario CUTCLEAN = new Scenario("CUTCLEAN", UniversalMaterial.IRON_INGOT, CutCleanListener.class);
    public static final Scenario FIRELESS = new Scenario("FIRELESS", UniversalMaterial.LAVA_BUCKET, FirelessListener.class);
    public static final Scenario BOWLESS = new Scenario("BOWLESS", UniversalMaterial.BOW, BowlessListener.class);
    public static final Scenario RODLESS = new Scenario("RODLESS", UniversalMaterial.FISHING_ROD, RodlessListener.class);
    public static final Scenario SHIELDLESS = new Scenario("SHIELDLESS", UniversalMaterial.SHIELD, ShieldlessListener.class, 9);
    public static final Scenario BLOODDIAMONDS = new Scenario("BLOODDIAMONDS", UniversalMaterial.DIAMOND_ORE, BloodDiamondsListener.class);
    public static final Scenario TIMBER = new Scenario("TIMBER", UniversalMaterial.OAK_LOG, TimberListener.class);
    public static final Scenario HORSELESS = new Scenario("HORSELESS", UniversalMaterial.SADDLE, HorselessListener.class);
    public static final Scenario TIMEBOMB = new Scenario("TIMEBOMB", UniversalMaterial.TRAPPED_CHEST, TimebombListener.class);
    public static final Scenario NOFALL = new Scenario("NOFALL", UniversalMaterial.FEATHER, NoFallListener.class);
    public static final Scenario BESTPVE = new Scenario("BESTPVE", UniversalMaterial.REDSTONE, BestPvEListener.class);
    public static final Scenario TRIPLEORES = new Scenario("TRIPLEORES", UniversalMaterial.REDSTONE_ORE, TripleOresListener.class);
    public static final Scenario DOUBLEORES = new Scenario("DOUBLEORES", UniversalMaterial.REDSTONE_ORE, DoubleOresListener.class);
    public static final Scenario TEAMINVENTORY = new Scenario("TEAMINVENTORY", UniversalMaterial.CHEST);
    public static final Scenario NOCLEAN = new Scenario("NOCLEAN", UniversalMaterial.QUARTZ, NoCleanListener.class);
    public static final Scenario HASTEYBOYS = new Scenario("HASTEYBOYS", UniversalMaterial.DIAMOND_PICKAXE, HasteyBoysListener.class);
    public static final Scenario LUCKYLEAVES = new Scenario("LUCKYLEAVES", UniversalMaterial.OAK_LEAVES, LuckyLeavesListener.class);
    public static final Scenario BLEEDINGSWEETS = new Scenario("BLEEDINGSWEETS", UniversalMaterial.BOOK, BleedingSweetsListener.class);
    public static final Scenario DOUBLEGOLD = new Scenario("DOUBLEGOLD", UniversalMaterial.GOLD_INGOT, DoubleGoldListener.class);
    public static final Scenario GOLDLESS = new Scenario("GOLDLESS", UniversalMaterial.GOLD_ORE, GoldLessListener.class);
    public static final Scenario FLOWERPOWER = new Scenario("FLOWERPOWER", UniversalMaterial.SUNFLOWER, FlowerPowerListener.class);
    public static final Scenario SWITCHEROO = new Scenario("SWITCHEROO", UniversalMaterial.ARROW, SwitcherooListener.class);
    public static final Scenario VEINMINER = new Scenario("VEINMINER", UniversalMaterial.COAL_ORE, VeinMinerListener.class);
    public static final Scenario DRAGONRUSH = new Scenario("DRAGONRUSH", UniversalMaterial.DRAGON_EGG, DragonRushListener.class);
    public static final Scenario LOVEATFIRSTSIGHT = new Scenario("LOVEATFIRSTSIGHT", UniversalMaterial.POPPY, LoveAtFirstSightListener.class);
    public static final Scenario FASTLEAVESDECAY = new Scenario("FASTLEAVESDECAY", UniversalMaterial.ACACIA_LEAVES, FastLeavesDecayListener.class);
    public static final Scenario SKYHIGH = new Scenario("SKYHIGH", UniversalMaterial.FEATHER, SkyHighListener.class);
    public static final Scenario FASTSMELTING = new Scenario("FASTSMELTING", UniversalMaterial.FURNACE, FastSmeltingListener.class);
    public static final Scenario SUPERHEROES = new Scenario("SUPERHEROES", UniversalMaterial.NETHER_STAR, SuperHeroesListener.class);
    public static final Scenario ANONYMOUS = new Scenario("ANONYMOUS", UniversalMaterial.NAME_TAG, AnonymousListener.class);
    public static final Scenario GONEFISHING = new Scenario("GONEFISHING", UniversalMaterial.FISHING_ROD, GoneFishingListener.class);
    public static final Scenario INFINITEENCHANTS = new Scenario("INFINITEENCHANTS", UniversalMaterial.ENCHANTING_TABLE, InfiniteEnchantsListener.class);
    public static final Scenario CHILDRENLEFTUNATTENDED = new Scenario("CHILDRENLEFTUNATTENDED", UniversalMaterial.WOLF_SPAWN_EGG, ChildrenLeftUnattended.class);
    public static final Scenario SILENTNIGHT = new Scenario("SILENTNIGHT", UniversalMaterial.CLOCK, SilentNightListener.class);
    // TODO: Fix bugs before releasing. public static final Scenario SHAREDHEALTH = new Scenario("SHAREDHEALTH", UniversalMaterial.RED_DYE, SharedHealthListener.class);
    public static final Scenario PERMAKILL = new Scenario("PERMAKILL", UniversalMaterial.IRON_SWORD, PermaKillListener.class);
    public static final Scenario WEAKESTLINK = new Scenario("WEAKESTLINK", UniversalMaterial.DIAMOND_SWORD, WeakestLinkListener.class);
    public static final Scenario EGGS = new Scenario("EGGS", UniversalMaterial.EGG, EggsScenarioListener.class);
    public static final Scenario NOGOINGBACK = new Scenario("NOGOINGBACK", UniversalMaterial.NETHER_BRICK);
    public static final Scenario DOUBLEDATES = new Scenario("DOUBLEDATES", UniversalMaterial.RED_BANNER, DoubleDatesListener.class);
    public static final Scenario FLYHIGH = new Scenario("FLYHIGH", UniversalMaterial.ELYTRA, FlyHighListener.class, 9);
    public static final Scenario RANDOMIZEDDROPS = new Scenario("RANDOMIZEDDROPS", UniversalMaterial.EXPERIENCE_BOTTLE, RandomizedDropsListener.class);
    public static final Scenario UPSIDEDOWNCRAFTING = new Scenario("UPSIDEDOWNCRAFTING", UniversalMaterial.CRAFTING_TABLE, UpsideDownCraftsListener.class, 13);
    public static final Scenario RANDOMIZEDCRAFTS = new Scenario("RANDOMIZEDCRAFTS", UniversalMaterial.CRAFTING_TABLE, RandomizedCraftsListener.class, 13);
    public static final Scenario MONSTERSINC = new Scenario("MONSTERSINC", UniversalMaterial.IRON_DOOR, MonstersIncListener.class);
    public static final Scenario ACHIEVEMENTHUNTER = new Scenario("ACHIEVEMENTHUNTER", UniversalMaterial.BOOK, AchievementHunter.class);

    public static final Set<Scenario> BUILD_IN_SCENARIOS = new HashSet<>();

    static {
        Collections.addAll(BUILD_IN_SCENARIOS,
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
                ACHIEVEMENTHUNTER
        );
    }

    private String key;
    private UniversalMaterial material;
    private Class<? extends ScenarioListener> listener;
    private int fromVersion;

    private String name;
    private List<String> description;

    public Scenario(String key, UniversalMaterial material){
        this(key, material, null);
    }

    public Scenario(String key, UniversalMaterial material, Class<? extends ScenarioListener> listener){
        this(key, material, listener, 8);
    }

    public Scenario(String key, UniversalMaterial material, Class<? extends ScenarioListener> listener, int fromVersion){
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

    public String getLowerCase(){
        return getKey().toLowerCase();
    }

    public UniversalMaterial getMaterial() {
        return material;
    }

    @Nullable
    public Class<? extends ScenarioListener> getListener() {
        return listener;
    }

    public boolean equals(String name){
        return name.contains(getName()) || name.replace(" ", "").toLowerCase().equals(getLowerCase()) || getKey().equals(name);
    }

    public boolean isCompatibleWithVersion(){
        return fromVersion <= UhcCore.getVersion();
    }

}
