package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;

public class AnonymousListener extends ScenarioListener{

    public AnonymousListener(){
        super(Scenario.ANONYMOUS);
    }

    @Override
    public void onEnable(){
        Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new Runnable() {
            @Override
            public void run() {
                GameManager gm = GameManager.getGameManager();
                ScoreboardManager sm = gm.getScoreboardManager();
                for (UhcPlayer uhcPlayer : GameManager.getGameManager().getPlayersManager().getPlayersList()){
                    sm.updatePlayerTab(uhcPlayer);
                }
            }
        }, 1);
    }

    @Override
    public void onDisable(){
        Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new Runnable() {
            @Override
            public void run() {
                GameManager gm = GameManager.getGameManager();
                ScoreboardManager sm = gm.getScoreboardManager();
                for (UhcPlayer uhcPlayer : GameManager.getGameManager().getPlayersManager().getPlayersList()){
                    sm.updatePlayerTab(uhcPlayer);
                }
            }
        }, 1);
    }

}