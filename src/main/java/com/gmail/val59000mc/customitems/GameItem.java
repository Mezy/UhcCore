package com.gmail.val59000mc.customitems;

import com.gmail.val59000mc.configuration.MainConfig;
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
    TEAM_LIST(UniversalMaterial.PLAYER_HEAD),
    TEAM_SELECTION(UniversalMaterial.IRON_SWORD),
    KIT_SELECTION(UniversalMaterial.IRON_PICKAXE),
    SCENARIO_VIEWER(UniversalMaterial.PAPER),
    BUNGEE_ITEM(UniversalMaterial.BARRIER),

    // Team Setting Items
    TEAM_SETTINGS(UniversalMaterial.ANVIL),
    TEAM_COLOR_SELECTION(UniversalMaterial.LAPIS_LAZULI),
    TEAM_RENAME(UniversalMaterial.NAME_TAG),
    TEAM_READY(UniversalMaterial.LIME_WOOL),
    TEAM_NOT_READY(UniversalMaterial.RED_WOOL),

    // Team Items
    TEAM_LEAVE(UniversalMaterial.BARRIER),
    TEAM_VIEW_INVITES(UniversalMaterial.BOOK),
    TEAM_INVITE_PLAYER(UniversalMaterial.PAPER),
    TEAM_INVITE_PLAYER_SEARCH(UniversalMaterial.NAME_TAG),
    TEAM_INVITE_ACCEPT(UniversalMaterial.LIME_WOOL),
    TEAM_INVITE_DENY(UniversalMaterial.RED_WOOL),
    TEAM_FILL_BLACK(UniversalMaterial.BLACK_STAINED_GLASS_PANE),

    // Game Items
    CUSTOM_CRAFT_BOOK(UniversalMaterial.ENCHANTED_BOOK),
    COMPASS_ITEM(UniversalMaterial.COMPASS),

    // Scenario GUI items
    SCENARIOS_EDIT(UniversalMaterial.BARRIER),
    SCENARIOS_BACK(UniversalMaterial.ARROW),
    SCENARIOS_NEXT(UniversalMaterial.ARROW),

    UNKNOWN(UniversalMaterial.AIR);

    private static final String LORE_PREFIX = ChatColor.DARK_GRAY + "UHC Item";
    public static final GameItem[] LOBBY_ITEMS = new GameItem[]{
            TEAM_LIST,
            TEAM_SELECTION,
            KIT_SELECTION,
            CUSTOM_CRAFT_BOOK,
            SCENARIO_VIEWER,
            BUNGEE_ITEM
    };

    private final UniversalMaterial type;

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

    public ItemStack getItem(String addedLore){
        ItemStack item = type.getStack();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getItemName());
        meta.setLore(Arrays.asList(LORE_PREFIX, addedLore));
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
        MainConfig cfg = gm.getConfig();
        switch (this){
            case TEAM_SELECTION:
                return cfg.get(MainConfig.MAX_PLAYERS_PER_TEAM) > 1 || !cfg.get(MainConfig.TEAM_ALWAYS_READY);
            case TEAM_LIST:
                return cfg.get(MainConfig.MAX_PLAYERS_PER_TEAM) > 1;
            case KIT_SELECTION:
                return KitsManager.isAtLeastOneKit();
            case CUSTOM_CRAFT_BOOK:
                return CraftsManager.isAtLeastOneCraft();
            case TEAM_COLOR_SELECTION:
                return cfg.get(MainConfig.TEAM_COLORS);
            case SCENARIO_VIEWER:
                return !gm.getScenarioManager().getEnabledScenarios().isEmpty() || cfg.get(MainConfig.ENABLE_SCENARIO_VOTING);
            case BUNGEE_ITEM:
                return cfg.get(MainConfig.ENABLE_BUNGEE_SUPPORT) && cfg.get(MainConfig.ENABLE_BUNGEE_LOBBY_ITEM);
            case COMPASS_ITEM:
                return cfg.get(MainConfig.ENABLE_PLAYING_COMPASS);
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
                return Lang.TEAM_ITEM_COLOR;
            case SCENARIO_VIEWER:
                return Lang.SCENARIO_GLOBAL_ITEM_HOTBAR;
            case BUNGEE_ITEM:
                return Lang.ITEMS_BUNGEE;
            case COMPASS_ITEM:
                return Lang.ITEMS_COMPASS_PLAYING;
            case TEAM_SETTINGS:
                return Lang.TEAM_ITEM_SETTINGS;
            case TEAM_RENAME:
                return Lang.TEAM_ITEM_RENAME;
            case TEAM_READY:
                return Lang.TEAM_ITEM_READY;
            case TEAM_NOT_READY:
                return Lang.TEAM_ITEM_NOT_READY;
            case TEAM_LEAVE:
                return Lang.TEAM_ITEM_LEAVE;
            case TEAM_INVITE_PLAYER:
                return Lang.TEAM_ITEM_INVITE;
            case TEAM_INVITE_PLAYER_SEARCH:
                return Lang.TEAM_ITEM_INVITE_SEARCH;
            case TEAM_VIEW_INVITES:
                return Lang.TEAM_ITEM_INVITES;
            case TEAM_INVITE_ACCEPT:
                return Lang.TEAM_ITEM_INVITE_ACCEPT;
            case TEAM_INVITE_DENY:
                return Lang.TEAM_ITEM_INVITE_DENY;
            case TEAM_LIST:
                return Lang.ITEMS_TEAM_LIST;
            case TEAM_FILL_BLACK:
                return ChatColor.RESET.toString();
            case SCENARIOS_EDIT:
                return Lang.SCENARIO_GLOBAL_ITEM_EDIT;
            case SCENARIOS_BACK:
                return Lang.SCENARIO_GLOBAL_ITEM_BACK;
            case SCENARIOS_NEXT:
                return Lang.SCENARIO_GLOBAL_ITEM_NEXT;
        }
        return "Unknown item!";
    }

    public static boolean isGameItem(ItemStack item){
        if (!item.hasItemMeta()){
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName() || !meta.hasLore()){
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