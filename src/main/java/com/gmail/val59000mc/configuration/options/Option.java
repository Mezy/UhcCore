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
        Object obj = config.get(path, def);
        return (T) obj;
    }

}
