package com.gmail.val59000mc.players;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.scoreboard.ScoreboardLayout;
import com.gmail.val59000mc.scoreboard.ScoreboardManager;
import com.gmail.val59000mc.scoreboard.ScoreboardType;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class UpdateScoreboardThread implements Runnable{

	private UhcPlayer uhcPlayer;
	private Player bukkitPlayer;
	private UpdateScoreboardThread task;
	private Scoreboard scoreboard;
	private GameManager gm;
	private ScoreboardLayout scoreboardLayout;
	private ScoreboardManager scoreboardManager;
	private ScoreboardType scoreboardType;
	private final long updateDelay = 20L;
	private Objective objective;



	public UpdateScoreboardThread(UhcPlayer uhcPlayer){
		this.uhcPlayer = uhcPlayer;
		task = this;
		gm = GameManager.getGameManager();
		scoreboardManager = gm.getScoreboardManager();
		scoreboardLayout = scoreboardManager.getScoreboardLayout();
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

			if (!line.equals("")) {

				String translatedLine = scoreboardManager.translatePlaceholders(line, uhcPlayer, bukkitPlayer, scoreboardType);

				if (translatedLine.length() <= 16){
					first = translatedLine;
				}else {

					int split = 16;

					first = translatedLine.substring(0, split);

					if (first.endsWith("§")){
						split = 15;
						first = translatedLine.substring(0, split);
					}

					second = ChatColor.getLastColors(first);

					second += translatedLine.substring(split, translatedLine.length());

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


		Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(),task,updateDelay);

	}

	private ScoreboardType getScoreboardType(){

		if (gm.getGameState().equals(GameState.WAITING)){
			return ScoreboardType.WAITING;
		}

		if (uhcPlayer.getState().equals(PlayerState.DEAD)){
			return ScoreboardType.SPECTATING;
		}

		if (gm.getGameState().equals(GameState.PLAYING) || gm.getGameState().equals(GameState.ENDED)){
			return ScoreboardType.PLAYING;
		}

		if (gm.getGameState().equals(GameState.DEATHMATCH)){
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