package com.gmail.val59000mc.configuration.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class IntegerOption extends Option{

    private int value;

    public IntegerOption(String name, Category category, int value){
        super(name, category);
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        switch (clickType){
            case LEFT:
                value++;
                break;
            case RIGHT:
                value--;
                break;
            case SHIFT_LEFT:
                value+=10;
                break;
            case SHIFT_RIGHT:
                value-=10;
                break;
            default:
                break;
        }
        save();
    }

}