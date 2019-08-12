package com.gmail.val59000mc.configuration.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class BooleanOption extends Option{

    private boolean value;

    public BooleanOption(String name, Category category, boolean value){
        super(name, category);
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        value = !value;
        save();
    }

}