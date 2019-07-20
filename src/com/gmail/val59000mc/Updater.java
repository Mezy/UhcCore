package com.gmail.val59000mc;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Updater extends Thread implements Listener{

    private static final String VERSION_URL = "https://api.spiget.org/v2/resources/47572/versions/latest";
    private Plugin plugin;
    private String currentVersion, newestVersion;

    public Updater(Plugin plugin){
        this.plugin = plugin;
        start();
    }

    @Override
    public void run() {
        try {
            File file = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
            long timeSinceModified = System.currentTimeMillis() - file.lastModified();
            if (timeSinceModified > 1000*60*60*2) { // more than 2 hours ago (time the api takes to update.)
                runVersionCheck();
            }
        }catch (Exception ex){
            Bukkit.getLogger().severe("[UhcCore] Failed to check for updates!");
            ex.printStackTrace();
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();

        if (player.isOp()) {
            sendUpdateMessage(player);
        }
    }

    private void runVersionCheck() throws Exception{
        URL url = new URL(VERSION_URL);
        URLConnection request = url.openConnection();
        request.connect();

        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader(request.getInputStream()));

        newestVersion = root.getAsJsonObject().get("name").getAsString();
        currentVersion = plugin.getDescription().getVersion();

        if (newestVersion.equals(currentVersion)){
            return; // already on the newest version
        }

        // new version is avalible, register player join listener so we can notify admins
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        sendUpdateMessage(Bukkit.getConsoleSender());
    }

    private void sendUpdateMessage(CommandSender receiver){
        receiver.sendMessage("");
        receiver.sendMessage(ChatColor.DARK_GREEN + "[UhcCore] " + ChatColor.GREEN + "A new version of the UhcCore plugin is available!");
        receiver.sendMessage(ChatColor.DARK_GREEN + "Current version: " + ChatColor.GREEN + currentVersion);
        receiver.sendMessage(ChatColor.DARK_GREEN + "New version: " + ChatColor.GREEN + newestVersion);
        receiver.sendMessage(ChatColor.DARK_GREEN + "To download the new version click here: " + ChatColor.GREEN + "https://www.spigotmc.org/resources/uhccore-automated-uhc-for-minecraft-1-8-1-14.47572/updates");
        receiver.sendMessage("");
    }

}