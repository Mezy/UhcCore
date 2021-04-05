package com.gmail.val59000mc.scoreboard;

import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.configuration.VaultManager;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.handlers.ScoreboardHandler;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.*;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.scenariolisteners.SilentNightListener;
import com.gmail.val59000mc.scoreboard.placeholders.BlocksToTeamLeader;
import com.gmail.val59000mc.scoreboard.placeholders.ScenariosPlaceholder;
import com.gmail.val59000mc.scoreboard.placeholders.TeamMembersPlaceholder;
import com.gmail.val59000mc.scoreboard.placeholders.TimersPlaceholder;
import com.gmail.val59000mc.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardManager {

    private final ScoreboardHandler scoreboardHandler;
    private final ScoreboardLayout scoreboardLayout;
    private final List<Placeholder> placeholders;

    public ScoreboardManager(ScoreboardHandler scoreboardHandler, ScoreboardLayout scoreboardLayout) {
        this.scoreboardHandler = scoreboardHandler;
        this.scoreboardLayout = scoreboardLayout;

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

    public void updatePlayerOnTab(UhcPlayer uhcPlayer) {
        scoreboardHandler.updatePlayerOnTab(uhcPlayer);
    }
}