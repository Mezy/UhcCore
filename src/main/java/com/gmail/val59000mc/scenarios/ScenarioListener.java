package com.gmail.val59000mc.scenarios;

import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.TeamManager;
import com.gmail.val59000mc.scoreboard.ScoreboardManager;
import org.bukkit.event.Listener;

/**
 * Used as event listener for a scenario (automatically registered).
 * In here you should handle anything related to the scenario.
 * See {@link #onEnable()} & {@link #onDisable()}
 */
public abstract class ScenarioListener implements Listener {

    public GameManager getGameManager(){
        return GameManager.getGameManager();
    }

    public PlayersManager getPlayersManager(){
        return getGameManager().getPlayersManager();
    }

    public TeamManager getTeamManager(){
        return getGameManager().getTeamManager();
    }

    public ScoreboardManager getScoreboardManager(){
        return getGameManager().getScoreboardManager();
    }

    public ScenarioManager getScenarioManager(){
        return getGameManager().getScenarioManager();
    }

    public MainConfig getConfiguration(){
        return getGameManager().getConfig();
    }

    /**
     * Used to check if a scenario is enabled.
     * @param scenario Scenario to check.
     * @return Returns true if the scenario is enabled.
     */
    public boolean isEnabled(Scenario scenario) {
        return getScenarioManager().isEnabled(scenario);
    }

    /**
     * Gets called when the scenario is enabled.
     */
    public void onEnable() {}

    /**
     * Gets called when the scenario is disabled.
     */
    public void onDisable() {}

}