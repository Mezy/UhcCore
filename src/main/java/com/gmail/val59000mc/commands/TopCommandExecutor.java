package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopCommandExecutor implements CommandExecutor{

    private final PlayersManager playersManager;

    public TopCommandExecutor(PlayersManager playersManager){
        this.playersManager = playersManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        UhcPlayer uhcPlayer = playersManager.getUhcPlayer(player);

        if (uhcPlayer.getState() != PlayerState.PLAYING){
            player.sendMessage(Lang.COMMAND_TOP_ERROR_PLAYING);
            return true;
        }

        if (player.getWorld().getEnvironment() == World.Environment.NETHER){
            player.sendMessage(Lang.COMMAND_TOP_ERROR_NETHER);
            return true;
        }

        Block highest = player.getWorld().getHighestBlockAt(player.getLocation());

        player.teleport(highest.getLocation().add(.5, 0, .5));
        player.sendMessage(Lang.COMMAND_TOP_TELEPORT);
        return true;
    }

}