package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingsCommandExecutor implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can use the settings GUI");
            return true;
        }

        Player player = ((Player) sender).getPlayer();
        player.openInventory(GameManager.getGameManager().getConfiguration().getConfigGui().getGui());
        return true;
    }

}