package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.threads.TimeBeforeEndThread;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeathmatchCommandExecutor implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("start")){
            sender.sendMessage(ChatColor.RED + "Usage: /deathmatch start");
            return true;
        }

        GameManager gm = GameManager.getGameManager();

        // Check if in game.
        if (gm.getGameState() != GameState.PLAYING){
            sender.sendMessage(ChatColor.RED + "You may only use this command during the game.");
            return true;
        }

        // Start TimeBeforeEndThread if time limit was not enabled.
        if (gm.getRemainingTime() == 0 && !gm.getConfiguration().getEnableTimeLimit()){
            gm.setRemainingTime(10);
            Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new TimeBeforeEndThread());
            sender.sendMessage(ChatColor.GREEN + "Starting deathmatch!");
            return true;
        }

        // If deathmatch is about to start don't allow the command.
        if (gm.getRemainingTime() < 10){
            sender.sendMessage(ChatColor.RED + "Deathmatch is already starting!");
            return true;
        }

        gm.setRemainingTime(10);
        sender.sendMessage(ChatColor.GREEN + "Starting deathmatch!");
        return true;
    }

}