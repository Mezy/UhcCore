package com.gmail.val59000mc.scenarios;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.configuration.YamlFile;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.utils.FileUtils;
import com.gmail.val59000mc.utils.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class ScenarioManager {

    private static final int ROW = 9;
    private final Map<Scenario, ScenarioListener> activeScenarios;

    public ScenarioManager(){
        activeScenarios = new HashMap<>();
    }

    public void addScenario(Scenario scenario){
        if (isActivated(scenario)){
            return;
        }

        Class<? extends ScenarioListener> listenerClass = scenario.getListener();

        try {
            ScenarioListener scenarioListener = null;
            if (listenerClass != null) {
                scenarioListener = listenerClass.newInstance();
            }

            activeScenarios.put(scenario, scenarioListener);

            if (scenarioListener != null) {
                loadScenarioOptions(scenario, scenarioListener);
                scenarioListener.onEnable();

                // If disabled in the onEnable method don't register listener.
                if (isActivated(scenario)) {
                    Bukkit.getServer().getPluginManager().registerEvents(scenarioListener, UhcCore.getPlugin());
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void removeScenario(Scenario scenario){
        ScenarioListener scenarioListener = activeScenarios.get(scenario);
        activeScenarios.remove(scenario);

        if (scenarioListener != null) {
            HandlerList.unregisterAll(scenarioListener);
            scenarioListener.onDisable();
        }
    }

    public boolean toggleScenario(Scenario scenario){
        if (isActivated(scenario)){
            removeScenario(scenario);
            return false;
        }

        addScenario(scenario);
        return true;
    }

    public synchronized Set<Scenario> getActiveScenarios(){
        return activeScenarios.keySet();
    }

    public boolean isActivated(Scenario scenario){
        return activeScenarios.containsKey(scenario);
    }

    public ScenarioListener getScenarioListener(Scenario scenario){
        return activeScenarios.get(scenario);
    }

    public void loadDefaultScenarios(MainConfig cfg){
        if (cfg.get(MainConfig.ENABLE_DEFAULT_SCENARIOS)){
            List<Scenario> defaultScenarios = cfg.get(MainConfig.DEFAULT_SCENARIOS);
            for (Scenario scenario : defaultScenarios){
                Bukkit.getLogger().info("[UhcCore] Loading " + scenario.getName());
                addScenario(scenario);
            }
        }
    }

    public Inventory getScenarioMainInventory(boolean editItem){

        Inventory inv = Bukkit.createInventory(null,3*ROW, Lang.SCENARIO_GLOBAL_INVENTORY);

        for (Scenario scenario : getActiveScenarios()) {
            if (scenario.isCompatibleWithVersion()) {
                inv.addItem(scenario.getScenarioItem());
            }
        }

        if (editItem){
            // add edit item
            ItemStack edit = new ItemStack(Material.BARRIER);
            ItemMeta itemMeta = edit.getItemMeta();
            itemMeta.setDisplayName(Lang.SCENARIO_GLOBAL_ITEM_EDIT);
            edit.setItemMeta(itemMeta);

            inv.setItem(26,edit);
        }
        return inv;
    }

    public Inventory getScenarioEditInventory(){

        Inventory inv = Bukkit.createInventory(null,6*ROW, Lang.SCENARIO_GLOBAL_INVENTORY_EDIT);

        // add edit item
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta itemMeta = back.getItemMeta();
        itemMeta.setDisplayName(Lang.SCENARIO_GLOBAL_ITEM_BACK);
        back.setItemMeta(itemMeta);
        inv.setItem(5*ROW+8,back);

        for (Scenario scenario : Scenario.values()){
            if (!scenario.isCompatibleWithVersion()){
                continue;
            }

            ItemStack scenarioItem = scenario.getScenarioItem();
            if (isActivated(scenario)){
                scenarioItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                scenarioItem.setAmount(2);
            }
            inv.addItem(scenarioItem);
        }

        return inv;
    }

    public Inventory getScenarioVoteInventory(UhcPlayer uhcPlayer){
        Set<Scenario> playerVotes = uhcPlayer.getScenarioVotes();
        List<Scenario> blacklist = GameManager.getGameManager().getConfig().get(MainConfig.SCENARIO_VOTING_BLACKLIST);
        Inventory inv = Bukkit.createInventory(null,6*ROW, Lang.SCENARIO_GLOBAL_INVENTORY_VOTE);

        for (Scenario scenario : Scenario.values()){
            // Don't add to menu when blacklisted / not compatible / already enabled.
            if (blacklist.contains(scenario) || !scenario.isCompatibleWithVersion() || isActivated(scenario)){
                continue;
            }

            ItemStack item = scenario.getScenarioItem();

            if (playerVotes.contains(scenario)) {
                item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                item.setAmount(2);
            }
            inv.addItem(item);
        }
        return inv;
    }

    public void disableAllScenarios(){
        Set<Scenario> active = new HashSet<>(getActiveScenarios());
        for (Scenario scenario : active){
            removeScenario(scenario);
        }
    }

    public void countVotes(){
        Map<Scenario, Integer> votes = new HashMap<>();

        List<Scenario> blacklist = GameManager.getGameManager().getConfig().get(MainConfig.SCENARIO_VOTING_BLACKLIST);
        for (Scenario scenario : Scenario.values()){
            if (!blacklist.contains(scenario)) {
                votes.put(scenario, 0);
            }
        }

        for (UhcPlayer uhcPlayer : GameManager.getGameManager().getPlayersManager().getPlayersList()){
            for (Scenario scenario : uhcPlayer.getScenarioVotes()){
                int totalVotes = votes.get(scenario) + 1;
                votes.put(scenario, totalVotes);
            }
        }

        int scenarioCount = GameManager.getGameManager().getConfig().get(MainConfig.ELECTED_SCENARIO_COUNT);
        while (scenarioCount > 0){
            // get scenario with most votes
            Scenario scenario = null;
            int scenarioVotes = 0;

            for (Scenario s : votes.keySet()){
                // Don't let people vote for scenarios that are enabled by default.
                if (isActivated(s)){
                    continue;
                }

                if (scenario == null || votes.get(s) > scenarioVotes){
                    scenario = s;
                    scenarioVotes = votes.get(s);
                }
            }

            addScenario(scenario);
            votes.remove(scenario);
            scenarioCount--;
        }
    }

    private void loadScenarioOptions(Scenario scenario, ScenarioListener listener) throws ReflectiveOperationException, IOException, InvalidConfigurationException{
        List<Field> optionFields = NMSUtils.getAnnotatedFields(listener.getClass(), Option.class);

        if (optionFields.isEmpty()){
            return;
        }

        YamlFile cfg = FileUtils.saveResourceIfNotAvailable("scenarios.yml");

        for (Field field : optionFields){
            Option option = field.getAnnotation(Option.class);
            String key = option.key().isEmpty() ? field.getName() : option.key();
            Object value = cfg.get(scenario.name().toLowerCase() + "." + key, field.get(listener));
            field.set(listener, value);
        }

        if (cfg.addedDefaultValues()){
            cfg.saveWithComments();
        }
    }

}