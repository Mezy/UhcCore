package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.UhcTeam;
import com.gmail.val59000mc.utils.TimeUtils;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;

import java.util.List;

public class PreStartThread implements Runnable{

	private static PreStartThread instance;
	
	private int timeBeforeStart;
	private int remainingTime;
	private int minPlayers;
	private boolean pause;
	private boolean force;
	private PreStartThread task;
	
	public PreStartThread(){
		
		
		MainConfiguration cfg = GameManager.getGameManager().getConfiguration();
		instance = this;
		this.timeBeforeStart = cfg.getTimeBeforeStartWhenReady();
		this.remainingTime = cfg.getTimeBeforeStartWhenReady();
		this.minPlayers = cfg.getMinPlayersToStart();
		this.pause = false;
		this.force = false;
		this.task = this;
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
		
		Bukkit.getScheduler().runTask(UhcCore.getPlugin(), new Runnable(){

			@Override
			public void run() {
				
				GameManager gm = GameManager.getGameManager();
				List<UhcTeam> teams = gm.getPlayersManager().listUhcTeams();
				double readyTeams = 0;
				double teamsNumber = (double) teams.size();
				for(UhcTeam team : teams){
					if(team.isReadyToStart() && team.isOnline())
						readyTeams+=1;
				}
				
				double percentageReadyTeams = 100*readyTeams/teamsNumber;
				int playersNumber = Bukkit.getOnlinePlayers().size();
				
				if(force == true || (pause == false && (remainingTime < 5 || (playersNumber >= minPlayers && readyTeams >= gm.getConfiguration().getMinimalReadyTeamsToStart() && percentageReadyTeams >= gm.getConfiguration().getMinimalReadyTeamsPercentageToStart())))){
						if(remainingTime == timeBeforeStart+1){
							gm.broadcastInfoMessage(Lang.GAME_ENOUGH_TEAMS_READY);
							gm.broadcastInfoMessage(Lang.GAME_STARTING_IN.replace("%time%", ""+ TimeUtils.getFormattedTime(remainingTime)));
							gm.getPlayersManager().playSoundToAll(UniversalSound.CLICK);
						}else if((remainingTime > 0 && remainingTime <= 10) || (remainingTime > 0 && remainingTime%10 == 0)){
							gm.broadcastInfoMessage(Lang.GAME_STARTING_IN.replace("%time%", ""+remainingTime));
							gm.getPlayersManager().playSoundToAll(UniversalSound.CLICK);
						}
						
						remainingTime--;
						
						if(remainingTime == -1)
							Bukkit.getScheduler().runTask(UhcCore.getPlugin(), new Runnable(){

								@Override
								public void run() {
									GameManager.getGameManager().startGame();
								}
							});
						else		
							Bukkit.getScheduler().runTaskLaterAsynchronously(UhcCore.getPlugin(), task,20);
				}else{
					if(pause == false && remainingTime < timeBeforeStart+1){
						gm.broadcastInfoMessage(Lang.GAME_STARTING_CANCELLED);
					}
					remainingTime = timeBeforeStart+1;
					Bukkit.getScheduler().runTaskLaterAsynchronously(UhcCore.getPlugin(), task,20);
				}
			}
			
		});
		
	}

}
