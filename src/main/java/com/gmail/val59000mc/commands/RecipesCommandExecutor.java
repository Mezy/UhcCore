package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.customitems.CraftsManager;
import com.gmail.val59000mc.languages.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RecipesCommandExecutor implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = ((Player) sender).getPlayer();

        if (!CraftsManager.isAtLeastOneCraft()){
            // no crafts
            player.sendMessage(Lang.COMMAND_RECIPES_ERROR);
            return true;
        }
        
        CraftsManager.openCraftBookInventory(player);
        return true;
    }

}