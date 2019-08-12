package com.gmail.val59000mc.configuration.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class StringOption extends Option{

    private String value;

    public StringOption(String name, Category category, String value){
        super(name, category);
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        Bukkit.broadcastMessage("Not able to edit");
    }

}