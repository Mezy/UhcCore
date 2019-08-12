package com.gmail.val59000mc.configuration.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class Option{

    private String path, displayName;
    private Category category;

    public Option(String path, Category category){
        this.path = path;
        this.category = category;

        displayName = ChatColor.DARK_GREEN + path.replaceAll("-", " ");
    }

    public abstract Object getValue();

    public abstract void onClick(Player player, ClickType clickType);

    public ItemStack getOptionItem(){
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getDisplayName());
        meta.setLore(Arrays.asList("Value: " + getValue()));
        item.setItemMeta(meta);
        return item;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getConfigPath(){
        if (category.isRoot()){
            return path;
        }
        return category.getConfigPath() + "." + path;
    }

    public void save(){
        category.getFile().set(getConfigPath(), getValue());
        category.saveConfig();
    }

}