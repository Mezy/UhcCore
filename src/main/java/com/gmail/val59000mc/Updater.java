package com.gmail.val59000mc;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.utils.FileUtils;
import com.gmail.val59000mc.utils.TimeUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

public class Updater extends Thread implements Listener{

    private static final String LATEST_RELEASE = "https://api.github.com/repos/Mezy/UhcCore/releases/latest";
    private final Plugin plugin;
    private Version currentVersion, newestVersion;
    private boolean hasPendingUpdate;
    private String jarDownloadUrl;

    public Updater(Plugin plugin){
        this.plugin = plugin;
        hasPendingUpdate = false;
        start();
    }

    @Override
    public void run(){
        while (!hasPendingUpdate && plugin.isEnabled()){
            try{
                runVersionCheck();
                sleep(false);
            }catch (Exception ex){
                Bukkit.getLogger().severe("[UhcCore] Failed to check for updates!");
                ex.printStackTrace();
                sleep(true);
            }
        }
    }

    private void sleep(boolean failedLastCheck){
        if (hasPendingUpdate){
            return;
        }

        long time = (failedLastCheck?5:30) * TimeUtils.MINUTE;

        try{
            sleep(time);
        }catch (InterruptedException ex){
            Bukkit.getLogger().severe("[UhcCore] Update thread stopped!");
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
            updatePlugin(true);
        }catch (Exception ex){
            player.sendMessage(ChatColor.RED + "Failed to update plugin, check console for more info.");
            ex.printStackTrace();
        }
    }

    private void runVersionCheck() throws Exception{
        HttpsURLConnection connection = (HttpsURLConnection) new URL(LATEST_RELEASE).openConnection();

        // Add headers
        connection.setRequestMethod("GET");
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("User-Agent", "UhcCore:"+ UhcCore.getPlugin().getDescription().getVersion());

        connection.connect();

        JsonParser jp = new JsonParser();
        JsonObject root = jp.parse(new InputStreamReader(connection.getInputStream())).getAsJsonObject();

        newestVersion = new Version(root.get("tag_name").getAsString());
        currentVersion = new Version(plugin.getDescription().getVersion());

        if (!newestVersion.isNewerThan(currentVersion)){
            return; // Already on the newest or newer version
        }

        hasPendingUpdate = true;

        for (JsonElement jsonElement : root.get("assets").getAsJsonArray()) {
            JsonObject asset = jsonElement.getAsJsonObject();

            if (asset.get("name").getAsString().endsWith(".jar")){
                jarDownloadUrl = asset.get("browser_download_url").getAsString();
                break;
            }
        }

        if (jarDownloadUrl == null){
            Bukkit.getLogger().severe("Jar download URL not found!");
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

    private void updatePlugin(boolean restart) throws Exception{
        HttpsURLConnection connection = (HttpsURLConnection) new URL(jarDownloadUrl).openConnection();
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

        if (restart) {
            Bukkit.getLogger().info("[UhcCore] Restarting to finish plugin update.");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
        }
    }

    public void runAutoUpdate(){
        // Auto update is disabled.
        if (!GameManager.getGameManager().getConfiguration().getEnableAutoUpdate()){
            return;
        }

        // No pending update.
        if (!hasPendingUpdate){
            return;
        }

        Bukkit.getLogger().info("[UhcCore] Running auto update.");
        try{
            updatePlugin(false);
        }catch (Exception ex){
            Bukkit.getLogger().warning("[UhcCore] Failed to update plugin!");
            ex.printStackTrace();
        }
    }

    private static class Version{

        private final String version;
        private final int[] versionNums;

        private Version(String version){
            if (version.startsWith("v")){
                version = version.substring(1);
            }

            if (version.contains(" ")){
                version = version.split(" ")[0];
            }

            this.version = version;

            String[] stringNums = version.split("\\.");
            versionNums = new int[stringNums.length];

            for (int i = 0; i < stringNums.length; i++){
                try{
                    versionNums[i] = Integer.parseInt(stringNums[i]);
                }catch (IllegalArgumentException ex){
                    Bukkit.getLogger().severe("Failed to parse plugin version: " + version);
                    ex.printStackTrace();
                }
            }
        }

        public boolean equals(Version version){
            return this.version.equals(version.version);
        }

        @Override
        public String toString(){
            return version;
        }

        private int getVersionNumber(int index){
            if (versionNums.length > index){
                return versionNums[index];
            }

            // This version doesn't have that many version numbers, so return 0 by default.
            return 0;
        }

        private boolean isNewerThan(Version version){
            if (equals(version)){
                return false;
            }

            int numCount = versionNums.length;
            if (version.versionNums.length > numCount){
                numCount = version.versionNums.length;
            }

            for (int i = 0; i < numCount; i++){
                // This version is smaller than arg version so this is old
                if (getVersionNumber(i) < version.getVersionNumber(i)){
                    return false;
                }
                // This version is bigger than arg version so this is new
                if (getVersionNumber(i) > version.getVersionNumber(i)){
                    return true;
                }

                // If neither of those are true both numbers are equal and a later number in the line will define age
            }

            // Versions are equal
            return false;
        }
    }

}