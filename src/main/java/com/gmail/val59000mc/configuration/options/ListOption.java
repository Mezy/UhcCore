package com.gmail.val59000mc.configuration.options;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class ListOption<T> implements Option<List<T>>{

    public enum Type {
        STRING_LIST,
        LONG_LIST,
        DOUBLE_LIST,
        INT_LIST
    }

    private final String path;
    private final Type type;

    public ListOption(String path, Type type){
        this.path = path;
        this.type = type;
    }

    @Override
    public List<T> getValue(YamlConfiguration config){
        switch (type){
            case INT_LIST:
                return (List<T>) config.getIntegerList(path);
            case STRING_LIST:
                return (List<T>) config.getStringList(path);
            case DOUBLE_LIST:
                return (List<T>) config.getDoubleList(path);
            case LONG_LIST:
                return (List<T>) config.getLongList(path);
            default:
                return (List<T>) config.get(path);
        }
    }

}
