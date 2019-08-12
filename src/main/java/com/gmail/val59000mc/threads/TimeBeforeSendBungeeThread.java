package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.utils.TimeUtils;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
		
		Bukkit.getScheduler().runTask(UhcCore.getPlugin(), new Runnable(){

			@Override
			public void run() {
				remainingTime--;
				
				Player player;
				try {
					player = uhcPlayer.getPlayer();

					if(remainingTime <=10 || (remainingTime > 10 && remainingTime%10 == 0)){
						player.sendMessage(Lang.PLAYERS_SEND_BUNGEE.replace("%time%",TimeUtils.getFormattedTime(remainingTime)));
						GameManager.getGameManager().getPlayersManager().playsoundTo(uhcPlayer, UniversalSound.CLICK);
					}
					
					if(remainingTime <= 0){
						GameManager.getGameManager().getPlayersManager().sendPlayerToBungeeServer(player);
					}
					
				} catch (UhcPlayerNotOnlineException e) {
					// nothing to do for offline players
				}
				
				if(remainingTime > 0){
					Bukkit.getScheduler().runTaskLaterAsynchronously(UhcCore.getPlugin(), task, 20);
				}
			}
			
		});
		
		
	}

}
