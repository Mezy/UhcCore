package com.gmail.val59000mc.scenarios;

import com.gmail.val59000mc.scenarios.scenariolisteners.*;
import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;

public enum Scenario{
    CUTCLEAN("CutClean", UniversalMaterial.IRON_INGOT, CutCleanListener.class, "&6CutClean&7:", "&7- No furnaces required.", "&7- Ores and animal drops are automatically smelted.", "&7- Apple rates are 5%, flint rates are 90%", "&7- No lapis is needed for enchanting."),
    FIRELESS("Fireless", UniversalMaterial.LAVA_BUCKET, FirelessListener.class, "&6Fireless&7:", "&7- You cannot take fire damage."),
    BOWLESS("Bowless", UniversalMaterial.BOW, BowlessListener.class, "&6Bowless&7:", "&7- You are not able to craft bows."),
    RODLESS("Rodless", UniversalMaterial.FISHING_ROD, RodlessListener.class, "&6Rodless&7:", "&7- Fishing rods are not craftable."),
    BLOODDIAMONDS("Blood Diamonds", UniversalMaterial.DIAMOND_ORE, BloodDiamondsListener.class, "&6Blood Diamonds&7:", "&7- When mining a diamond ore, the player will take half a heart of damage."),
    TIMBER("Timber", UniversalMaterial.OAK_LOG, TimberListener.class, "&6Timber&7:", "&7- Breaking a log of a tree will cause the whole tree to fall down."),
    HORSELESS("Horseless", UniversalMaterial.SADDLE, HorselessListener.class, "&6Horseless&7:", "&7- You are not able to tame horses/donkeys."),
    TIMEBOMB("Timebomb", UniversalMaterial.TRAPPED_CHEST, TimebombListener.class, "&6Timebomb&7:", "&7- After killing a player, their loot will drop into a double chest.", "&7- The chest will explode 30 seconds later."),
    NOFALL("No-Fall", UniversalMaterial.FEATHER, NoFallListener.class, "&6Nofall&7:", "&7- Fall damage is off."),
    BESTPVE("BestPvE", UniversalMaterial.REDSTONE, BestPvEListener.class, "&6Best PvE&7:", "&7- Each player will be added to the \"Best PvE List\" at the start of the game.", "&7- While being on the list , you will gain 1 extra heart every 10 minutes.", "&7- Once a player takes damage, the player will be removed from the list.", "&7- If you kill a player, you are added back to the list."),
    TRIPLEORES("Triple Ores", UniversalMaterial.REDSTONE_ORE, TripleOresListener.class, "&6Triple Ores&7:", "&7- Ores drop in threes."),
    TEAMINVENTORY("Team Inventory", UniversalMaterial.CHEST, null, "&6Team Inventory&7:", "&7- Each team has an extra inventory that all members from the team can view by using /teaminventory."),
    NOCLEAN("No-Clean", UniversalMaterial.QUARTZ, NoCleanListener.class, "&6NoClean&7:", "&7- Gives you 30 seconds of invincibility after a kill.", "&7- Attacking other players will remove this PvP Protection."),
    HASTEYBOYS("Hastey Boys", UniversalMaterial.DIAMOND_PICKAXE, HasteyBoysListener.class, "&6HasteyBoys&7:", "&7- Every tool you craft will have Efficiency 3 and Unbreaking 1"),
    LUCKYLEAVES("Lucky Leaves", UniversalMaterial.OAK_LEAVES, LuckyLeavesListener.class, "&6LuckyLeaves&7:", "&7- Trees have a 0.5% chance of dropping a Golden Apple."),
    BLEEDINGSWEETS("Bleeding Sweets", UniversalMaterial.BOOK, BleedingSweetsListener.class, "&6BleedingSweets&7", "&7- On death a player drops 1 diamond, 5 gold, 1 book, 2 string and 16 arrows"),
    DOUBLEGOLD("Double Gold", UniversalMaterial.GOLD_INGOT, DoubleGoldListener.class, "&6DoubleGold&7:", "&7- The original amount of gold will double when mined."),
    GOLDLESS("Gold Less", UniversalMaterial.GOLD_ORE, GoldLessListener.class, "&6GoldLess&7:", "&7- When gold is mined, it will disappear and is not obtainable."),
    FLOWERPOWER("FlowerPower", UniversalMaterial.SUNFLOWER, FlowerPowerListener.class, "&6FlowerPower&7:", "&7- Break flowers to receive items."),
    SWITCHEROO("Switcheroo", UniversalMaterial.ARROW, SwitcherooListener.class, "&6Switcheroo&7:", "&7- When you shoot someone, you trade places with them"),
    VEINMINER("Vein Miner", UniversalMaterial.COAL_ORE, VeinMinerListener.class, "&6VeinMiner&7:", "&7- Mining a block while crouched breaks blocks of the same type in a chain reaction", "&7- Only works when using the correct tool for the block type"),
    DRAGONRUSH("DragonRush", UniversalMaterial.DRAGON_EGG, DragonRushListener.class, "&6DragonRush&7:", "&7- In dragon rush the goal is to kill the dragon.", "&7- The first team to kill the dragon wins!", "&7- There is a end portal at 0 0"),
    LOVEATFIRSTSIGHT("Love At First Sight", UniversalMaterial.POPPY, LoveAtFirstSightListener.class, "&6Love At First Sight&7:", "&7- In this scenario you are not able to choose your teammates trough the team selection inventory.", "&7- Teams are created by clicking on someone in game."),
    FASTLEAVESDECAY("Fast Leaves Decay", UniversalMaterial.ACACIA_LEAVES, FastLeavesDecayListener.class, "&6Fast Leaves Decay&7:", "&7- In this scenario after breaking all logs of a tree the leaves will be gone within seconds");

    private String name;
    private UniversalMaterial material;
    private Class<? extends ScenarioListener> listener;
    private String[] info;

    Scenario(String name, UniversalMaterial material, Class<? extends ScenarioListener> listener, String... info){
        this.name = name;
        this.material = material;
        this.listener = listener;
        this.info = info;
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

    public String[] getInfo() {
        return info;
    }

    public void setInfo(String[] info) {
        this.info = info;
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