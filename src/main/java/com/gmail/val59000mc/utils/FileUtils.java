package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.YamlFile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils{

    public static YamlFile saveResourceIfNotAvailable(String fileName){
        return saveResourceIfNotAvailable(fileName, fileName, false);
    }

    public static YamlFile saveResourceIfNotAvailable(String fileName, String sourceName){
        return saveResourceIfNotAvailable(fileName, sourceName, false);
    }

    public static YamlFile saveResourceIfNotAvailable(String fileName, boolean disableLogging){
        return saveResourceIfNotAvailable(fileName, fileName, disableLogging);
    }

    public static YamlFile saveResourceIfNotAvailable(String fileName, String sourceName, boolean disableLogging){
        File file = new File(UhcCore.getPlugin().getDataFolder() + "/" + fileName);

        if (!disableLogging) {
            Bukkit.getLogger().info("[UhcCore] Loading " + file.toString());
        }

        if (!file.exists()){
            // save resource
            UhcCore.getPlugin().saveResource(sourceName, false);
        }

        if (!fileName.equals(sourceName)){
            File sourceFile = new File(UhcCore.getPlugin().getDataFolder() + "/" + sourceName);
            sourceFile.renameTo(file);
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

    public static void removeScheduledDeletionFiles(){
        YamlFile storage = FileUtils.saveResourceIfNotAvailable("storage.yml");
        List<String> deleteFiles = storage.getStringList("delete");
        List<String> notDeleted = new ArrayList<>();
        if (deleteFiles.isEmpty()){
            return;
        }

        for (String path : deleteFiles){
            Bukkit.getLogger().info("[UhcCore] Deleting file: " + path);
            File file = new File(path);
            if (!file.delete()){
                Bukkit.getLogger().warning("[UhcCore] Failed to delete file: " + path);
                notDeleted.add(path);
            }
        }

        storage.set("delete", notDeleted);

        try{
            storage.save();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public static void scheduleFileForDeletion(File file){
        // Clear file
        try{
            FileOutputStream out = new FileOutputStream(file);
            out.flush();
            out.close();
        }catch (IOException ex){
            // Failed to clear file
            ex.printStackTrace();
        }

        // Add to "delete" in storage.yml
        YamlFile storage = FileUtils.saveResourceIfNotAvailable("storage.yml");
        List<String> deleteFiles = storage.getStringList("delete");
        deleteFiles.add(file.getPath());
        storage.set("delete", deleteFiles);

        try{
            storage.save();
        }catch (IOException ex){
            // Failed to save storage.yml
            ex.printStackTrace();
        }
    }

}