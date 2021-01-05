package com.gmail.val59000mc.configuration;

public class Option<T> {

    private final String path;
    private final T def;

    public Option(String path, T def){
        this.path = path;
        this.def = def;
    }

    public Option(String path, Option<T> def){
        this(path, def.getDef());
    }

    public String getPath() {
        return path;
    }

    public T getDef() {
        return def;
    }

}
