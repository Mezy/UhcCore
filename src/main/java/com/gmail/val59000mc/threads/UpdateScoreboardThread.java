package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scoreboard.ScoreboardLayout;
import com.gmail.val59000mc.scoreboard.ScoreboardManager;
import com.gmail.val59000mc.scoreboard.ScoreboardType;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class UpdateScoreboardThread implements Runnable{
	private static final long UPDATE_DELAY = 20L;
	private static final String COLOR_CHAR = String.valueOf(ChatColor.COLOR_CHAR);

	private final GameManager gameManager;
	private final ScoreboardLayout scoreboardLayout;
	private final ScoreboardManager scoreboardManager;

	private final UhcPlayer uhcPlayer;
	private final Scoreboard scoreboard;

	private Player bukkitPlayer;
	private ScoreboardType scoreboardType;
	private Objective objective;

	public UpdateScoreboardThread(GameManager gameManager, UhcPlayer uhcPlayer){
		this.gameManager = gameManager;
		scoreboardManager = gameManager.getScoreboardManager();
		scoreboardLayout = scoreboardManager.getScoreboardLayout();

		this.uhcPlayer = uhcPlayer;
		scoreboard = uhcPlayer.getScoreboard();

		scoreboardType = getScoreboardType();
		objective = VersionUtils.getVersionUtils().registerObjective(scoreboard, "informations", "dummy");
		resetObjective();

		try {
			bukkitPlayer = uhcPlayer.getPlayer();
		}catch (UhcPlayerNotOnlineException ex){
			// Nothing
		}
	}

	@Override
	public void run() {
		if (!uhcPlayer.isOnline()){
			return;
		}

		if (!scoreboardType.equals(getScoreboardType())){
			scoreboardType = getScoreboardType();
			resetObjective();
			scoreboardManager.updatePlayerTab(uhcPlayer);
		}

		int i = 0;
		for (String line : scoreboardLayout.getLines(scoreboardType)){

			String first = "";
			String second = "";

			if (!line.isEmpty()) {

				String translatedLine = scoreboardManager.translatePlaceholders(line, uhcPlayer, bukkitPlayer, scoreboardType);

				if (translatedLine.length() <= 16){
					first = translatedLine;
				}else {

					int split = 16;

					first = translatedLine.substring(0, split);
					boolean copyColor = true;

					if (first.endsWith(COLOR_CHAR)){
						copyColor = false;
						split = 15;
						first = translatedLine.substring(0, split);

						if (first.substring(0, 14).endsWith(COLOR_CHAR)){
							split = 13;
							first = translatedLine.substring(0, split);
						}
					}

					if (copyColor) {
						second = ChatColor.getLastColors(first);
					}

					second += translatedLine.substring(split);

					if (second.length() > 16){
						Bukkit.getLogger().warning("[UhcCore] Scoreboard line is too long: '" + translatedLine + "'!");
						second = "";
					}
				}
			}

			Team lineTeam = scoreboard.getTeam(scoreboardManager.getScoreboardLine(i));

			if (!lineTeam.getPrefix().equals(first)){
				lineTeam.setPrefix(first);
			}
			if (!lineTeam.getSuffix().equals(second)){
				lineTeam.setSuffix(second);
			}

			i++;
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(),this, UPDATE_DELAY);
	}

	private ScoreboardType getScoreboardType(){
		if (uhcPlayer.getState().equals(PlayerState.DEAD)){
			return ScoreboardType.SPECTATING;
		}

		GameState gameState = gameManager.getGameState();

		if (gameState.equals(GameState.WAITING)){
			return ScoreboardType.WAITING;
		}

		if (gameState.equals(GameState.PLAYING) || gameState.equals(GameState.ENDED)){
			return ScoreboardType.PLAYING;
		}

		if (gameState.equals(GameState.DEATHMATCH)){
			return ScoreboardType.DEATHMATCH;
		}

		return ScoreboardType.PLAYING;
	}

	private void resetObjective(){
		objective.unregister();
		objective = VersionUtils.getVersionUtils().registerObjective(scoreboard, "informations","dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(scoreboardLayout.getTitle());

		int lines = scoreboardLayout.getLines(scoreboardType).size();

		for (int i = 0; i < lines; i++){
			Score score = objective.getScore(scoreboardManager.getScoreboardLine(i));
			score.setScore(i);
		}
	}

}