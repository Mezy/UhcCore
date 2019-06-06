package com.gmail.val59000mc.mc1_13.commands;

import com.gmail.val59000mc.mc1_13.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.mc1_13.game.GameManager;
import com.gmail.val59000mc.mc1_13.game.GameState;
import com.gmail.val59000mc.mc1_13.players.PlayerState;
import com.gmail.val59000mc.mc1_13.players.UhcPlayer;
import com.gmail.val59000mc.mc1_13.players.UhcTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandManager {
	public static void setGameState(GameState gameState){
		GameManager.getGameManager().setGameState(gameState);
	}

	public static void setPlayerState(Player player, PlayerState state) {
		try {
			GameManager.getGameManager().getPlayersManager().getUhcPlayer(player).setState(state);
		} catch (UhcPlayerDoesntExistException e) {
		}
	}

	public static void setPvp(boolean state) {
		GameManager.getGameManager().setPvp(state);
	}

	public static void listUhcPlayers() {
		StringBuilder str = new StringBuilder();
		str.append("Current UhcPlayers : ");
		for(UhcPlayer player : GameManager.getGameManager().getPlayersManager().getPlayersList()){
			str.append(player.getName()+" ");
		}
		Bukkit.getLogger().info(str.toString());
	}

	public static void listUhcTeams() {
		StringBuilder str;
		Bukkit.getLogger().info("Current UhcTeams : ");
		
		for(UhcTeam team : GameManager.getGameManager().getPlayersManager().listUhcTeams()){
			str = new StringBuilder();
			str.append("Team "+team.getLeader().getName()+" : ");
			for(UhcPlayer player : team.getMembers()){
				str.append(player.getName()+" ");
			}
			Bukkit.getLogger().info(str.toString());
		}
	}
}
