package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HubCommandExecutor implements CommandExecutor{

    private final GameManager gameManager;

    public HubCommandExecutor(GameManager gameManager){
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = ((Player) sender).getPlayer();

        if (gameManager.getConfiguration().getEnableBungeeSupport()){
            gameManager.getPlayersManager().sendPlayerToBungeeServer(player);
            return true;
        }

        player.sendMessage(Lang.PLAYERS_SEND_BUNGEE_DISABLED);
        return true;
    }

}