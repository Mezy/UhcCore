package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.events.UhcStartedEvent;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.UniversalMaterial;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class BowFightersListener extends ScenarioListener {

@EventHandler
public void onGameStart(UhcStartedEvent e) {
    getPlayerManager().getOnlinePlayingPlayers().forEach(UhcPlayer -> {
        try {
            //players get 2 string, 4 arrows and an infinity book
            UhcPlayer.getPlayer().getInventory().addItem(UniversalMaterial.STRING.getStack(2));
            UhcPlayer.getPlayer().getInventory().addItem(UniversalMaterial.ARROW.getStack(4));

            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta)book.getItemMeta();
            meta.addStoredEnchant(Enchantment.ARROW_INFINITE, 1, true);
            book.setItemMeta(meta);

            UhcPlayer.getPlayer().getInventory().addItem(book);
        } catch (UhcPlayerNotOnlineException ex) {
            //offline players dont get items
        }
    });
}

@EventHandler
public void onCraftItem(CraftItemEvent e) {
    ItemStack item = e.getCurrentItem();

    if (item.getType().equals(Material.IRON_SWORD) || item.getType().equals(UniversalMaterial.GOLDEN_SWORD.getType())
        || item.getType().equals(Material.DIAMOND_SWORD)) {
            e.getWhoClicked().sendMessage(Lang.SCENARIO_BOWFIGHTERS_ERROR);
            e.setCancelled(true);
    }
    if (item.getType().equals(Material.IRON_AXE) || item.getType().equals(UniversalMaterial.GOLDEN_AXE.getType())
        || item.getType().equals(Material.DIAMOND_AXE)) {
            e.getWhoClicked().sendMessage(Lang.SCENARIO_BOWFIGHTERS_ERROR);
            e.setCancelled(true);
    }
}

@EventHandler
public void onItemSpawn(ItemSpawnEvent e) {
    ItemStack item = e.getEntity().getItemStack();

    if (item.getType().equals(Material.IRON_SWORD) || item.getType().equals(UniversalMaterial.GOLDEN_SWORD.getType())
        || item.getType().equals(Material.DIAMOND_SWORD)) {
            e.setCancelled(true);
    }
    if (item.getType().equals(Material.IRON_AXE) || item.getType().equals(UniversalMaterial.GOLDEN_AXE.getType())
        || item.getType().equals(Material.DIAMOND_AXE)) {
            e.setCancelled(true);
    }
}

}