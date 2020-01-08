package com.gmail.val59000mc;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.utils.FileUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Updater extends Thread implements Listener{

    private static final long TWO_HOURS = 1000*60*60*2;
    private static final String VERSION_URL = "https://api.spiget.org/v2/resources/47572/versions/latest";
    private static final String DOWNLOAD_URL = "https://github.com/Mezy/UhcCore/releases/download/v{version}/UhcCore-{version}.jar";
    private Plugin plugin;
    private String currentVersion, newestVersion;

    public Updater(Plugin plugin){
        this.plugin = plugin;
        start();
    }

    @Override
    public void run(){
        try {
            File file = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
            long timeSinceModified = System.currentTimeMillis() - file.lastModified();
            if (timeSinceModified > TWO_HOURS){ // More than 2 hours ago (Time the API takes to update)
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

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e){
        if (!e.getMessage().equalsIgnoreCase("/uhccore update")){
            return;
        }
        e.setCancelled(true);

        Player player = e.getPlayer();
        GameManager gm = GameManager.getGameManager();

        if (gm.getGameState() == GameState.PLAYING || gm.getGameState() == GameState.DEATHMATCH){
            player.sendMessage(ChatColor.RED + "You can not update the plugin during games as it will restart your server.");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Updating plugin ...");

        try{
            updatePlugin();
        }catch (Exception ex){
            player.sendMessage(ChatColor.RED + "Failed to update plugin, check console for more info.");
            ex.printStackTrace();
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
            return; // Already on the newest version
        }

        // New version is available, register player join listener so we can notify admins.
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        sendUpdateMessage(Bukkit.getConsoleSender());
    }

    private void sendUpdateMessage(CommandSender receiver){
        receiver.sendMessage("");
        receiver.sendMessage(ChatColor.DARK_GREEN + "[UhcCore] " + ChatColor.GREEN + "A new version of the UhcCore plugin is available!");
        receiver.sendMessage(ChatColor.DARK_GREEN + "Current version: " + ChatColor.GREEN + currentVersion);
        receiver.sendMessage(ChatColor.DARK_GREEN + "New version: " + ChatColor.GREEN + newestVersion);
        receiver.sendMessage(ChatColor.DARK_GREEN + "To update use: " + ChatColor.GREEN + "/uhccore update");
        receiver.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "WARNING: " + ChatColor.RED + "This will restart your server!");
        receiver.sendMessage("");
    }

    private void updatePlugin() throws Exception{
        HttpsURLConnection connection = (HttpsURLConnection) new URL(DOWNLOAD_URL.replace("{version}", newestVersion)).openConnection();
        connection.connect();

        File newPluginFile = new File("plugins/UhcCore-" + newestVersion + ".jar");
        File oldPluginFile = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile());

        InputStream in = connection.getInputStream();
        FileOutputStream out = new FileOutputStream(newPluginFile);

        // Copy in to out
        int read;
        byte[] bytes = new byte[1024];

        while ((read = in.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }

        out.flush();
        out.close();
        in.close();
        connection.disconnect();

        Bukkit.getLogger().info("[UhcCore] New plugin version downloaded.");

        if (!newPluginFile.equals(oldPluginFile)){
            FileUtils.scheduleFileForDeletion(oldPluginFile);
            Bukkit.getLogger().info("[UhcCore] Old plugin version will be deleted on next startup.");
        }

        Bukkit.getLogger().info("[UhcCore] Restarting to finish plugin update.");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
    }

}