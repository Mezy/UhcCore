package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
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
        for (UhcPlayer uhcPlayer : this.playerManager.getOnlinePlayingPlayers()){
            try {
                Player bukkitPlayer = uhcPlayer.getPlayer();
                bukkitPlayer.setHealth(bukkitPlayer.getMaxHealth());
            }catch (UhcPlayerNotOnlineException ex){
                // no heal for offline players
            }
        }
        this.gameManager.broadcastInfoMessage(Lang.GAME_FINAL_HEAL);
        return true;
    }
}