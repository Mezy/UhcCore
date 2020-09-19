package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import org.bukkit.Sound;

public enum UniversalSound {
    CLICK("CLICK", "UI_BUTTON_CLICK", "UI_BUTTON_CLICK"),
    ENDERDRAGON_GROWL("ENDERDRAGON_GROWL", "ENTITY_ENDERDRAGON_GROWL", "ENTITY_ENDER_DRAGON_GROWL"),
    WITHER_SPAWN("WITHER_SPAWN", "ENTITY_WITHER_SPAWN", "ENTITY_WITHER_SPAWN"),
    FIREWORK_LAUNCH("FIREWORK_LAUNCH", "ENTITY_FIREWORK_LAUNCH", "ENTITY_FIREWORK_ROCKET_LAUNCH"),
    PLAYER_HURT("HURT_FLESH", "ENTITY_PLAYER_HURT", "ENTITY_PLAYER_HURT"),
    BLOCK_GRASS_BREAK("DIG_GRASS", "BLOCK_GRASS_BREAK", "BLOCK_GRASS_BREAK");

    private final String name8, name9, name13;
    private Sound sound;

    UniversalSound(String name8, String name9, String name13){
        this.name8 = name8;
        this.name9 = name9;
        this.name13 = name13;

        sound = null;
    }

    public Sound getSound(){
        if (sound == null){
            if (UhcCore.getVersion() < 9){
                sound = Sound.valueOf(name8);
            }else if (UhcCore.getVersion() < 13){
                sound = Sound.valueOf(name9);
            }else {
                sound = Sound.valueOf(name13);
            }
        }

        return sound;
    }
}