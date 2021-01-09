package com.gmail.val59000mc.configuration.options;

import org.bukkit.configuration.file.YamlConfiguration;

public class Option<T> {

    public enum ListType {
        STRING_LIST,
        LONG_LIST,
        DOUBLE_LIST,
        INT_LIST
    }

    protected final String path;
    private final T def;
    private final ListType listType;

    public Option(String path, T def){
        this.path = path;
        this.def = def;
        this.listType = null;
    }

    public Option(String path, ListType listType){
        this.path = path;
        this.def = null;
        this.listType = listType;
    }

    protected Option(String path){
        this.path = path;
        this.def = null;
        this.listType = null;
    }

    public Option(String path, Option<T> defOption){
        this(path, defOption.def);
    }

    public T getValue(YamlConfiguration config){
        if (def instanceof String){
            return (T) config.getString(path, (String) def);
        }
        if (def instanceof Integer){
            return (T) (Integer) config.getInt(path, (Integer) def);
        }
        if (def instanceof Double){
            return (T) (Double) config.getDouble(path, (Double) def);
        }
        if (def instanceof Long){
            return (T) (Long) config.getLong(path, (Long) def);
        }
        if (def instanceof Boolean){
            return (T) (Boolean) config.getBoolean(path, (Boolean) def);
        }
        if (listType != null){
            switch (listType){
                case INT_LIST:
                    return (T) config.getIntegerList(path);
                case STRING_LIST:
                    return (T) config.getStringList(path);
                case DOUBLE_LIST:
                    return (T) config.getDoubleList(path);
                case LONG_LIST:
                    return (T) config.getLongList(path);
            }
        }

        return (T) config.get(path, def);
    }

}
