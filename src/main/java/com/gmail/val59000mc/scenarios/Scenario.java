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

public enum Scenario{
    CUTCLEAN(UniversalMaterial.IRON_INGOT, CutCleanListener.class),
    FIRELESS(UniversalMaterial.LAVA_BUCKET, FirelessListener.class),
    BOWLESS(UniversalMaterial.BOW, BowlessListener.class),
    RODLESS(UniversalMaterial.FISHING_ROD, RodlessListener.class),
    SHIELDLESS(UniversalMaterial.SHIELD, ShieldlessListener.class, 9),
    BLOODDIAMONDS(UniversalMaterial.DIAMOND_ORE, BloodDiamondsListener.class),
    TIMBER(UniversalMaterial.OAK_LOG, TimberListener.class),
    HORSELESS(UniversalMaterial.SADDLE, HorselessListener.class),
    TIMEBOMB(UniversalMaterial.TRAPPED_CHEST, TimebombListener.class),
    NOFALL(UniversalMaterial.FEATHER, NoFallListener.class),
    BESTPVE(UniversalMaterial.REDSTONE, BestPvEListener.class),
    TRIPLEORES(UniversalMaterial.REDSTONE_ORE, TripleOresListener.class),
    DOUBLEORES(UniversalMaterial.REDSTONE_ORE, DoubleOresListener.class),
    TEAMINVENTORY(UniversalMaterial.CHEST),
    NOCLEAN(UniversalMaterial.QUARTZ, NoCleanListener.class),
    HASTEYBOYS(UniversalMaterial.DIAMOND_PICKAXE, HasteyBoysListener.class),
    LUCKYLEAVES(UniversalMaterial.OAK_LEAVES, LuckyLeavesListener.class),
    BLEEDINGSWEETS(UniversalMaterial.BOOK, BleedingSweetsListener.class),
    DOUBLEGOLD(UniversalMaterial.GOLD_INGOT, DoubleGoldListener.class),
    GOLDLESS(UniversalMaterial.GOLD_ORE, GoldLessListener.class),
    FLOWERPOWER(UniversalMaterial.SUNFLOWER, FlowerPowerListener.class),
    SWITCHEROO(UniversalMaterial.ARROW, SwitcherooListener.class),
    VEINMINER(UniversalMaterial.COAL_ORE, VeinMinerListener.class),
    DRAGONRUSH(UniversalMaterial.DRAGON_EGG, DragonRushListener.class),
    LOVEATFIRSTSIGHT(UniversalMaterial.POPPY, LoveAtFirstSightListener.class),
    FASTLEAVESDECAY(UniversalMaterial.ACACIA_LEAVES, FastLeavesDecayListener.class),
    SKYHIGH(UniversalMaterial.FEATHER, SkyHighListener.class),
    FASTSMELTING(UniversalMaterial.FURNACE, FastSmeltingListener.class),
    SUPERHEROES(UniversalMaterial.NETHER_STAR, SuperHeroesListener.class),
    ANONYMOUS(UniversalMaterial.NAME_TAG, AnonymousListener.class),
    GONEFISHING(UniversalMaterial.FISHING_ROD, GoneFishingListener.class),
    INFINITEENCHANTS(UniversalMaterial.ENCHANTING_TABLE, InfiniteEnchantsListener.class),
    CHILDRENLEFTUNATTENDED(UniversalMaterial.WOLF_SPAWN_EGG, ChildrenLeftUnattended.class),
    SILENTNIGHT(UniversalMaterial.CLOCK, SilentNightListener.class),
    // TODO: Fix bugs before releasing. SHAREDHEALTH(UniversalMaterial.RED_DYE, SharedHealthListener.class),
    PERMAKILL(UniversalMaterial.IRON_SWORD, PermaKillListener.class),
    WEAKESTLINK(UniversalMaterial.DIAMOND_SWORD, WeakestLinkListener.class),
    EGGS(UniversalMaterial.EGG, EggsScenarioListener.class),
    NOGOINGBACK(UniversalMaterial.NETHER_BRICK),
    DOUBLEDATES(UniversalMaterial.RED_BANNER, DoubleDatesListener.class),
    FLYHIGH(UniversalMaterial.ELYTRA, FlyHighListener.class, 9),
    RANDOMIZEDDROPS(UniversalMaterial.EXPERIENCE_BOTTLE, RandomizedDropsListener.class),
    UPSIDEDOWNCRAFTING(UniversalMaterial.CRAFTING_TABLE, UpsideDownCraftsListener.class, 13),
    RANDOMIZEDCRAFTS(UniversalMaterial.CRAFTING_TABLE, RandomizedCraftsListener.class, 13),
    MONSTERSINC(UniversalMaterial.IRON_DOOR, MonstersIncListener.class),
    ACHIEVEMENTHUNTER(UniversalMaterial.BOOK, AchievementHunter.class),
    NINESLOTS(UniversalMaterial.BARRIER, NineSlotsListener.class),
    NETHERSTART(UniversalMaterial.LAVA_BUCKET, NetherStartListener.class);

    private String name;
    private UniversalMaterial material;
    private Class<? extends ScenarioListener> listener;
    private int fromVersion;
    private List<String> description;

    Scenario(UniversalMaterial material){
        this(material, null);
    }

    Scenario(UniversalMaterial material, Class<? extends ScenarioListener> listener){
        this(material, listener, 8);
    }

    Scenario(UniversalMaterial material, Class<? extends ScenarioListener> listener, int fromVersion){
        this.material = material;
        this.listener = listener;
        this.fromVersion = fromVersion;
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
        return name().toLowerCase();
    }

    public UniversalMaterial getMaterial() {
        return material;
    }

    @Nullable
    public Class<? extends ScenarioListener> getListener() {
        return listener;
    }

    public boolean equals(String name){
        return name.contains(getName()) || name.replace(" ", "").toLowerCase().equals(name().toLowerCase());
    }

    public static Scenario getScenario(String s){

        for (Scenario scenario : values()){
            if (scenario.equals(s)){
                return scenario;
            }
        }
        return null;
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
