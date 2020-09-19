package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
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

    private final GameManager gameManager;

    public ReviveCommandExecutor(GameManager gameManager){
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length != 1 && args.length != 2){
            sender.sendMessage(ChatColor.RED + "Correct usage: '/revive <player>' or use '/revive <player> clear' to respawn the player without giving their items back.");
            return true;
        }

        if (gameManager.getGameState() != GameState.PLAYING){
            sender.sendMessage(ChatColor.RED + "You can only use this command while playing!");
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "Loading player data ...");

        String name = args[0];
        Player player = Bukkit.getPlayer(name);
        boolean spawnWithItems = args.length != 2 || !args[1].equalsIgnoreCase("clear");

        if (player != null){
            uuidCallback(player.getUniqueId(), player.getName(), spawnWithItems, sender);
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), () -> uuidCallback(MojangUtils.getPlayerUuid(name), MojangUtils.getPlayerName(name), spawnWithItems, sender));

        return true;
    }

    private void uuidCallback(UUID uuid, String name, boolean spawnWithItems, CommandSender caller){
        if (uuid == null){
            caller.sendMessage(ChatColor.RED + "Player not found!");
        }

        PlayersManager pm = gameManager.getPlayersManager();

        UhcPlayer uhcPlayer = pm.revivePlayer(uuid, name, spawnWithItems);

        if (uhcPlayer.isOnline()){
            caller.sendMessage(ChatColor.GREEN + name + " has been revived!");
        }else{
            caller.sendMessage(ChatColor.GREEN + name + " can now join the game!");
        }
    }

}