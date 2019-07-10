package com.gmail.val59000mc.scenarios;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.commands.ScenarioCommandExecutor;
import com.gmail.val59000mc.commands.TeamInventoryCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ScenarioManager {

    private Map<Scenario, ScenarioListener> activeScenarios;

    public ScenarioManager(){
        activeScenarios = new HashMap<>();
    }

    public void addScenario(Scenario scenario){
        if (scenario.equals(Scenario.TRIPLEORES) && isActivated(Scenario.VEINMINER) ||
                isActivated(Scenario.TRIPLEORES) && scenario.equals(Scenario.VEINMINER)){
            Bukkit.broadcastMessage(ChatColor.RED + "Vein miner does not work in combination with triple ores!");
            return;
        }

        if (scenario.equals(Scenario.DOUBLEGOLD) && isActivated(Scenario.VEINMINER) ||
                isActivated(Scenario.DOUBLEGOLD) && scenario.equals(Scenario.VEINMINER)){
            Bukkit.broadcastMessage(ChatColor.RED + "Vein miner does not work in combination with double gold!");
            return;
        }

        Class<? extends ScenarioListener> listenerClass = scenario.getListener();

        try {
            ScenarioListener scenarioListener = null;
            if (listenerClass != null) {
                scenarioListener = listenerClass.newInstance();
                scenarioListener.onEnable();
                Bukkit.getServer().getPluginManager().registerEvents(scenarioListener, UhcCore.getPlugin());
            }

            activeScenarios.put(scenario, scenarioListener);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void removeScenario(Scenario scenario){
        ScenarioListener scenarioListener = activeScenarios.get(scenario);
        if (scenarioListener != null) {
            HandlerList.unregisterAll(scenarioListener);
            scenarioListener.onDisable();
        }
        activeScenarios.remove(scenario);
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

    public void registerListeners(){

        // Listeners
        //Bukkit.getPluginManager().registerEvents(new GoldenHeadListener(), UhcCore.getPlugin());
    }

    public Inventory getScenarioMainInventory(boolean editItem){

        Inventory inv = Bukkit.createInventory(null,27, ChatColor.GOLD + "" + ChatColor.BOLD + "Scenarios" + ChatColor.GRAY + " (Click for info)");

        for (Scenario scenario : getActiveScenarios()){
            inv.addItem(scenario.getScenarioItem());
        }

        if (editItem){
            // add edit item
            ItemStack edit = new ItemStack(Material.BARRIER);
            ItemMeta itemMeta = edit.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "Edit");
            edit.setItemMeta(itemMeta);

            inv.setItem(26,edit);
        }
        return inv;
    }

    public Inventory getScenarioEditInventory(){

        Inventory inv = Bukkit.createInventory(null,36, ChatColor.GOLD + "" + ChatColor.BOLD + "Scenarios" + ChatColor.GRAY + " (Edit)");

        // add edit item
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta itemMeta = back.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Back");
        back.setItemMeta(itemMeta);
        inv.setItem(27,back);

        for (Scenario scenario : Scenario.values()){

            ItemStack scenarioItem = scenario.getScenarioItem();
            if (isActivated(scenario)){
                scenarioItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            }
            inv.addItem(scenarioItem);
        }

        return inv;
    }

}