package com.gmail.val59000mc.scenarios;

import com.gmail.val59000mc.game.GameManager;
import org.bukkit.event.Listener;

public abstract class ScenarioListener implements Listener{

    private Scenario scenario;

    public ScenarioListener(Scenario scenario){
        this.scenario = scenario;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public ScenarioManager getScenarioManager(){
        return GameManager.getGameManager().getScenarioManager();
    }

    public boolean isActivated(Scenario scenario){
        return getScenarioManager().isActivated(scenario);
    }

    public boolean isActivated(){
        return getScenarioManager().isActivated(scenario);
    }

    public void onEnable(){}

    public void onDisable(){}

}