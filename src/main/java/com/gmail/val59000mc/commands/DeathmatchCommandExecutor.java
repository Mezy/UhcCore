package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.game.handlers.DeathmatchHandler;
import com.gmail.val59000mc.threads.TimeBeforeDeathmatchThread;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeathmatchCommandExecutor implements CommandExecutor{

    private final DeathmatchHandler deathmatchHandler;
    private final GameManager gameManager;

    public DeathmatchCommandExecutor(GameManager gameManager, DeathmatchHandler deathmatchHandler){
        this.gameManager = gameManager;
        this.deathmatchHandler = deathmatchHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("start")){
            sender.sendMessage(ChatColor.RED + "Usage: /deathmatch start");
            return true;
        }

        // Check if in game.
        if (gameManager.getGameState() != GameState.PLAYING){
            sender.sendMessage(ChatColor.RED + "You may only use this command during the game.");
            return true;
        }

        // Start TimeBeforeEndThread if time limit was not enabled.
        if (gameManager.getRemainingTime() == 0 && !gameManager.getConfig().get(MainConfig.ENABLE_DEATHMATCH)){
            gameManager.setRemainingTime(10);
            Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new TimeBeforeDeathmatchThread(gameManager, deathmatchHandler));
            sender.sendMessage(ChatColor.GREEN + "Starting deathmatch!");
            return true;
        }

        // If deathmatch is about to start don't allow the command.
        if (gameManager.getRemainingTime() < 10){
            sender.sendMessage(ChatColor.RED + "Deathmatch is already starting!");
            return true;
        }

        gameManager.setRemainingTime(10);
        sender.sendMessage(ChatColor.GREEN + "Starting deathmatch!");
        return true;
    }

}