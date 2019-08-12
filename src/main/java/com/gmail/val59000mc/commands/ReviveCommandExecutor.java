package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.utils.MojangUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ReviveCommandExecutor implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length != 1){
            sender.sendMessage(ChatColor.RED + "Correct usage: /revive <player>");
            return true;
        }

        if (GameManager.getGameManager().getGameState() != GameState.PLAYING){
            sender.sendMessage(ChatColor.RED + "You can only use this command while playing!");
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "Loading player data ...");

        String name = args[0];
        Player player = Bukkit.getPlayer(name);

        if (player != null){
            uuidCallback(player.getUniqueId(), player.getName(), sender);
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new Runnable() {
            @Override
            public void run() {
                uuidCallback(MojangUtils.getPlayerUuid(name), MojangUtils.getPlayerName(name), sender);
            }
        });

        return true;
    }

    private void uuidCallback(UUID uuid, String name, CommandSender caller){
        if (uuid == null){
            caller.sendMessage(ChatColor.RED + "Player not found!");
        }

        GameManager gm = GameManager.getGameManager();
        PlayersManager pm = gm.getPlayersManager();

        UhcPlayer uhcPlayer;

        try {
            uhcPlayer = pm.getUhcPlayer(uuid);
            uhcPlayer.setHasBeenTeleportedToLocation(false);
            // teleport player
        }catch (UhcPlayerDoesntExistException ex){
            uhcPlayer = pm.newUhcPlayer(uuid, name);
        }

        uhcPlayer.setState(PlayerState.PLAYING);

        try {
            pm.playerJoinsTheGame(uhcPlayer.getPlayer());
            caller.sendMessage(ChatColor.GREEN + name + " has been revived!");
        }catch (UhcPlayerNotOnlineException ex){
            caller.sendMessage(ChatColor.GREEN + name + " can now join the game!");
        }
    }

}