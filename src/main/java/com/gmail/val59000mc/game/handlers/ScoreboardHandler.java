package com.gmail.val59000mc.game.handlers;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.players.*;
import com.gmail.val59000mc.scoreboard.ScoreboardLayout;
import com.gmail.val59000mc.scoreboard.ScoreboardManager;
import com.gmail.val59000mc.scoreboard.ScoreboardType;
import com.gmail.val59000mc.threads.UpdateScoreboardThread;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardHandler {

    private static final String COLOR_CHAR = String.valueOf(ChatColor.COLOR_CHAR);
    public static final String[] SCOREBOARD_LINES = new String[] {
            ChatColor.UNDERLINE + "" + ChatColor.RESET,
            ChatColor.ITALIC + "" + ChatColor.RESET,
            ChatColor.BOLD + "" + ChatColor.RESET,
            ChatColor.RESET + "" + ChatColor.RESET,
            ChatColor.GREEN + "" + ChatColor.RESET,
            ChatColor.DARK_GRAY + "" + ChatColor.RESET,
            ChatColor.GOLD + "" + ChatColor.RESET,
            ChatColor.RED + "" + ChatColor.RESET,
            ChatColor.YELLOW + "" + ChatColor.RESET,
            ChatColor.WHITE + "" + ChatColor.RESET,
            ChatColor.DARK_GREEN + "" + ChatColor.RESET,
            ChatColor.BLUE + "" + ChatColor.RESET,
            ChatColor.STRIKETHROUGH + "" + ChatColor.RESET,
            ChatColor.MAGIC + "" + ChatColor.RESET,
            ChatColor.DARK_RED + "" + ChatColor.RESET
    };

    private final GameManager gameManager;
    private final MainConfig config;
    private final ScoreboardLayout scoreboardLayout;

    public ScoreboardHandler(GameManager gameManager, MainConfig config, ScoreboardLayout scoreboardLayout) {
        this.gameManager = gameManager;
        this.config = config;
        this.scoreboardLayout = scoreboardLayout;
    }

    public void setUpPlayerScoreboard(UhcPlayer uhcPlayer, Player bukkitPlayer) {

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        uhcPlayer.setScoreboard(scoreboard);
        bukkitPlayer.setScoreboard(scoreboard);

        Objective healthTab = null;
        Objective healthBelowName = null;

        if (config.get(MainConfig.HEARTS_ON_TAB)) {
            healthTab = VersionUtils.getVersionUtils().registerObjective(scoreboard, "health_tab", "health");
            healthTab.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }

        if (config.get(MainConfig.HEARTS_BELOW_NAME)) {
            healthBelowName = VersionUtils.getVersionUtils().registerObjective(scoreboard, ChatColor.RED + "\u2764", "health");
            healthBelowName.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

        // add teams for no flicker scoreboard
        for (int i = 0; i < 15; i++){
            Team team = scoreboard.registerNewTeam(SCOREBOARD_LINES[i]);
            team.addEntry(SCOREBOARD_LINES[i]);
        }

        boolean disableEnemyNameTags = config.get(MainConfig.DISABLE_ENEMY_NAMETAGS);
        PlayerManager playerManager = gameManager.getPlayerManager();

        // setup teams
        if (config.get(MainConfig.TEAM_COLORS)){
            setColoredTeams(gameManager.getTeamManager(), uhcPlayer, scoreboard, disableEnemyNameTags, healthTab, healthBelowName);
        }else {
            setFriendEnemyTeams(uhcPlayer, playerManager, scoreboard, disableEnemyNameTags, healthTab, healthBelowName);
        }

        updatePlayerOnTab(uhcPlayer);

        Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(),new UpdateScoreboardThread(this, uhcPlayer),1L);
    }

    private void setFriendEnemyTeams(UhcPlayer scoreboardPlayer, PlayerManager pm, Scoreboard scoreboard, boolean disableEnemyNameTags, Objective healthTab, Objective healthBelowName) {
        Team friends = scoreboard.registerNewTeam("friends");
        Team enemies = scoreboard.registerNewTeam("enemies");
        friends.setPrefix(ChatColor.GREEN + "");
        enemies.setPrefix(ChatColor.RED + "");
        friends.setSuffix(ChatColor.RESET + "");
        enemies.setSuffix(ChatColor.RESET + "");

        if (disableEnemyNameTags){
            VersionUtils.getVersionUtils().setTeamNameTagVisibility(enemies, false);
        }

        Team spectators = scoreboard.registerNewTeam("spectators");
        spectators.setPrefix(ChatColor.GRAY + "");
        spectators.setSuffix(ChatColor.RESET + "");

        // Putting players in colored teams
        for (UhcPlayer uhcPlayer : pm.getPlayersList()) {
            updatePlayerHealth(uhcPlayer, healthTab, healthBelowName);

            if (uhcPlayer.getState().equals(PlayerState.DEAD) || uhcPlayer.getState().equals(PlayerState.WAITING)){
                spectators.addEntry(uhcPlayer.getName());
            }else if (uhcPlayer.isInTeamWith(scoreboardPlayer)) {
                friends.addEntry(uhcPlayer.getName());
            }else {
                enemies.addEntry(uhcPlayer.getName());
            }

        }
    }

    private void setColoredTeams(TeamManager teamManager, UhcPlayer scoreboardPlayer, Scoreboard scoreboard, boolean disableEnemyNameTags, Objective healthTab, Objective healthBelowName) {
        Team spectators = scoreboard.registerNewTeam("spectators");
        spectators.setPrefix(ChatColor.GRAY + "");
        spectators.setSuffix(ChatColor.RESET + "");

        for (UhcTeam uhcTeam : teamManager.getUhcTeams()) {

            Team team;

            if (uhcTeam.contains(scoreboardPlayer)) {
                // Add 0 to be at the top of the tab list
                team = scoreboard.registerNewTeam("0" + uhcTeam.getTeamNumber());
            } else {
                team = scoreboard.registerNewTeam("" + uhcTeam.getTeamNumber());

                if (disableEnemyNameTags){
                    VersionUtils.getVersionUtils().setTeamNameTagVisibility(team, false);
                }
            }

            team.setPrefix(uhcTeam.getPrefix());
            team.setSuffix(ChatColor.RESET + "");

            for (UhcPlayer member : uhcTeam.getMembers()) {

                updatePlayerHealth(member, healthTab, healthBelowName);

                if (member.getState().equals(PlayerState.DEAD)) {
                    // spec team
                    spectators.addEntry(member.getName());
                } else {
                    team.addEntry(member.getName());
                }
            }
        }
    }

    private void updatePlayerHealth(UhcPlayer uhcPlayer, Objective healthTab, Objective healthBelowName) {
        Player player;

        try {
            player = uhcPlayer.getPlayer();
        } catch (UhcPlayerNotOnlineException ex) {
            // No health display for offline players.
            return;
        }

        if (healthTab != null) {
            healthTab.getScore(player.getName()).setScore((int) player.getHealth());
        }
        if (healthBelowName != null) {
            healthBelowName.getScore(player.getName()).setScore((int) player.getHealth());
        }
    }

    public void updatePlayerOnTab(UhcPlayer uhcPlayer) {
        boolean teamColors = config.get(MainConfig.TEAM_COLORS);

        for (UhcPlayer scoreboardOwner : gameManager.getPlayerManager().getPlayersList()) {
            if (teamColors) {
                updatePlayerOnColoredTab(uhcPlayer, scoreboardOwner);
            }else {
                updatePlayerOnFriendEnemyTab(uhcPlayer, scoreboardOwner);
            }
        }

        // Change player display name
        if (teamColors && config.get(MainConfig.CHANGE_DISPLAY_NAMES)) {
            try {
                uhcPlayer.getPlayer().setDisplayName(uhcPlayer.getDisplayName());
            }catch (UhcPlayerNotOnlineException ex){
                // Player left while updating tab.
            }
        }
    }

    private void updatePlayerOnFriendEnemyTab(UhcPlayer uhcPlayer, UhcPlayer scoreboardOwner) {
        Scoreboard scoreboard = scoreboardOwner.getScoreboard();
        if (scoreboard == null) {
            return;
        }

        if (uhcPlayer.getState().equals(PlayerState.PLAYING)) {
            if (scoreboardOwner.isInTeamWith(uhcPlayer)) {
                // add to there friend team
                scoreboard.getTeam("friends").addEntry(uhcPlayer.getName());
            } else {
                // add to enemies team
                scoreboard.getTeam("enemies").addEntry(uhcPlayer.getName());
            }
        } else {
            // add to spectators team
            scoreboard.getTeam("spectators").addEntry(uhcPlayer.getName());
        }
    }

    private void updatePlayerOnColoredTab(UhcPlayer uhcPlayer, UhcPlayer scoreboardOwner) {
        Scoreboard scoreboard = scoreboardOwner.getScoreboard();
        if (scoreboard == null) {
            return;
        }

        // Add to no-team team
        if (uhcPlayer.getState() == PlayerState.DEAD) {
            Team team = scoreboard.getTeam("spectators");
            if (team != null) {
                team.addEntry(uhcPlayer.getName());
            }

            return;
        }

        Team team;
        if (scoreboardOwner.isInTeamWith(uhcPlayer)) {
            // Add to their team with 0 in front
            team = scoreboard.getTeam("0" + uhcPlayer.getTeam().getTeamNumber());
            if (team == null){
                team = scoreboard.registerNewTeam("0" + uhcPlayer.getTeam().getTeamNumber());
            }
        } else {
            // Add to normal team
            team = scoreboard.getTeam("" + uhcPlayer.getTeam().getTeamNumber());
            if (team == null){
                team = scoreboard.registerNewTeam("" + uhcPlayer.getTeam().getTeamNumber());

                if (config.get(MainConfig.DISABLE_ENEMY_NAMETAGS)){
                    VersionUtils.getVersionUtils().setTeamNameTagVisibility(team, false);
                }
            }
        }

        team.setPrefix(uhcPlayer.getTeam().getPrefix());
        team.setSuffix(ChatColor.RESET + "");
        team.addEntry(uhcPlayer.getName());
    }

    public void resetObjective(Scoreboard scoreboard, ScoreboardType scoreboardType) {
        Objective objective = scoreboard.getObjective("informations");

        if (objective != null) {
            objective.unregister();
        }

        objective = VersionUtils.getVersionUtils().registerObjective(scoreboard, "informations", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(scoreboardLayout.getTitle());

        int lines = scoreboardLayout.getLines(scoreboardType).size();

        for (int i = 0; i < lines; i++){
            Score score = objective.getScore(SCOREBOARD_LINES[i]);
            score.setScore(i);
        }
    }

    public void updatePlayerSidebar(UhcPlayer uhcPlayer, ScoreboardType scoreboardType) {
        ScoreboardManager scoreboardManager = gameManager.getScoreboardManager();

        Player player;

        try {
            player = uhcPlayer.getPlayer();
        }catch (UhcPlayerNotOnlineException ex) {
            throw new RuntimeException(ex);
        }

        int i = 0;
        for (String line : scoreboardLayout.getLines(scoreboardType)){

            String first = "";
            String second = "";

            if (!line.isEmpty()) {

                String translatedLine = scoreboardManager.translatePlaceholders(line, uhcPlayer, player, scoreboardType);

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

            Team lineTeam = uhcPlayer.getScoreboard().getTeam(SCOREBOARD_LINES[i]);

            if (!lineTeam.getPrefix().equals(first)){
                lineTeam.setPrefix(first);
            }
            if (!lineTeam.getSuffix().equals(second)){
                lineTeam.setSuffix(second);
            }

            i++;
        }
    }

    public ScoreboardType getPlayerScoreboardType(UhcPlayer uhcPlayer) {
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

}
