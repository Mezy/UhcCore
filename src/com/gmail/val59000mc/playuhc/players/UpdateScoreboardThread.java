package com.gmail.val59000mc.playuhc.players;

import java.util.ArrayList;
import java.util.List;

import com.gmail.val59000mc.playuhc.sounds.UhcSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.val59000mc.playuhc.PlayUhc;
import com.gmail.val59000mc.playuhc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.playuhc.game.GameManager;
import com.gmail.val59000mc.playuhc.languages.Lang;

public class UpdateScoreboardThread implements Runnable{
	
	UhcPlayer uhcPlayer;
	String timeString;
	String borderString;
	String killsString;
	List<String> teamatesStrings;
	Scoreboard scoreboard;
	Objective informations;
	Objective kills;
	World world;
	int nextTick;
	GameManager gm;
	UpdateScoreboardThread task;
	
	
	
	public UpdateScoreboardThread(UhcPlayer uhcPlayer){
		this.uhcPlayer = uhcPlayer;
		timeString = null;
		borderString = null;
		killsString = null;
		teamatesStrings = null;
		nextTick = 20;
		task = this;
		

		gm = GameManager.getGameManager();
		
		world = gm.getLobby().getLoc().getWorld();
		scoreboard = uhcPlayer.getScoreboard();
		informations = scoreboard.getObjective("informations");
		kills = scoreboard.getObjective("kills");
	}
	@Override
	public void run() {
		Bukkit.getScheduler().runTask(PlayUhc.getPlugin(), new Runnable(){

			@Override
			public void run() {
				
				EraseLastScores();
				PrintNewScore();
				try {
					uhcPlayer.getPlayer().setScoreboard(scoreboard);
				}catch (UhcPlayerNotOnlineException e) {
					// No scoreboard for offline players
				}
				Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), task, nextTick);	
				
			}});
		
	}
	
	private void EraseLastScores(){
		
		if(borderString != null && killsString != null && teamatesStrings != null && timeString != null){
			scoreboard.resetScores(timeString);
			scoreboard.resetScores(borderString);
			scoreboard.resetScores(killsString);
			for(String teamate : teamatesStrings){
				scoreboard.resetScores(teamate);
			}
		}
	}
	
	private void PrintNewScore(){
		int linesCount = 3;
		
		timeString = "";
		if(gm.getConfiguration().getEnableTimeLimit()){
			linesCount = 4;
			timeString = ChatColor.WHITE+Lang.SCOREBOARD_TIME+" : ";
			timeString += ChatColor.GREEN+gm.getFormatedRemainingTime();
		}
		
		
		// Write border string
		long borderSize = (long) Math.floor(world.getWorldBorder().getSize()/2);
		borderString = ChatColor.WHITE+Lang.SCOREBOARD_BORDER+" : ";
		String borderSizeString = "+"+borderSize+"  -"+borderSize;
		try{
			Player p = uhcPlayer.getPlayer();
			if(p.getWorld().getEnvironment().equals(Environment.NETHER)){
				borderSize = (long) Math.floor(world.getWorldBorder().getSize()/4);
				borderSizeString = "+"+borderSize+"  -"+borderSize;
			}
			double distX = Math.abs(Math.abs(p.getLocation().getX())-borderSize);
			double distZ = Math.abs(Math.abs(p.getLocation().getZ())-borderSize);
			if(distX < 25 || distZ < 25){
				borderSizeString = ChatColor.RED+borderSizeString;
				gm.getPlayersManager().playsoundTo(uhcPlayer, UhcSound.CLICK);
			}else if(distX < 80 || distZ < 80){
				borderSizeString = ChatColor.YELLOW+borderSizeString;
			}else{
				borderSizeString = ChatColor.GREEN+borderSizeString;
			}
		}catch(UhcPlayerNotOnlineException e){
			// Green display for offline players
			borderString = ChatColor.GREEN+borderSizeString;
			nextTick = 80;
		}
		borderString = borderString+borderSizeString;
		
		// Write kills string
		killsString = Lang.SCOREBOARD_KILLS+" : "+ChatColor.GREEN+kills.getScore(uhcPlayer.getName()).getScore();
		
		// Write teamates strings
		teamatesStrings = new ArrayList<String>();
		teamatesStrings.add(Lang.SCOREBOARD_TEAM+" : ");
		for(UhcPlayer teamate : uhcPlayer.getTeam().getMembers()){
			String teamateString = teamate.getName();
			
			if(teamate.getState().equals(PlayerState.PLAYING)){
				teamateString = ChatColor.GREEN+teamateString+ChatColor.WHITE;

				// Display life for playing players
				try{
					Player p = teamate.getPlayer();
					Double health = p.getHealth();
					String colorHP = ChatColor.GREEN+"";
					if(health < 8)
						colorHP = ChatColor.RED+"";
					else if(health < 15)
						colorHP = ChatColor.YELLOW+"";
					teamateString += " : "+colorHP+((long) Math.round(health))+" "+Lang.SCOREBOARD_LIFE;
				}catch(UhcPlayerNotOnlineException e){
					// Life = 0 for offline players
					teamateString += " : "+ChatColor.GRAY+"? "+Lang.SCOREBOARD_LIFE;
				}
				
			}else{
				teamateString = ChatColor.GRAY+teamateString;
			}
			teamatesStrings.add(teamateString);
			linesCount++;
		}
		
		
		// Print on scoreboard
		if(timeString != ""){
			informations.getScore(timeString).setScore(linesCount);
			linesCount--;
		}
		informations.getScore(borderString).setScore(linesCount);
		linesCount--;
		informations.getScore(killsString).setScore(linesCount);
		linesCount--;
		for(int line = linesCount ; line > 0 ; line--){
			informations.getScore(teamatesStrings.get(teamatesStrings.size()-line)).setScore(line);
		}
	}
	
	
}
