package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.threads.PreStartThread;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartCommandExecutor implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "[UhcCore] Force starting has toggled!");
        PreStartThread.toggleForce();
        return true;
    }

}