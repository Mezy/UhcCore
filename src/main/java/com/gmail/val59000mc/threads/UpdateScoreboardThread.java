package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.handlers.ScoreboardHandler;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scoreboard.ScoreboardType;
import org.bukkit.Bukkit;

public class UpdateScoreboardThread implements Runnable {
	private static final long UPDATE_DELAY = 20L;

	private final ScoreboardHandler scoreboardHandler;

	private final UhcPlayer uhcPlayer;

	private ScoreboardType scoreboardType;

	public UpdateScoreboardThread(ScoreboardHandler scoreboardHandler, UhcPlayer uhcPlayer) {
		this.scoreboardHandler = scoreboardHandler;
		this.uhcPlayer = uhcPlayer;
	}

	@Override
	public void run() {
		if (!uhcPlayer.isOnline()) {
			return;
		}

		ScoreboardType newType = scoreboardHandler.getPlayerScoreboardType(uhcPlayer);

		if (scoreboardType != newType) {
			scoreboardType = newType;
			scoreboardHandler.resetObjective(uhcPlayer.getScoreboard(), scoreboardType);
			scoreboardHandler.updatePlayerOnTab(uhcPlayer);
		}

		scoreboardHandler.updatePlayerSidebar(uhcPlayer, scoreboardType);

		Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(),this, UPDATE_DELAY);
	}

}