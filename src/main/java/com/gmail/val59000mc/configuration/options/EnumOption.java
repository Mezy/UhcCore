package com.gmail.val59000mc.configuration.options;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class EnumOption<T extends Enum<T>> extends Option<T> {

    private final Class<T> type;
    private final T def;
    private final String defString;

    public EnumOption(String path, T def) {
        super(path, def);
        type = (Class<T>) def.getClass();
        this.def = def;
        defString = def.name();
    }

    public EnumOption(String path, Class<T> type, String defString) {
        super(path);
        this.type = type;
        this.def = null;
        this.defString = defString;
    }

    @Override
    public T getValue(YamlConfiguration config) {
        String enumType = config.getString(path, defString);

        try {
            Validate.notNull(enumType, "No enum type specified!");
            return Enum.valueOf(type, enumType);
        }catch (IllegalArgumentException ex){
            Bukkit.getLogger().severe("[UhcCore] Invalid enum type " + enumType);
            ex.printStackTrace();
            return def;
        }
    }

}
