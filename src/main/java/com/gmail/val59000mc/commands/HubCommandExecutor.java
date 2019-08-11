package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HubCommandExecutor implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        GameManager gm = GameManager.getGameManager();
        Player player = ((Player) sender).getPlayer();

        if (gm.getConfiguration().getEnableBungeeSupport()){
            gm.getPlayersManager().sendPlayerToBungeeServer(player);
            return true;
        }

        player.sendMessage(Lang.PLAYERS_SEND_BUNGEE_DISABLED);
        return true;
    }

}