package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamInventoryCommandExecutor implements CommandExecutor{

    private final PlayersManager playersManager;
    private final ScenarioManager scenarioManager;

    public TeamInventoryCommandExecutor(PlayersManager playersManager, ScenarioManager scenarioManager){
        this.playersManager = playersManager;
        this.scenarioManager = scenarioManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        Player player = (Player) sender;

        if (!scenarioManager.isActivated(Scenario.TEAMINVENTORY)){
            player.sendMessage(Lang.SCENARIO_TEAMINVENTORY_DISABLED);
            return true;
        }

        UhcPlayer uhcPlayer = playersManager.getUhcPlayer(player);

        if (args.length == 1 && player.hasPermission("scenarios.teaminventory.other")){
            try {
                uhcPlayer = playersManager.getUhcPlayer(args[0]);
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
            player.sendMessage(Lang.SCENARIO_TEAMINVENTORY_ERROR);
            return true;
        }

        player.sendMessage(Lang.SCENARIO_TEAMINVENTORY_OPEN);
        player.openInventory(uhcPlayer.getTeam().getTeamInventory());
        return true;
    }

}