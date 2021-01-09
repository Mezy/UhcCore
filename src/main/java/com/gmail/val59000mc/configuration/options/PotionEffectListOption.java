package com.gmail.val59000mc.configuration.options;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PotionEffectListOption implements Option<List<PotionEffect>> {

    private final String path;

    public PotionEffectListOption(String path) {
        this.path = path;
    }

    @Override
    public List<PotionEffect> getValue(YamlConfiguration config) {
        List<String> potionStrList = config.getStringList(path);
        List<PotionEffect> potionEffects = new ArrayList<>();

        for(String potionStr : potionStrList){
            try{
                String[] potionArr = potionStr.split("/");
                PotionEffectType type = PotionEffectType.getByName(potionArr[0].toUpperCase());
                int duration = Integer.parseInt(potionArr[1]);
                int amplifier = Integer.parseInt(potionArr[2]);

                Validate.notNull(type, "Invalid potion effect type: " + potionArr[0]);

                PotionEffect effect = new PotionEffect(type, duration, amplifier);
                potionEffects.add(effect);
            }catch(IllegalArgumentException e){
                Bukkit.getLogger().warning("[UhcCore] "+potionStr+" ignored, please check the syntax. It must be formated like POTION_NAME/duration/amplifier");
            }
        }

        return potionEffects;
    }

}
