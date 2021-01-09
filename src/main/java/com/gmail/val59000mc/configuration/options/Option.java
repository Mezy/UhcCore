package com.gmail.val59000mc.configuration.options;

import org.bukkit.configuration.file.YamlConfiguration;

public interface Option<T> {
    T getValue(YamlConfiguration config);
}
