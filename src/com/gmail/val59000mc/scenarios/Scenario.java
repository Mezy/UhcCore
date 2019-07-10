package com.gmail.val59000mc.scenarios;

import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.ChatColor;
import com.gmail.val59000mc.scenarios.scenariolisteners.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;

public enum Scenario{
    CUTCLEAN("CutClean", UniversalMaterial.IRON_INGOT, CutCleanListener.class),
    FIRELESS("Fireless", UniversalMaterial.LAVA_BUCKET, FirelessListener.class),
    BOWLESS("Bowless", UniversalMaterial.BOW, BowlessListener.class),
    RODLESS("Rodless", UniversalMaterial.FISHING_ROD, RodlessListener.class),
    BLOODDIAMONDS("Blood Diamonds", UniversalMaterial.DIAMOND_ORE, BloodDiamondsListener.class),
    TIMBER("Timber", UniversalMaterial.OAK_LOG, TimberListener.class),
    HORSELESS("Horseless", UniversalMaterial.SADDLE, HorselessListener.class),
    TIMEBOMB("Timebomb", UniversalMaterial.TRAPPED_CHEST, TimebombListener.class),
    NOFALL("No-Fall", UniversalMaterial.FEATHER, NoFallListener.class),
    BESTPVE("BestPvE", UniversalMaterial.REDSTONE, BestPvEListener.class),
    TRIPLEORES("Triple Ores", UniversalMaterial.REDSTONE_ORE, TripleOresListener.class),
    TEAMINVENTORY("Team Inventory", UniversalMaterial.CHEST, null),
    NOCLEAN("No-Clean", UniversalMaterial.QUARTZ, NoCleanListener.class),
    HASTEYBOYS("Hastey Boys", UniversalMaterial.DIAMOND_PICKAXE, HasteyBoysListener.class),
    LUCKYLEAVES("Lucky Leaves", UniversalMaterial.OAK_LEAVES, LuckyLeavesListener.class),
    BLEEDINGSWEETS("Bleeding Sweets", UniversalMaterial.BOOK, BleedingSweetsListener.class),
    DOUBLEGOLD("Double Gold", UniversalMaterial.GOLD_INGOT, DoubleGoldListener.class),
    GOLDLESS("Gold Less", UniversalMaterial.GOLD_ORE, GoldLessListener.class),
    FLOWERPOWER("FlowerPower", UniversalMaterial.POPPY, FlowerPowerListener.class),
    SWITCHEROO("Switcheroo", UniversalMaterial.ARROW, SwitcherooListener.class),
    VEINMINER("Vein Miner", UniversalMaterial.COAL_ORE, VeinMinerListener.class);

    private String name;
    private UniversalMaterial material;
    private Class<? extends ScenarioListener> listener;

    Scenario(String name, UniversalMaterial material, Class<? extends ScenarioListener> listener){
        this.name = name;
        this.material = material;
        this.listener = listener;
    }

    public String getName() {
        return name;
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

        meta.setDisplayName(ChatColor.GOLD + name);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);
        return item;
    }

}