package com.gmail.val59000mc.scoreboard;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScenariosPlaceholder extends Placeholder{

    private Map<UUID, Integer> lastShownScenario;

    public ScenariosPlaceholder(){
        super("%scenarios");
        lastShownScenario = new HashMap<>();
    }

    @Override
    public String getReplacement(UhcPlayer uhcPlayer, Player player, ScoreboardType scoreboardType){
        ScenarioManager scenarioManager = GameManager.getGameManager().getScenarioManager();

        if (scenarioManager.getActiveScenarios().isEmpty()){
            return "-";
        }

        Scenario[] activeScenarios = scenarioManager.getActiveScenarios().toArray(new Scenario[scenarioManager.getActiveScenarios().size()]);

        int showScenario = lastShownScenario.getOrDefault(player.getUniqueId(), -1) + 1;
        if (showScenario == activeScenarios.length){
            showScenario = 0;
        }
        lastShownScenario.put(player.getUniqueId(), showScenario);
        return activeScenarios[showScenario].getName();
    }

}