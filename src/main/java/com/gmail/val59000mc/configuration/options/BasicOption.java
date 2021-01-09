package com.gmail.val59000mc.configuration.options;

import org.bukkit.configuration.file.YamlConfiguration;

public class BasicOption<T> implements Option<T>{

    private final String path;
    private final T def;

    public BasicOption(String path, T def){
        this.path = path;
        this.def = def;
    }

    public BasicOption(String path, BasicOption<T> defOption){
        this(path, defOption.def);
    }

    @Override
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

        return (T) config.get(path, def);
    }

}
