package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import org.bukkit.Bukkit;

import java.io.File;

public class FileUtils{

    public static File saveResourceIfNotAvailable(String fileName){
        File file = new File(UhcCore.getPlugin().getDataFolder() + "/" + fileName);

        Bukkit.getLogger().info("[UhcCore] Loading " + file.toString());

        if (!file.exists()){
            // save resource
            UhcCore.getPlugin().saveResource(fileName, false);
        }

        if (!file.exists()){
            Bukkit.getLogger().severe("[UhcCore] Failed to save file: " + fileName);
        }

        return file;
    }

}