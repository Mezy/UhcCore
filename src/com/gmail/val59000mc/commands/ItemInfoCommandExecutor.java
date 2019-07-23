package com.gmail.val59000mc.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemInfoCommandExecutor implements CommandExecutor{

    @Override @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = ((Player) sender).getPlayer();
        ItemStack item = player.getItemInHand();

        if (item.getType() == Material.AIR){
            player.sendMessage(ChatColor.RED + "Please hold a item first!");
            return true;
        }

        player.sendMessage(ChatColor.DARK_GREEN + "Item Info:");
        player.sendMessage(ChatColor.DARK_GREEN + " Material: " + ChatColor.GREEN + item.getType());
        player.sendMessage(ChatColor.DARK_GREEN + " Data/Damage value: " + ChatColor.GREEN + item.getDurability());

        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()){
            player.sendMessage(ChatColor.DARK_GREEN + " Enchantments:");
            Map<Enchantment, Integer> enchantments = item.getItemMeta().getEnchants();
            for (Enchantment enchantment : enchantments.keySet()){
                player.sendMessage("  " + ChatColor.DARK_GREEN + enchantment.getName() + ChatColor.GREEN + " (level " + enchantments.get(enchantment) + ")");
            }
        }
        return true;
    }

}