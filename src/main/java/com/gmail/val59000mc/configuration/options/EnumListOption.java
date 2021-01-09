package com.gmail.val59000mc.configuration.options;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class EnumListOption<T extends Enum<T>> implements Option<List<T>> {

    private final String path;
    private final Class<T> type;

    public EnumListOption(String path, Class<T> type) {
        this.path = path;
        this.type = type;
    }

    @Override
    public List<T> getValue(YamlConfiguration config) {
        List<String> stringList = config.getStringList(path);
        List<T> enumList = new ArrayList<>();

        for (String s : stringList){
            try {
                enumList.add(Enum.valueOf(type, s));
            }catch (IllegalArgumentException ex){
                Bukkit.getLogger().severe("[UhcCore] Invalid enum type " + s);
                ex.printStackTrace();
            }
        }

        return enumList;
    }

}
