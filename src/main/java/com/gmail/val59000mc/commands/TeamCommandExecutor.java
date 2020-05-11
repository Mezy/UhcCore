package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommandExecutor implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        GameManager gm = GameManager.getGameManager();
        PlayersManager pm = gm.getPlayersManager();
        UhcPlayer uhcPlayer = pm.getUhcPlayer(player);

        if (args.length == 0){
            player.sendMessage("Send command help");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("invite")){
            if (!uhcPlayer.isTeamLeader()){
                player.sendMessage("Only leaders can use this command!");
                return true;
            }

            if (args.length != 2){
                player.sendMessage("Usage: /team invite <player>");
                return true;
            }

            Player invitePlayer = Bukkit.getPlayer(args[1]);

            if (invitePlayer == null){
                player.sendMessage("Player not found!");
                return true;
            }

            if (player.equals(invitePlayer)){
                player.sendMessage("Can't invite yourself!");
            }

            UhcPlayer uhcInvitePlayer = pm.getUhcPlayer(invitePlayer);
            uhcInvitePlayer.inviteToTeam(uhcPlayer.getTeam());
            return true;
        }

        player.sendMessage("Invalid sub command");
        return true;
    }

}