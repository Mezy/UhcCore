package com.gmail.val59000mc.scoreboard.placeholders;

import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scoreboard.Placeholder;
import com.gmail.val59000mc.scoreboard.ScoreboardType;
import com.gmail.val59000mc.utils.TimeUtils;
import org.bukkit.entity.Player;

public class TimersPlaceholder extends Placeholder{

    private enum Timer{
        PVP,
        DEATHMATCH,
        BORDER
    }

    public TimersPlaceholder(){
        super("%timers");
    }

    @Override
    public String getReplacement(UhcPlayer uhcPlayer, Player player, ScoreboardType scoreboardType) {
        String timerName = null;
        long timerTime = 0;

        for (Timer timer : Timer.values()){
            long l = getTimeRemaining(timer);
            if (timerName == null || l < timerTime){
                timerName = getTimerName(timer);
                timerTime = l;
            }
        }

        if (timerName == null){
            return "-";
        }

        return timerName + ": " + TimeUtils.getFormattedTime(timerTime);
    }

    private String getTimerName(Timer timer){
        switch (timer){
            case PVP:
                return "PvP";
            case DEATHMATCH:
                return "Deathmatch";
            case BORDER:
                return "Border";
        }
        return "?";
    }

    private long getTimeRemaining(Timer timer){
        GameManager gm = GameManager.getGameManager();
        MainConfiguration cfg = gm.getConfiguration();
        switch (timer){
            case PVP:
                return cfg.getTimeBeforePvp() - gm.getElapsedTime();
            case DEATHMATCH:
                return gm.getRemainingTime();
            case BORDER:
                return cfg.getBorderTimeBeforeShrink() - gm.getElapsedTime();
        }
        return -1;
    }

}