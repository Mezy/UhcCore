package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommandExecutor implements CommandExecutor {
    private final PlayerManager playerManager;
    private final GameManager gameManager;

    public HealCommandExecutor(PlayerManager playerManager, GameManager gameManager) {
        this.playerManager = playerManager;
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("uhc-core.commands.heal")) {
            if (args.length == 0) { // heal all players
                for (UhcPlayer uhcPlayer : this.playerManager.getOnlinePlayingPlayers()) {
                    try {
                        Player bukkitPlayer = uhcPlayer.getPlayer();
                        bukkitPlayer.setHealth(bukkitPlayer.getMaxHealth());
                    } catch (UhcPlayerNotOnlineException ex) {
                        // no heal for offline players
                    }
                }
                this.gameManager.broadcastInfoMessage(Lang.GAME_FINAL_HEAL);
            } else if (args.length == 1) { // heal 1 player
                Player player = Bukkit.getPlayer(args[0]);
                if (player != null) { // player found
                    player.setHealth(player.getMaxHealth());
                    sender.sendMessage(args[0] + " has been healed.");
                } else { // player not found
                    sender.sendMessage(args[0] + " not found.");
                }
            }
        }
        return true;
    }
}