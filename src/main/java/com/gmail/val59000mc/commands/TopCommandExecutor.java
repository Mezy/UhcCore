package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.languages.Lang;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopCommandExecutor implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        Block highest = player.getWorld().getHighestBlockAt(player.getLocation());

        player.teleport(highest.getLocation().add(.5, 0, .5));
        player.sendMessage(Lang.COMMAND_TOP_TELEPORT);
        return true;
    }

}