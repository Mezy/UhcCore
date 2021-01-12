package com.gmail.val59000mc.scoreboard;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.configuration.VaultManager;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.*;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.scenariolisteners.SilentNightListener;
import com.gmail.val59000mc.scoreboard.placeholders.BlocksToTeamLeader;
import com.gmail.val59000mc.scoreboard.placeholders.ScenariosPlaceholder;
import com.gmail.val59000mc.scoreboard.placeholders.TeamMembersPlaceholder;
import com.gmail.val59000mc.scoreboard.placeholders.TimersPlaceholder;
import com.gmail.val59000mc.threads.UpdateScoreboardThread;
import com.gmail.val59000mc.utils.TimeUtils;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardManager {

    private final ScoreboardLayout scoreboardLayout;
    private final List<Placeholder> placeholders;

    public ScoreboardManager(){
        scoreboardLayout = new ScoreboardLayout();
        scoreboardLayout.loadFile();
        placeholders = new ArrayList<>();
        placeholders.add(new BlocksToTeamLeader());
        placeholders.add(new TeamMembersPlaceholder());
        placeholders.add(new ScenariosPlaceholder());
        placeholders.add(new TimersPlaceholder());
    }

    public ScoreboardLayout getScoreboardLayout() {
        return scoreboardLayout;
    }

    public void setUpPlayerScoreboard(UhcPlayer scoreboardPlayer){
        GameManager gm = GameManager.getGameManager();
        PlayerManager pm = gm.getPlayerManager();
        MainConfig cfg = gm.getConfig();

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        scoreboardPlayer.setScoreboard(scoreboard);

        if (cfg.get(MainConfig.HEARTS_ON_TAB)) {
            Objective health = VersionUtils.getVersionUtils().registerObjective(scoreboard, "health_tab", "health");
            health.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }

        if (cfg.get(MainConfig.HEARTS_BELOW_NAME)) {
            Objective health = VersionUtils.getVersionUtils().registerObjective(scoreboard, ChatColor.RED + "\u2764", "health");
            health.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

        try {
            scoreboardPlayer.getPlayer().setScoreboard(scoreboard);
        } catch (UhcPlayerNotOnlineException e) {
            // No scoreboard for offline players
        }


        // add teams for no flicker scoreboard
        for (int i = 0; i < 15; i++){
            Team team = scoreboard.registerNewTeam(getScoreboardLine(i));
            team.addEntry(getScoreboardLine(i));
        }

        // setup teams
        if (!cfg.get(MainConfig.TEAM_COLORS)){

            Objective healthTab = scoreboard.getObjective("health_tab");
            Objective healthBelowName = scoreboard.getObjective(ChatColor.RED + "\u2764");

            Team friends = scoreboard.registerNewTeam("friends");
            Team enemies = scoreboard.registerNewTeam("enemies");
            friends.setPrefix(ChatColor.GREEN + "");
            enemies.setPrefix(ChatColor.RED + "");
            friends.setSuffix(ChatColor.RESET + "");
            enemies.setSuffix(ChatColor.RESET + "");

            if (cfg.get(MainConfig.DISABLE_ENEMY_NAMETAGS)){
                VersionUtils.getVersionUtils().setTeamNameTagVisibility(enemies, false);
            }

            Team spectators = scoreboard.registerNewTeam("spectators");
            spectators.setPrefix(ChatColor.GRAY + "");
            spectators.setSuffix(ChatColor.RESET + "");

            // Putting players in colored teams
            for (UhcPlayer uhcPlayer : pm.getPlayersList()) {

                try {
                    if (healthTab != null) {
                        healthTab.getScore(uhcPlayer.getName()).setScore((int) uhcPlayer.getPlayer().getHealth());
                    }
                    if (healthBelowName != null) {
                        healthBelowName.getScore(uhcPlayer.getName()).setScore((int) uhcPlayer.getPlayer().getHealth());
                    }
                } catch (UhcPlayerNotOnlineException ex) {
                    // No health display for offline players.
                }

                if (uhcPlayer.getState().equals(PlayerState.DEAD) || uhcPlayer.getState().equals(PlayerState.WAITING)){
                    spectators.addEntry(uhcPlayer.getName());
                }else if (uhcPlayer.isInTeamWith(scoreboardPlayer)) {
                    friends.addEntry(uhcPlayer.getName());
                }else {
                    enemies.addEntry(uhcPlayer.getName());
                }

            }

            updatePlayerTab(scoreboardPlayer);

        }else {

            // Team colors
            Objective healthTab = scoreboard.getObjective("health_tab");
            Objective healthBelowName = scoreboard.getObjective(ChatColor.RED + "\u2764");

            Team spectators = scoreboard.registerNewTeam("spectators");
            spectators.setPrefix(ChatColor.GRAY + "");
            spectators.setSuffix(ChatColor.RESET + "");

            for (UhcTeam uhcTeam : gm.getTeamManager().getUhcTeams()) {

                if (uhcTeam.contains(scoreboardPlayer)) {

                    Team team = scoreboard.registerNewTeam("0" + uhcTeam.getTeamNumber());
                    team.setPrefix(uhcTeam.getPrefix());
                    team.setSuffix(ChatColor.RESET + "");

                    for (UhcPlayer member : uhcTeam.getMembers()) {

                        try {
                            if (healthTab != null) {
                                healthTab.getScore(member.getName()).setScore((int) member.getPlayer().getHealth());
                            }
                            if (healthBelowName != null) {
                                healthBelowName.getScore(member.getName()).setScore((int) member.getPlayer().getHealth());
                            }
                        } catch (UhcPlayerNotOnlineException ex) {
                            // No health display for offline players.
                        }

                        if (member.getState().equals(PlayerState.DEAD)) {
                            // spec team
                            spectators.addEntry(member.getName());
                        } else {
                            team.addEntry(member.getName());
                        }
                    }

                }else{

                    Team team = scoreboard.registerNewTeam("" + uhcTeam.getTeamNumber());
                    team.setPrefix(uhcTeam.getPrefix());
                    team.setSuffix(ChatColor.RESET + "");

                    if (gm.getConfig().get(MainConfig.DISABLE_ENEMY_NAMETAGS)){
                        VersionUtils.getVersionUtils().setTeamNameTagVisibility(team, false);
                    }

                    for (UhcPlayer member : uhcTeam.getMembers()) {

                        try {
                            if (healthTab != null) {
                                healthTab.getScore(member.getName()).setScore((int) member.getPlayer().getHealth());
                            }
                            if (healthBelowName != null) {
                                healthBelowName.getScore(member.getName()).setScore((int) member.getPlayer().getHealth());
                            }
                        } catch (UhcPlayerNotOnlineException ex) {
                            // No health display for offline players.
                        }

                        if (member.getState().equals(PlayerState.DEAD)) {
                            // spec team
                            spectators.addEntry(member.getName());
                        } else {
                            team.addEntry(member.getName());
                        }
                    }
                }
            }

            updatePlayerTab(scoreboardPlayer);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(),new UpdateScoreboardThread(gm, scoreboardPlayer),1L);
    }

    public void updatePlayerTab(UhcPlayer uhcPlayer){
        GameManager gm = GameManager.getGameManager();

        if (!gm.getConfig().get(MainConfig.TEAM_COLORS)) {

            for (UhcPlayer all : gm.getPlayerManager().getPlayersList()) {
                Scoreboard scoreboard = all.getScoreboard();
                if (scoreboard == null){
                    continue;
                }

                if (uhcPlayer.getState().equals(PlayerState.PLAYING)) {
                    if (all.isInTeamWith(uhcPlayer)) {
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

        }else {

            for (UhcPlayer all : gm.getPlayerManager().getPlayersList()){
                Scoreboard scoreboard = all.getScoreboard();
                if (scoreboard == null){
                    continue;
                }

                if (uhcPlayer.getState().equals(PlayerState.PLAYING) || uhcPlayer.getState().equals(PlayerState.WAITING)) {

                    if (all.isInTeamWith(uhcPlayer)) {
                        // add to there team with 0 in front

                        Team team = scoreboard.getTeam("0" + uhcPlayer.getTeam().getTeamNumber());
                        if (team == null){
                            team = scoreboard.registerNewTeam("0" + uhcPlayer.getTeam().getTeamNumber());
                        }
                        team.setPrefix(uhcPlayer.getTeam().getPrefix());
                        team.setSuffix(ChatColor.RESET + "");
                        team.addEntry(uhcPlayer.getName());

                    } else {
                        // add to normal team

                        Team team = scoreboard.getTeam("" + uhcPlayer.getTeam().getTeamNumber());
                        if (team == null){
                            team = scoreboard.registerNewTeam("" + uhcPlayer.getTeam().getTeamNumber());

                            if (gm.getConfig().get(MainConfig.DISABLE_ENEMY_NAMETAGS)){
                                VersionUtils.getVersionUtils().setTeamNameTagVisibility(team, false);
                            }
                        }
                        team.setPrefix(uhcPlayer.getTeam().getPrefix());
                        team.setSuffix(ChatColor.RESET + "");
                        team.addEntry(uhcPlayer.getName());
                    }

                } else {
                    // add to no-team team
                    Team team = scoreboard.getTeam("spectators");
                    if (team != null) {
                        team.addEntry(uhcPlayer.getName());
                    }
                }

            }

            // Change player display name
            if (gm.getConfig().get(MainConfig.CHANGE_DISPLAY_NAMES)){
                try {
                    uhcPlayer.getPlayer().setDisplayName(uhcPlayer.getDisplayName());
                }catch (UhcPlayerNotOnlineException ex){
                    // Player left while updating tab.
                }
            }
        }
    }

    public String getScoreboardLine(int line){
        if (line == 0) return ChatColor.UNDERLINE + "" + ChatColor.RESET;
        if (line == 1) return ChatColor.ITALIC + "" + ChatColor.RESET;
        if (line == 2) return ChatColor.BOLD + "" + ChatColor.RESET;
        if (line == 3) return ChatColor.RESET + "" + ChatColor.RESET;
        if (line == 4) return ChatColor.GREEN + "" + ChatColor.RESET;
        if (line == 5) return ChatColor.DARK_GRAY + "" + ChatColor.RESET;
        if (line == 6) return ChatColor.GOLD + "" + ChatColor.RESET;
        if (line == 7) return ChatColor.RED + "" + ChatColor.RESET;
        if (line == 8) return ChatColor.YELLOW + "" + ChatColor.RESET;
        if (line == 9) return ChatColor.WHITE + "" + ChatColor.RESET;
        if (line == 10) return ChatColor.DARK_GREEN + "" + ChatColor.RESET;
        if (line == 11) return ChatColor.BLUE + "" + ChatColor.RESET;
        if (line == 12) return ChatColor.STRIKETHROUGH + "" + ChatColor.RESET;
        if (line == 13) return ChatColor.MAGIC + "" + ChatColor.RESET;
        if (line == 14) return ChatColor.DARK_RED + "" + ChatColor.RESET;
        return null;
    }

    public String translatePlaceholders(String s, UhcPlayer uhcPlayer, Player bukkitPlayer, ScoreboardType scoreboardType){

        String returnString = s;
        GameManager gm = GameManager.getGameManager();
        MainConfig cfg = gm.getConfig();

        if (scoreboardType.equals(ScoreboardType.WAITING)){
            returnString = returnString.replace("%online%",Bukkit.getOnlinePlayers().size() + "").replace("%needed%",cfg.get(MainConfig.MIN_PLAYERS_TO_START) + "");
        }

        if (returnString.contains("%kit%")){
            if (uhcPlayer.hasKitSelected()){
                returnString = returnString.replace("%kit%", uhcPlayer.getKit().getName());
            }else{
                returnString = returnString.replace("%kit%", Lang.ITEMS_KIT_SCOREBOARD_NO_KIT);
            }
        }

        if (returnString.contains("%kills%")){
            returnString = returnString.replace("%kills%",uhcPlayer.getKills() + "");
        }

        if (returnString.contains("%teamKills%")){
            returnString = returnString.replace("%teamKills%",uhcPlayer.getTeam().getKills() + "");
        }

        if (returnString.contains("%teamColor%")){
            returnString = returnString.replace("%teamColor%",uhcPlayer.getTeam().getPrefix());
        }

        if (returnString.contains("%border%")){

            int size = (int) bukkitPlayer.getWorld().getWorldBorder().getSize() / 2;

            if (size == 30000000){
                size = 0;
            }

            String borderString = "+" + size + " -" + size;

            int playerX = bukkitPlayer.getLocation().getBlockX();
            int playerZ = bukkitPlayer.getLocation().getBlockZ();

            if (playerX < 0) playerX *= -1;
            if (playerZ < 0) playerZ *= -1;

            int distanceX = size - playerX;
            int distanceZ = size - playerZ;

            if (distanceX <= 5 || distanceZ <= 5){
                borderString = ChatColor.RED + borderString;
            }else if (distanceX <= 50 || distanceZ <= 50){
                borderString = ChatColor.YELLOW + borderString;
            }else {
                borderString = ChatColor.GREEN + borderString;
            }

            returnString = returnString.replace("%border%",borderString);
        }

        if (returnString.contains("%ylayer%")){
            returnString = returnString.replace("%ylayer%",(int) bukkitPlayer.getLocation().getY() + "");
        }

        if (returnString.contains("%xCoordinate%")){
            returnString = returnString.replace("%xCoordinate%",(int) bukkitPlayer.getLocation().getX() + "");
        }

        if (returnString.contains("%zCoordinate%")){
            returnString = returnString.replace("%zCoordinate%",(int) bukkitPlayer.getLocation().getZ() + "");
        }

        if (returnString.contains("%deathmatch%")){
            returnString = returnString.replace("%deathmatch%", gm.getFormattedRemainingTime());
        }

        if (returnString.contains("%time%")){
            returnString = returnString.replace("%time%", TimeUtils.getFormattedTime(gm.getElapsedTime()));
        }

        if (returnString.contains("%pvp%")){
            long pvp = cfg.get(MainConfig.TIME_BEFORE_PVP) - gm.getElapsedTime();

            if (pvp < 0){
                returnString = returnString.replace("%pvp%", "-");
            }else {
                returnString = returnString.replace("%pvp%", TimeUtils.getFormattedTime(pvp));
            }
        }

        if (returnString.contains("%alive%")){
            if (
                    gm.getScenarioManager().isEnabled(Scenario.SILENT_NIGHT) &&
                    ((SilentNightListener) gm.getScenarioManager().getScenarioListener(Scenario.SILENT_NIGHT)).isNightMode()
            ){
                returnString = returnString.replace("%alive%","?");
            }else{
                returnString = returnString.replace("%alive%",gm.getPlayerManager().getOnlinePlayingPlayers().size() + "");
            }
        }

        if (returnString.contains("%episode%")){
            returnString = returnString.replace("%episode%",gm.getEpisodeNumber() + "");
        }

        if (returnString.contains("%nextEpisode%")){
            returnString = returnString.replace("%nextEpisode%", TimeUtils.getFormattedTime(gm.getTimeUntilNextEpisode()) + "");
        }

        if (returnString.contains("%teamAlive%")){
            returnString = returnString.replace("%teamAlive%", String.valueOf(gm.getTeamManager().getPlayingUhcTeams().size()));
        }

        if (returnString.contains("%playerAlive%")){
            returnString = returnString.replace("%playerAlive%", String.valueOf(gm.getPlayerManager().getAllPlayingPlayers().size()));
        }

        if (returnString.contains("%playerSpectator%")){
            long count = gm.getPlayerManager().getPlayersList()
                    .stream()
                    .filter(UhcPlayer::isDeath)
                    .filter(UhcPlayer::isOnline)
                    .count();
            returnString = returnString.replace("%playerSpectator%", String.valueOf(count));
        }

        if (returnString.contains("%money%")){
            returnString = returnString.replace("%money%", String.format("%.2f", VaultManager.getPlayerMoney(bukkitPlayer)));
        }

        // Parse custom placeholders
        for (Placeholder placeholder : placeholders){
            returnString = placeholder.parseString(returnString, uhcPlayer, bukkitPlayer, scoreboardType);
        }

        if (returnString.length() > 32){
            Bukkit.getLogger().warning("[UhcCore] Scoreboard line is too long: '" + returnString + "'!");
            returnString = "";
        }

        return returnString;
    }

    /**
     * Used to register custom placeholders.
     * @param placeholder The placeholder you want to register.
     */
    public void registerPlaceholder(Placeholder placeholder){
        placeholders.add(placeholder);
    }

}