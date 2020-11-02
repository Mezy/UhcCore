package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class GsonFileUtils {

    private static final Gson GSON = new Gson();

    public static JsonObject saveResourceIfNotAvailable(String fileName) throws InvalidConfigurationException {
        return saveResourceIfNotAvailable(fileName, fileName);
    }

    public static JsonObject saveResourceIfNotAvailable(String fileName, String resourceName) throws InvalidConfigurationException {
        return saveResourceIfNotAvailable(fileName, false);
    }

    public static JsonObject saveResourceIfNotAvailable(String fileName, boolean disableLogging) throws InvalidConfigurationException {
        return saveResourceIfNotAvailable(fileName, fileName, disableLogging);
    }

    public static JsonObject saveResourceIfNotAvailable(String fileName, String sourceName, boolean disableLogging) throws InvalidConfigurationException {
        Path configDirectoryPath = UhcCore.getPlugin().getDataFolder().toPath();
        Path configFilePath = configDirectoryPath.resolve(fileName);

        if (!disableLogging) {
            Bukkit.getLogger().info("[UhcCore] Loading" + configFilePath.toString());
        }

        if (Files.notExists(configFilePath)) {
            UhcCore.getPlugin().saveResource(sourceName, false);
        }

        if (!Objects.equals(fileName, sourceName)) {
            Path sourceFilePath = configDirectoryPath.resolve(sourceName);
            try {
                Files.move(sourceFilePath, configFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (Files.notExists(configFilePath)) {
            Bukkit.getLogger().severe("[UhcCore] Failed to save file: " + configFilePath);
        }

        try (FileReader jsonReader = new FileReader(configFilePath.toFile())) {
            JsonElement element = new JsonParser().parse(jsonReader);
            return element.getAsJsonObject();
        }
        catch (IOException e) {
            Bukkit.getLogger().severe("[UhcCore] Failed to read file: " + configFilePath);
            throw new InvalidConfigurationException(e);
        }
        catch (JsonParseException | IllegalStateException e) {
            Bukkit.getLogger().severe("[UhcCore] Failed to parse json from file: " + configFilePath);
            throw new InvalidConfigurationException(e);
        }
    }

}
