package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SeedCommandExecutor implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        World world = Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getOverworldUuid());
        if (world == null){
            sender.sendMessage("Please wait for the game to fully load.");
            return true;
        }

        sender.sendMessage("["+world.getSeed()+"]");
        return true;
    }

}