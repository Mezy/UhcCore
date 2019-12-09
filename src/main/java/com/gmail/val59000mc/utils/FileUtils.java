package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.YamlFile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;

public class FileUtils{

    public static YamlFile saveResourceIfNotAvailable(String fileName){
        return saveResourceIfNotAvailable(fileName, false);
    }

    public static YamlFile saveResourceIfNotAvailable(String fileName, boolean disableLogging){
        File file = new File(UhcCore.getPlugin().getDataFolder() + "/" + fileName);

        if (!disableLogging) {
            Bukkit.getLogger().info("[UhcCore] Loading " + file.toString());
        }

        if (!file.exists()){
            // save resource
            UhcCore.getPlugin().saveResource(fileName, false);
        }

        if (!file.exists()){
            Bukkit.getLogger().severe("[UhcCore] Failed to save file: " + fileName);
        }

        YamlFile yamlFile = new YamlFile(file);
        try {
            yamlFile.load();
        }catch (IOException | InvalidConfigurationException ex){
            ex.printStackTrace();
        }

        return yamlFile;
    }

}