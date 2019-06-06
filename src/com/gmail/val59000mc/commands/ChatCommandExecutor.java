package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommandExecutor implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if(sender instanceof Player){
			Player player = (Player) sender;
			GameManager gm = GameManager.getGameManager();
			UhcPlayer uhcPlayer;
			try {
				uhcPlayer = gm.getPlayersManager().getUhcPlayer(player);
				if(uhcPlayer != null && uhcPlayer.getState().equals(PlayerState.PLAYING) && gm.getGameState().equals(GameState.PLAYING)){
					if(args.length == 0){
						if(uhcPlayer.isGlobalChat()){
							uhcPlayer.setGlobalChat(false);
							uhcPlayer.sendMessage(ChatColor.GREEN+ Lang.COMMAND_CHAT_TEAM);
						}
						else{
							uhcPlayer.setGlobalChat(true);
							uhcPlayer.sendMessage(ChatColor.GREEN+Lang.COMMAND_CHAT_GLOBAL);
						}
					}else{
						player.sendMessage(ChatColor.GRAY+Lang.COMMAND_CHAT_HELP);
					}
				}else{
					player.sendMessage(ChatColor.RED+Lang.COMMAND_CHAT_ERROR);
				}
				

				return true;
			} catch (UhcPlayerDoesntExistException e) {
			}
			
		}
		

		return false;
	}

}
