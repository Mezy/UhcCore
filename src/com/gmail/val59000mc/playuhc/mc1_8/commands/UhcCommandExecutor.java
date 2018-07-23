package com.gmail.val59000mc.playuhc.mc1_8.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.gmail.val59000mc.playuhc.mc1_8.game.GameState;
import com.gmail.val59000mc.playuhc.mc1_8.players.PlayerState;
import com.gmail.val59000mc.playuhc.mc1_8.threads.PreStartThread;

public class UhcCommandExecutor implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		// For debug purpose
		if(sender instanceof ConsoleCommandSender || sender instanceof Player){
			if(sender.hasPermission("playuhc.commands")){
				if(args.length > 0){
					switch(args[0]){
						case "gamestate":
							if(args.length == 2){
								try{
									CommandManager.setGameState(GameState.valueOf(args[1].toUpperCase()));
									return true;
								}catch(IllegalArgumentException e){
									sender.sendMessage(args[1]+" is not a valid game state");
								}
							}
						break;
						
						case "playerstate":
							if(args.length == 3){
								try{
									Player player = Bukkit.getPlayer(args[1]);
									if(player == null)
										throw new Exception("Player "+args[1]+" is not online");
									CommandManager.setPlayerState(player,PlayerState.valueOf(args[2].toUpperCase()));
									return true;
								}catch(IllegalArgumentException e){
									sender.sendMessage(args[2]+" is not a valid player state");
								}catch(Exception e){
									sender.sendMessage(e.getMessage());
								}
							}
							
						case "pvp":
							if(args.length == 2){
								CommandManager.setPvp(Boolean.parseBoolean(args[1]));
								return true;
							}
							break;
							
						case "listplayers":
							CommandManager.listUhcPlayers();
							return true;
						
						case "listteams":
							CommandManager.listUhcTeams();
							return true;
							
						case "pause":
							String pauseState = PreStartThread.togglePause();
							sender.sendMessage("The starting thread state is now : "+pauseState);
							return true;
							
						case "force":
							String forceState = PreStartThread.toggleForce();
							sender.sendMessage("The starting thread state is now : "+forceState);
							return true;
					
					}
				}
			}
			
		}
		return false;
	}

}
