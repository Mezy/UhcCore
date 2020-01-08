package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.UhcCore;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class UploadCommandExecutor implements CommandExecutor{

    private enum FileType{
        LOG("logs/latest.log"),
        CONFIG("plugins/UhcCore/config.yml"),
        LANG("plugins/UhcCore/lang.yml"),
        SCOREBOARD("plugins/UhcCore/scoreboard.yml"),
        FLOWERPOWER("plugins/UhcCore/flowerpower.yml");

        private String path;

        FileType(String path){
            this.path = path;
        }

        public File getFile(){
            return new File(path);
        }
    }

    private static final String API_URL = "https://paste.md-5.net/documents";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (args.length != 1){
            sender.sendMessage(ChatColor.RED + "Usage: /upload <file-type>");
            return true;
        }

        FileType fileType;

        try {
            fileType = FileType.valueOf(args[0].toUpperCase());
        }catch (IllegalArgumentException ex){
            sender.sendMessage(ChatColor.RED + "Invalid file type! Choose from this list:");
            for (FileType type : FileType.values()){
                sender.sendMessage(ChatColor.GREEN + " - " + type.name().toLowerCase());
            }
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "Uploading " + fileType.getFile() + " ...");

        String key;
        try {
            key = uploadFile(fileType);
        }catch (IOException ex){
            sender.sendMessage(ChatColor.RED + "Failed to upload file, check console for more details!");
            ex.printStackTrace();
            return true;
        }

        sender.sendMessage(ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "URL: " + ChatColor.GREEN + "https://paste.md-5.net/" + key);
        return true;
    }

    private String uploadFile(FileType fileType) throws IOException{
        File file = fileType.getFile();

        Bukkit.getLogger().info("[UhcCore] Uploading file: " + file);

        BufferedReader reader = new BufferedReader(new FileReader(file));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null){
            sb.append(line);
            sb.append('\n');
        }

        reader.close();

        String data = sb.toString();

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
        outputStream.writeUTF(data);
        outputStream.flush();
        outputStream.close();

        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        JsonObject json = new JsonParser().parse(bufferedReader.readLine()).getAsJsonObject();

        bufferedReader.close();
        connection.disconnect();

        Bukkit.getLogger().info("[UhcCore] Successfully uploaded file: " + file);
        return json.get("key").getAsString();
    }

}