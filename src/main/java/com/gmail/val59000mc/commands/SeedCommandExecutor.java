package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.maploader.MapLoader;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SeedCommandExecutor implements CommandExecutor{

    private final MapLoader mapLoader;

    public SeedCommandExecutor(MapLoader mapLoader){
        this.mapLoader = mapLoader;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        World world = mapLoader.getUhcWorld(World.Environment.NORMAL);
        if (world == null){
            sender.sendMessage("Please wait for the game to fully load.");
            return true;
        }

        sender.sendMessage("["+world.getSeed()+"]");
        return true;
    }

}