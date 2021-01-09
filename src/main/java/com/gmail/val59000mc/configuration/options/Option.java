package com.gmail.val59000mc.configuration.options;

import org.bukkit.configuration.file.YamlConfiguration;

public class Option<T> {

    protected final String path;
    private final T def;

    public Option(String path, T def){
        this.path = path;
        this.def = def;
    }

    public Option(String path){
        this.path = path;
        this.def = null;
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

        return (T) config.get(path, def);
    }

}
