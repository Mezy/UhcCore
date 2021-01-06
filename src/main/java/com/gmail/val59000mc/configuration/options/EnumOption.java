package com.gmail.val59000mc.configuration.options;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class EnumOption<T extends Enum<T>> extends Option<T> {

    private final T def;

    public EnumOption(String path, T def) {
        super(path, def);
        this.def = def;
    }

    @Override
    public T getValue(YamlConfiguration config) {
        String enumDef = config.getString(path, def.name());

        try {
            return (T) Enum.valueOf(def.getClass(), enumDef);
        }catch (IllegalArgumentException ex){
            Bukkit.getLogger().severe("[UhcCore] Invalid enum type " + enumDef);
            ex.printStackTrace();
            return def;
        }
    }

}
