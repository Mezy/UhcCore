package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.exceptions.UhcTeamException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCommandExecutor implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        GameManager gm = GameManager.getGameManager();

        if (gm.getGameState() != GameState.WAITING){
            player.sendMessage(Lang.COMMAND_SPECTATE_ERROR);
            return true;
        }

        UhcPlayer uhcPlayer = gm.getPlayersManager().getUhcPlayer(player);

        if (uhcPlayer.getState() == PlayerState.DEAD){
            setPlayerPlaying(player, uhcPlayer);
            player.sendMessage(Lang.COMMAND_SPECTATE_PLAYING);
            return true;
        }

        setPlayerSpectating(player, uhcPlayer);
        player.sendMessage(Lang.COMMAND_SPECTATE_SPECTATING);
        return true;
    }

    private void setPlayerSpectating(Player player, UhcPlayer uhcPlayer){
        GameManager gm = GameManager.getGameManager();
        uhcPlayer.setState(PlayerState.DEAD);
        gm.getScoreboardManager().updatePlayerTab(uhcPlayer);

        // Clear lobby items
        player.getInventory().clear();

        if (!uhcPlayer.getTeam().isSolo()){
            try {
                uhcPlayer.getTeam().leave(uhcPlayer);
            }catch (UhcTeamException ex){
                ex.printStackTrace();
            }
        }
    }

    private void setPlayerPlaying(Player player, UhcPlayer uhcPlayer){
        GameManager gm = GameManager.getGameManager();
        uhcPlayer.setState(PlayerState.WAITING);
        gm.getScoreboardManager().updatePlayerTab(uhcPlayer);

        // Give lobby items back
        UhcItems.giveLobbyItemsTo(player);
    }

}