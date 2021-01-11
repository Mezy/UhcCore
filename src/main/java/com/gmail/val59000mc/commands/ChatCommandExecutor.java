package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommandExecutor implements CommandExecutor{

	private final PlayerManager playerManager;

	public ChatCommandExecutor(PlayerManager playerManager){
		this.playerManager = playerManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (!(sender instanceof Player)){
			sender.sendMessage("Only players can use this command!");
			return true;
		}

		Player player = (Player) sender;
		UhcPlayer uhcPlayer = playerManager.getUhcPlayer(player);

		if(!uhcPlayer.getState().equals(PlayerState.PLAYING)){
			player.sendMessage(Lang.COMMAND_CHAT_ERROR);
			return true;
		}

		if(args.length == 0){
			if(uhcPlayer.isGlobalChat()){
				uhcPlayer.setGlobalChat(false);
				uhcPlayer.sendMessage(Lang.COMMAND_CHAT_TEAM);
			}else{
				uhcPlayer.setGlobalChat(true);
				uhcPlayer.sendMessage(Lang.COMMAND_CHAT_GLOBAL);
			}
			return true;
		}else{
			player.sendMessage(Lang.COMMAND_CHAT_HELP);
			return true;
		}
	}

}