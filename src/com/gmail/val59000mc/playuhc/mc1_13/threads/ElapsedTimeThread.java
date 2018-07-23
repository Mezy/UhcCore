package com.gmail.val59000mc.playuhc.mc1_13.threads;

import com.gmail.val59000mc.playuhc.PlayUhc;
import com.gmail.val59000mc.playuhc.mc1_13.configuration.VaultManager;
import com.gmail.val59000mc.playuhc.mc1_13.events.UhcTimeEvent;
import com.gmail.val59000mc.playuhc.mc1_13.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.playuhc.mc1_13.game.GameManager;
import com.gmail.val59000mc.playuhc.mc1_13.game.GameState;
import com.gmail.val59000mc.playuhc.mc1_13.languages.Lang;
import com.gmail.val59000mc.playuhc.mc1_13.players.UhcPlayer;
import com.gmail.val59000mc.playuhc.mc1_13.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;

public class ElapsedTimeThread implements Runnable{

	private GameManager gm;
	private ElapsedTimeThread task;
	private boolean enableTimeEvent;
	private long intervalTimeEvent;
	private double reward;
	
	public ElapsedTimeThread() {
		this.gm = GameManager.getGameManager();
		this.task = this;
		this.enableTimeEvent = gm.getConfiguration().getEnableTimeEvent();
		this.intervalTimeEvent = gm.getConfiguration().getIntervalTimeEvent();
		this.reward = gm.getConfiguration().getRewardTimeEvent();
	}
	
	@Override
	public void run() {
		
		long time = gm.getElapsedTime() + 1;
		gm.setElapsedTime(time);
		
		if(time%intervalTimeEvent == 0){

			Set<UhcPlayer> playingPlayers = gm.getPlayersManager().getPlayingPlayer();
			
			// Call time event
			UhcTimeEvent event = new UhcTimeEvent(playingPlayers,intervalTimeEvent,time);
			Bukkit.getServer().getPluginManager().callEvent(event);
			
			if(enableTimeEvent){
				
				String message = Lang.EVENT_TIME_REWARD
						.replace("%time%", TimeUtils.getFormattedTime(intervalTimeEvent))
						.replace("%totaltime%", TimeUtils.getFormattedTime(time))
						.replace("%money%", ""+reward);
				
				for(UhcPlayer uhcP : playingPlayers){
					
					try {
						Player p = uhcP.getPlayer();
						VaultManager.addMoney(p, reward);
						if(!message.isEmpty()){
							p.sendMessage(message);
						}
					} catch (UhcPlayerNotOnlineException e) {
						// Tignore offline players
					}
				}
			}
		}
		
		if(!gm.getGameState().equals(GameState.ENDED)){
			Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), task, 20);
		}	
	}
	
}
