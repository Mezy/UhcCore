package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommandExecutor implements CommandExecutor{

    private final GameManager gameManager;

    public TeamCommandExecutor(GameManager gameManager){
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        PlayersManager pm = gameManager.getPlayersManager();
        UhcPlayer uhcPlayer = pm.getUhcPlayer(player);

        if (args.length == 0){
            player.sendMessage("Send command help");
            return true;
        }

        // Don't allow the creation of teams during the game.
        if (gameManager.getGameState() != GameState.WAITING){
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("invite")){
            if (!uhcPlayer.isTeamLeader()){
                player.sendMessage(Lang.TEAM_MESSAGE_NOT_LEADER);
                return true;
            }

            if (args.length != 2){
                player.sendMessage("Usage: /team invite <player>");
                return true;
            }

            Player invitePlayer = Bukkit.getPlayer(args[1]);

            if (invitePlayer == null){
                player.sendMessage(Lang.TEAM_MESSAGE_PLAYER_NOT_ONLINE.replace("%player%", args[1]));
                return true;
            }

            UhcPlayer uhcInvitePlayer = pm.getUhcPlayer(invitePlayer);

            if (uhcPlayer.getTeam().contains(uhcInvitePlayer)){
                player.sendMessage(Lang.TEAM_MESSAGE_ALREADY_IN_TEAM);
                return true;
            }

            if (uhcInvitePlayer.getTeamInvites().contains(uhcPlayer.getTeam())){
                uhcPlayer.sendMessage(Lang.TEAM_MESSAGE_INVITE_ALREADY_SENT);
                return true;
            }

            uhcInvitePlayer.inviteToTeam(uhcPlayer.getTeam());
            return true;
        }

        if (subCommand.equals("invite-reply")){
            if (args.length != 2){
                player.sendMessage("Usage: /team invite-reply <player>");
                return true;
            }

            UhcPlayer teamLeader;

            try{
                teamLeader = pm.getUhcPlayer(args[1]);
            }catch (UhcPlayerDoesntExistException ex){
                player.sendMessage(Lang.TEAM_MESSAGE_PLAYER_NOT_ONLINE.replace("%player%", args[1]));
                return true;
            }

            UhcTeam team = teamLeader.getTeam();

            if (!uhcPlayer.getTeamInvites().contains(team)){
                uhcPlayer.sendMessage(ChatColor.RED + "No invite from that team!");
                return true;
            }

            UhcItems.openTeamReplyInviteInventory(player, team);
            return true;
        }

        player.sendMessage("Invalid sub command");
        return true;
    }

}