package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.UhcTeam;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;

import java.util.List;

public class PreStartThread implements Runnable{

	private static PreStartThread instance;

	private final GameManager gameManager;
	private final int timeBeforeStart;
	private int remainingTime;
	private final int minPlayers;
	private final boolean teamsAlwaysReady;
	private boolean pause, force;
	
	public PreStartThread(GameManager gameManager){
		this.gameManager = gameManager;
		MainConfig cfg = gameManager.getConfig();
		instance = this;
		this.timeBeforeStart = cfg.get(MainConfig.TIME_BEFORE_START_WHEN_READY);
		this.remainingTime = cfg.get(MainConfig.TIME_BEFORE_START_WHEN_READY);
		this.minPlayers = cfg.get(MainConfig.MIN_PLAYERS_TO_START);
		this.teamsAlwaysReady = cfg.get(MainConfig.TEAM_ALWAYS_READY);
		this.pause = false;
		this.force = false;
	}
	
	public static String togglePause(){
		instance.pause = !instance.pause;
		return "pause:"+instance.pause+"  "+"force:"+instance.force;
	}
	
	public static String toggleForce(){
		instance.force = !instance.force;
		return "pause:"+instance.pause+"  "+"force:"+instance.force;
	}
	
	@Override
	public void run() {
		List<UhcTeam> teams = gameManager.getPlayerManager().listUhcTeams();
		double readyTeams = 0;
		double teamsNumber = teams.size();

		for(UhcTeam team : teams){
			if((teamsAlwaysReady || team.isReadyToStart()) && team.isOnline()) {
				readyTeams += 1;
			}
		}

		double percentageReadyTeams = 100*readyTeams/teamsNumber;
		int playersNumber = Bukkit.getOnlinePlayers().size();

		if(
				force ||
				(!pause && (remainingTime < 5 || (playersNumber >= minPlayers && readyTeams >= gameManager.getConfig().get(MainConfig.MINIMAL_READY_TEAMS_TO_START) && percentageReadyTeams >= gameManager.getConfig().get(MainConfig.MINIMAL_READY_TEAMS_PERCENTAGE_TO_START))))
		){
			if(remainingTime == timeBeforeStart+1){
				gameManager.broadcastInfoMessage(Lang.GAME_ENOUGH_TEAMS_READY);
				gameManager.broadcastInfoMessage(Lang.GAME_STARTING_IN.replace("%time%", String.valueOf(remainingTime)));
				gameManager.getPlayerManager().playSoundToAll(UniversalSound.CLICK);
			}else if((remainingTime > 0 && remainingTime <= 10) || (remainingTime > 0 && remainingTime%10 == 0)){
				gameManager.broadcastInfoMessage(Lang.GAME_STARTING_IN.replace("%time%", String.valueOf(remainingTime)));
				gameManager.getPlayerManager().playSoundToAll(UniversalSound.CLICK);
			}

			remainingTime--;

			if(remainingTime == -1) {
				GameManager.getGameManager().startGame();
			}
			else{
				Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this, 20);
			}
		}else{
			if(!pause && remainingTime < timeBeforeStart+1){
				gameManager.broadcastInfoMessage(Lang.GAME_STARTING_CANCELLED);
			}
			remainingTime = timeBeforeStart+1;
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this,20);
		}
	}

}