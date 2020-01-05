package com.gmail.val59000mc.customitems;

import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.utils.UniversalMaterial;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

public enum GameItem{
    // Lobby Items
    TEAM_SELECTION(UniversalMaterial.IRON_SWORD),
    KIT_SELECTION(UniversalMaterial.IRON_PICKAXE),
    TEAM_COLOR_SELECTION(UniversalMaterial.LAPIS_LAZULI),
    SCENARIO_VIEWER(UniversalMaterial.PAPER),
    BUNGEE_ITEM(UniversalMaterial.BARRIER),

    // Game Items
    CUSTOM_CRAFT_BOOK(UniversalMaterial.ENCHANTED_BOOK),
    COMPASS_ITEM(UniversalMaterial.COMPASS),

    UNKNOWN(UniversalMaterial.AIR);

    private static final String LORE_PREFIX = ChatColor.DARK_GRAY + "UHC Item";
    public static final GameItem[] LOBBY_ITEMS = new GameItem[]{
            TEAM_SELECTION,
            KIT_SELECTION,
            CUSTOM_CRAFT_BOOK,
            TEAM_COLOR_SELECTION,
            SCENARIO_VIEWER,
            BUNGEE_ITEM
    };

    private UniversalMaterial type;

    GameItem(UniversalMaterial type){
        this.type = type;
    }

    public ItemStack getItem(){
        ItemStack item = type.getStack();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getItemName());
        meta.setLore(Collections.singletonList(LORE_PREFIX));
        item.setItemMeta(meta);
        return item;
    }

    public boolean equals(ItemStack item){
        Validate.notNull(item);
        if (item.getType() != type.getType()){
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        Validate.notNull(meta);
        return meta.getDisplayName().equals(getItemName());
    }

    public boolean meetsUsageRequirements(){
        GameManager gm = GameManager.getGameManager();
        MainConfiguration cfg = gm.getConfiguration();
        switch (this){
            case TEAM_SELECTION:
                return cfg.getMaxPlayersPerTeam() > 1 || !cfg.getTeamAlwaysReady();
            case KIT_SELECTION:
                return KitsManager.isAtLeastOneKit();
            case CUSTOM_CRAFT_BOOK:
                return CraftsManager.isAtLeastOneCraft();
            case TEAM_COLOR_SELECTION:
                return cfg.getUseTeamColors();
            case SCENARIO_VIEWER:
                return true;
            case BUNGEE_ITEM:
                return cfg.getEnableBungeeSupport() && cfg.getEnableBungeeLobbyItem();
            case COMPASS_ITEM:
                return cfg.getEnablePlayingCompass();
            case UNKNOWN:
                return false;
        }
        return true;
    }

    private String getItemName(){
        switch (this){
            case TEAM_SELECTION:
                return Lang.ITEMS_SWORD;
            case KIT_SELECTION:
                return Lang.ITEMS_KIT_SELECTION;
            case CUSTOM_CRAFT_BOOK:
                return Lang.ITEMS_CRAFT_BOOK;
            case TEAM_COLOR_SELECTION:
                return Lang.TEAM_COLOR_ITEM;
            case SCENARIO_VIEWER:
                return Lang.SCENARIO_GLOBAL_ITEM_HOTBAR;
            case BUNGEE_ITEM:
                return Lang.ITEMS_BUNGEE;
            case COMPASS_ITEM:
                return Lang.ITEMS_COMPASS_PLAYING;
        }
        return "Unknown item!";
    }

    public static boolean isGameItem(ItemStack item){
        if (!item.hasItemMeta()){
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()){
            return false;
        }
        return meta.getLore().contains(LORE_PREFIX);
    }

    public static GameItem getGameItem(ItemStack item){
        for (GameItem lobbyItem : GameItem.values()){
            if (lobbyItem.equals(item)){
                return lobbyItem;
            }
        }
        return UNKNOWN;
    }

}