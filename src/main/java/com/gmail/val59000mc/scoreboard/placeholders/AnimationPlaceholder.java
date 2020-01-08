package com.gmail.val59000mc.scoreboard.placeholders;

import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scoreboard.Placeholder;
import com.gmail.val59000mc.scoreboard.ScoreboardType;
import com.gmail.val59000mc.utils.TimeUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class AnimationPlaceholder extends Placeholder{

    private long lastChange;
    private int speed;
    private int currentStage;
    private List<String> stages;

    public AnimationPlaceholder(String name, int speed, List<String> stages){
        super(name);
        lastChange = 0;
        this.speed = speed;
        currentStage = 0;
        this.stages = stages;
    }

    @Override
    public String getReplacement(UhcPlayer uhcPlayer, Player player, ScoreboardType scoreboardType, String placeholder){
        if (lastChange < System.currentTimeMillis() + speed*TimeUtils.SECOND){
            lastChange = System.currentTimeMillis();
            currentStage++;
            if (currentStage == stages.size()){
                currentStage = 0;
            }
        }

        return stages.get(currentStage);
    }

}