package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamInventoryCommandExecutor implements CommandExecutor{

    private ScenarioManager sm;

    public TeamInventoryCommandExecutor(){
        sm = GameManager.getGameManager().getScenarioManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        Player player = ((Player) sender).getPlayer();

        if (!sm.isActivated(Scenario.TEAMINVENTORY)){
            player.sendMessage(ChatColor.RED + Scenario.TEAMINVENTORY.getName() + " is currently disabled!");
            return true;
        }

        UhcPlayer uhcPlayer;

        try {
            uhcPlayer = GameManager.getGameManager().getPlayersManager().getUhcPlayer(player);
        }catch (UhcPlayerDoesntExistException ex){
            player.sendMessage(ChatColor.RED + "An internal error occurred!");
            return true;
        }

        if (args.length == 1 && player.hasPermission("scenarios.teaminventory.other")){
            try {
                uhcPlayer = GameManager.getGameManager().getPlayersManager().getUhcPlayer(args[0]);
            }catch (UhcPlayerDoesntExistException ex){
                player.sendMessage(ChatColor.RED + "That player cannot be found!");
                return true;
            }

            if (uhcPlayer.getState() != PlayerState.PLAYING){
                player.sendMessage(ChatColor.RED + "That player is currently not playing!");
                return true;
            }
        }

        if (uhcPlayer.getState() != PlayerState.PLAYING){
            player.sendMessage(ChatColor.RED + "You may only open your team's inventory while playing!");
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "Opening team inventory ...");
        player.openInventory(uhcPlayer.getTeam().getTeamInventory());
        return true;
    }

}