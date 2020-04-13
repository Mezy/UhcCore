package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.YamlFile;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileUtils{

    private static final String API_URL = "https://paste.md-5.net/documents";
    private static final String PASTE_URL_DOMAIN = "https://paste.md-5.net/";

    public static YamlFile saveResourceIfNotAvailable(String fileName) throws InvalidConfigurationException{
        return saveResourceIfNotAvailable(fileName, fileName, false);
    }

    public static YamlFile saveResourceIfNotAvailable(String fileName, String sourceName) throws InvalidConfigurationException{
        return saveResourceIfNotAvailable(fileName, sourceName, false);
    }

    public static YamlFile saveResourceIfNotAvailable(String fileName, boolean disableLogging) throws InvalidConfigurationException{
        return saveResourceIfNotAvailable(fileName, fileName, disableLogging);
    }

    public static YamlFile saveResourceIfNotAvailable(String fileName, String sourceName, boolean disableLogging) throws InvalidConfigurationException{
        File file = new File(UhcCore.getPlugin().getDataFolder(), fileName);

        if (!disableLogging) {
            Bukkit.getLogger().info("[UhcCore] Loading " + file.toString());
        }

        if (!file.exists()){
            // save resource
            UhcCore.getPlugin().saveResource(sourceName, false);
        }

        if (!fileName.equals(sourceName)){
            File sourceFile = new File(UhcCore.getPlugin().getDataFolder(), sourceName);
            sourceFile.renameTo(file);
        }

        if (!file.exists()){
            Bukkit.getLogger().severe("[UhcCore] Failed to save file: " + fileName);
        }

        YamlFile yamlFile = new YamlFile(file);
        try {
            yamlFile.load();
        }catch (IOException | InvalidConfigurationException ex){
            Bukkit.getLogger().severe("[UhcCore] Failed to load " + fileName + ", there might be an error in the yaml syntax.");
            if (ex instanceof InvalidConfigurationException){
                throw (InvalidConfigurationException) ex;
            }

            ex.printStackTrace();
            return null;
        }

        return yamlFile;
    }

    public static void removeScheduledDeletionFiles(){
        YamlFile storage;

        try{
            storage = FileUtils.saveResourceIfNotAvailable("storage.yml");
        }catch (InvalidConfigurationException ex){
            ex.printStackTrace();
            return;
        }

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
        YamlFile storage;

        try{
            storage = FileUtils.saveResourceIfNotAvailable("storage.yml");
        }catch (InvalidConfigurationException ex){
            ex.printStackTrace();
            return;
        }

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

    /**
     * Method used to upload text files to paste bin.
     * @param builder StringBuilder containing the text you want to be uploaded.
     * @return Returns the URL of the uploaded text.
     * @throws IOException Thrown when uploading fails.
     */
    public static String uploadTextFile(StringBuilder builder) throws IOException{
        String data = builder.toString();

        HttpsURLConnection connection = (HttpsURLConnection) new URL(API_URL).openConnection();

        // Add headers
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Content-Length", String.valueOf(data.length()));
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setRequestProperty("User-Agent", "UhcCore:"+ UhcCore.getPlugin().getDescription().getVersion());

        // Send data
        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.write(data.getBytes());
        outputStream.flush();
        outputStream.close();

        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        JsonObject json = new JsonParser().parse(bufferedReader.readLine()).getAsJsonObject();

        bufferedReader.close();
        connection.disconnect();

        return PASTE_URL_DOMAIN + json.get("key").getAsString();
    }

}