package com.gmail.val59000mc.playuhc.threads;

import com.gmail.val59000mc.playuhc.sounds.UhcSound;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.gmail.val59000mc.playuhc.PlayUhc;
import com.gmail.val59000mc.playuhc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.playuhc.game.GameManager;
import com.gmail.val59000mc.playuhc.languages.Lang;
import com.gmail.val59000mc.playuhc.players.UhcPlayer;
import com.gmail.val59000mc.playuhc.utils.TimeUtils;

public class TimeBeforeSendBungeeThread implements Runnable{
	
	private UhcPlayer uhcPlayer;
	private int remainingTime;
	private TimeBeforeSendBungeeThread task;
	
	public TimeBeforeSendBungeeThread(UhcPlayer uhcPlayer,int remainingTime){
		this.uhcPlayer = uhcPlayer;
		this.remainingTime = remainingTime;
		this.task = this;
	}
	
	
	@Override
	public void run() {
		
		Bukkit.getScheduler().runTask(PlayUhc.getPlugin(), new Runnable(){

			@Override
			public void run() {
				remainingTime--;
				
				Player player;
				try {
					player = uhcPlayer.getPlayer();

					if(remainingTime <=10 || (remainingTime > 10 && remainingTime%10 == 0)){
						player.sendMessage(Lang.PLAYERS_SEND_BUNGEE.replace("%time%",TimeUtils.getFormattedTime(remainingTime)));
						GameManager.getGameManager().getPlayersManager().playsoundTo(uhcPlayer, UhcSound.CLICK);
					}
					
					if(remainingTime <= 0){
						GameManager.getGameManager().getPlayersManager().sendPlayerToBungeeServer(player, "");
					}
					
				} catch (UhcPlayerNotOnlineException e) {
					// nothing to do for offline players
				}
				
				if(remainingTime > 0){
					Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), task, 20);
				}
			}
			
		});
		
		
	}

}
