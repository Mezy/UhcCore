package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.ParseException;
import com.gmail.val59000mc.utils.FileUtils;
import com.gmail.val59000mc.utils.JsonItemStack;
import com.gmail.val59000mc.utils.JsonItemUtils;
import com.gmail.val59000mc.utils.SpigotUtils;
import io.papermc.lib.PaperLib;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Map;

public class ItemInfoCommandExecutor implements CommandExecutor{

    private static final boolean DEBUG = false;

    @Override @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = ((Player) sender).getPlayer();
        ItemStack item = player.getItemInHand();

        if (DEBUG && args.length != 0){
            try {
                item = JsonItemUtils.getItemFromJson(args[0]);
                player.getInventory().addItem(item);
            }catch (ParseException ex){
                player.sendMessage(ex.getMessage());
            }
            return true;
        }

        if (item.getType() == Material.AIR){
            player.sendMessage(ChatColor.RED + "Please hold a item first!");
            return true;
        }

        if (args.length == 2){
            int min, max;
            try {
                min = Integer.parseInt(args[0]);
                max = Integer.parseInt(args[1]);
            }catch (IllegalArgumentException ex){
                player.sendMessage(ChatColor.RED + "Usage: /iteminfo [minimum] [maximum]");
                return true;
            }

            JsonItemStack jsonItem = new JsonItemStack(item);
            jsonItem.setMinimum(min);
            try {
                jsonItem.setMaximum(max);
            }catch (IllegalArgumentException ex){
                player.sendMessage(ChatColor.RED + ex.getMessage());
                return true;
            }
            item = jsonItem;
        }

        player.sendMessage(ChatColor.DARK_GREEN + "Item Info:");
        player.sendMessage(ChatColor.DARK_GREEN + " Material: " + ChatColor.GREEN + item.getType());
        player.sendMessage(ChatColor.DARK_GREEN + " Data/Damage value: " + ChatColor.GREEN + item.getDurability());
        sendJsonItemMessage(player, item);

        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()){
            player.sendMessage(ChatColor.DARK_GREEN + " Enchantments:");
            Map<Enchantment, Integer> enchantments = item.getItemMeta().getEnchants();
            for (Enchantment enchantment : enchantments.keySet()){
                player.sendMessage("  " + ChatColor.DARK_GREEN + enchantment.getName() + ChatColor.GREEN + " (level " + enchantments.get(enchantment) + ")");
            }
        }
        return true;
    }

    private void sendJsonItemMessage(Player player, ItemStack item){
        String json = JsonItemUtils.getItemJson(item);

        if (json.length() > 100){
            player.sendMessage(ChatColor.GREEN + "Item Json is too big for chat, uploading to paste bin ...");

            String url;
            try{
                url = FileUtils.uploadTextFile(new StringBuilder(json));
            }catch (IOException ex){
                player.sendMessage(ChatColor.RED + "Failed to upload item json to paste bin, check console for more detail.");
                ex.printStackTrace();
                return;
            }

            player.sendMessage(ChatColor.DARK_GREEN + " Json-Item: " + ChatColor.GREEN + url);
            return;
        }

        String text = ChatColor.DARK_GREEN + " Json-Item: " + ChatColor.RESET + json;
        if (PaperLib.isSpigot()){
            SpigotUtils.sendMessage(player, text, ChatColor.GREEN + "Click to copy", json, SpigotUtils.Action.SUGGEST);
        }else{
            player.sendMessage(text);
        }
    }

}