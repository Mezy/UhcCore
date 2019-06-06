package com.gmail.val59000mc.mc1_13.scoreboard;

import com.gmail.val59000mc.mc1_13.players.UhcPlayer;
import org.bukkit.entity.Player;

public abstract class Placeholder {

    private String placeholder;

    public Placeholder(String placeholder){
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public abstract String getReplacement(UhcPlayer uhcPlayer, Player player, ScoreboardType scoreboardType);

}