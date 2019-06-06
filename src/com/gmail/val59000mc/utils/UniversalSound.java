package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import org.bukkit.Sound;

public enum UniversalSound {
    CLICK("CLICK", "UI_BUTTON_CLICK"),
    ENDERDRAGON_GROWL("ENDERDRAGON_GROWL", "ENTITY_ENDERDRAGON_GROWL"),
    WITHER_SPAWN("WITHER_SPAWN", "ENTITY_WITHER_SPAWN"),
    FIREWORK_LAUNCH("FIREWORK_LAUNCH", "ENTITY_FIREWORK_LAUNCH");

    private String name8, name9;

    private Sound sound;

    UniversalSound(String name8, String name9){
        this.name8 = name8;
        this.name9 = name9;

        sound = null;
    }

    public Sound getSound(){
        if (sound == null){
            if (UhcCore.getVersion() < 9){
                sound = Sound.valueOf(name8);
            }else {
                sound = Sound.valueOf(name9);
            }
        }

        return sound;
    }
}