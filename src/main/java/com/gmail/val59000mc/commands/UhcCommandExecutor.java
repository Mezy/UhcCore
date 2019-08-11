package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import com.gmail.val59000mc.threads.PreStartThread;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UhcCommandExecutor implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

		if (args.length == 1 && args[0].equalsIgnoreCase("reload")){
			if (!sender.hasPermission("uhc-core.commands.reload")){
				sender.sendMessage(ChatColor.RED + "You don't have the permission to use this command");
				return true;
			}

			new Lang();
			GameManager.getGameManager().getScoreboardManager().getScoreboardLayout().loadFile();
			sender.sendMessage(ChatColor.GREEN + "lang.yml and scoreboard.yml have been reloaded");
			return true;
		}

		// debug commands
		if (!sender.hasPermission("uhc-core.commands.debug")){
			sender.sendMessage(ChatColor.RED + "You don't have the permission to use UHC-Core debug commands");
			return true;
		}

		if (args.length == 0){
			sender.sendMessage("Invalid command");
			return true;
		}

		GameManager gm = GameManager.getGameManager();
		PlayersManager pm = gm.getPlayersManager();

		switch(args[0]){
			case "gamestate":
				if(args.length == 2){
					try{
						GameState gameState = GameState.valueOf(args[1].toUpperCase());
						gm.setGameState(gameState);
						sender.sendMessage("Changed gamestate to: " + gameState.toString());
						return true;
					}catch(IllegalArgumentException e){
						sender.sendMessage(args[1]+" is not a valid game state");
						return true;
					}
				}else {
					sender.sendMessage("Current gamestate: " + gm.getGameState());
					return true;
				}
			case "playerstate":
				if(args.length == 3){
					try{
						Player player = Bukkit.getPlayer(args[1]);
						if(player == null){
							sender.sendMessage("Player "+args[1]+" is not online");
							return true;
						}
						PlayerState playerState = PlayerState.valueOf(args[2].toUpperCase());
						pm.getUhcPlayer(player).setState(playerState);
						sender.sendMessage("Changed " + player.getName() + "'s playerstate to " + playerState);
						return true;
					}catch(IllegalArgumentException e){
						sender.sendMessage(args[2]+" is not a valid player state");
						return true;
					}catch(Exception e){
						sender.sendMessage(e.getMessage());
						return true;
					}
				}else {
					sender.sendMessage("Invalid playerstate command");
					return true;
				}

			case "pvp":
				if(args.length == 2){
					boolean state = Boolean.parseBoolean(args[1]);
					gm.setPvp(state);
					sender.sendMessage("Changed PvP to " + state);
					return true;
				}else {
					sender.sendMessage("Invalid pvp command");
					return true;
				}

			case "listplayers":
				listUhcPlayers(sender);
				return true;

			case "listteams":
				listUhcTeams(sender);
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

		sender.sendMessage("Unknown sub command " + args[0]);
		return true;
	}


	private void listUhcPlayers(CommandSender sender) {
		StringBuilder str = new StringBuilder();
		str.append("Current UhcPlayers : ");
		for(UhcPlayer player : GameManager.getGameManager().getPlayersManager().getPlayersList()){
			str.append(player.getName()+" ");
		}
		sender.sendMessage(str.toString());
	}

	private void listUhcTeams(CommandSender sender) {
		StringBuilder str;
		Bukkit.getLogger().info("Current UhcTeams : ");

		for(UhcTeam team : GameManager.getGameManager().getPlayersManager().listUhcTeams()){
			str = new StringBuilder();
			str.append("Team "+team.getLeader().getName()+" : ");
			for(UhcPlayer player : team.getMembers()){
				str.append(player.getName()+" ");
			}
			sender.sendMessage(str.toString());
		}
	}

}