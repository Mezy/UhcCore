package com.gmail.val59000mc.scoreboard;

import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;

public abstract class Placeholder{

    private String placeholder;

    /**
     * Placeholders are used to get your custom data on the scoreboard.
     * @param placeholder placeholder without %
     */
    public Placeholder(String placeholder){
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return "%"+placeholder+"%";
    }

    public abstract String getReplacement(UhcPlayer uhcPlayer, Player player, ScoreboardType scoreboardType);

}