package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommandExecutor implements CommandExecutor {
    private final PlayerManager playerManager;

    public HealCommandExecutor(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (UhcPlayer uhcPlayer : this.playerManager.getOnlinePlayingPlayers()){
            try {
                Player bukkitPlayer = uhcPlayer.getPlayer();
                bukkitPlayer.setHealth(bukkitPlayer.getMaxHealth());
            }catch (UhcPlayerNotOnlineException ex){
                // no heal for offline players
            }
        }
        return true;
    }
}