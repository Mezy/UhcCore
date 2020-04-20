package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.VaultManager;
import com.gmail.val59000mc.events.UhcTimeEvent;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ElapsedTimeThread implements Runnable{

	private GameManager gm;
	private ElapsedTimeThread task;
	private boolean enableTimeEvent;
	private long intervalTimeEvent;
	private double reward;
	private List<String> timeCommands;
	private boolean cmdsNeedsPlayer = false;
	
	public ElapsedTimeThread() {
		this.gm = GameManager.getGameManager();
		this.task = this;
		this.enableTimeEvent = gm.getConfiguration().getEnableTimeEvent();
		this.intervalTimeEvent = gm.getConfiguration().getIntervalTimeEvent();
		this.reward = gm.getConfiguration().getRewardTimeEvent();
		this.timeCommands = gm.getConfiguration().getTimeCommands();
		for (String cmd : timeCommands){
			if (cmd.contains("%name%")){
				this.cmdsNeedsPlayer = true;
				break;
			}
		}
	}
	
	@Override
	public void run() {
		
		long time = gm.getElapsedTime() + 1;
		gm.setElapsedTime(time);

		Set<UhcPlayer> playingPlayers = gm.getPlayersManager().getOnlinePlayingPlayers();

		// Call time event
		UhcTimeEvent event = new UhcTimeEvent(playingPlayers,time);
		Bukkit.getScheduler().runTask(UhcCore.getPlugin(), new Runnable() {
			@Override
			public void run() {
				Bukkit.getServer().getPluginManager().callEvent(event);
			}
		});
		
		if(time%intervalTimeEvent == 0){
			
			if(enableTimeEvent){
				String message;

				message = Lang.EVENT_TIME_REWARD
						.replace("%time%", TimeUtils.getFormattedTime(intervalTimeEvent))
						.replace("%totaltime%", TimeUtils.getFormattedTime(time))
						.replace("%money%", "" + reward);

				for (UhcPlayer uhcP : playingPlayers) {
					try {
						Player p = uhcP.getPlayer();
						if (reward > 0) {
							VaultManager.addMoney(p, reward);
						}
						if (cmdsNeedsPlayer) {
							timeCommands.forEach(cmd -> {
								try {
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%name%", uhcP.getRealName()));
								} catch (CommandException exception) {
									Bukkit.getLogger().warning("The command: '" + cmd + "' does not exists.");
								}
							});
						}

						if (!message.isEmpty()) {
							p.sendMessage(message);
						}
					} catch (UhcPlayerNotOnlineException e) {
						// Tignore offline players
					}
				}
				if (!cmdsNeedsPlayer){
					timeCommands.forEach(cmd -> {
						try {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
						} catch (CommandException exception) {
							Bukkit.getLogger().warning("The command: '" + cmd + "' does not exists.");
						}
					});
				}
			}
		}

		if(!gm.getGameState().equals(GameState.ENDED)){
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), task, 20);
		}
	}

}
