package com.gmail.val59000mc.game.handlers;

import com.gmail.val59000mc.Metrics;
import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.configuration.YamlFile;
import com.gmail.val59000mc.maploader.MapLoader;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioManager;
import com.gmail.val59000mc.utils.FileUtils;
import com.gmail.val59000mc.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsHandler {

    private final static String CHART_GAME_COUNT = "game_count";
    private final static String CHART_TEAM_SIZE = "team_size";
    private final static String CHART_NETHER = "nether";
    private final static String CHART_SCENARIOS = "scenarios";
    private final static String CHART_THE_END = "the_end";
    private final static String CHART_TEAM_COLORS = "team_colors";
    private final static String CHART_DEATHMATCH = "deathmatch";
    private final static String CHART_AUTO_UPDATE = "auto_update";
    private final static String CHART_REPLACE_OCEANS = "replace_oceans";
    private final static String CHART_GOLDEN_HEADS = "golden_heads";
    private final static String CHART_ALWAYS_READY = "always_ready";
    private final static String CHART_GOLD_DROPS = "gold_drops";

    private final static String VALUE_ENABLED = "enabled";
    private final static String VALUE_DISABLED = "disabled";

    private final JavaPlugin plugin;
    private final MainConfig config;
    private final MapLoader mapLoader;
    private final ScenarioManager scenarioManager;

    private Metrics bStats;

    public StatsHandler(JavaPlugin plugin, MainConfig config, MapLoader mapLoader, ScenarioManager scenarioManager) {
        this.plugin = plugin;
        this.config = config;
        this.mapLoader = mapLoader;
        this.scenarioManager = scenarioManager;
    }

    public void startRegisteringStats() {
        bStats = new Metrics(plugin);

        bStats.addCustomChart(new Metrics.SingleLineChart(CHART_GAME_COUNT, () -> {
            YamlFile storage = FileUtils.saveResourceIfNotAvailable(UhcCore.getPlugin(), "storage.yml");

            List<Long> games = storage.getLongList("games");
            List<Long> recentGames = new ArrayList<>();

            for (long game : games){
                if (game + TimeUtils.HOUR > System.currentTimeMillis()){
                    recentGames.add(game);
                }
            }

            storage.set("games", recentGames);
            storage.save();
            return recentGames.size();
        }));

        bStats.addCustomChart(
                new Metrics.SimplePie(
                        CHART_TEAM_SIZE,
                        () -> String.valueOf(config.get(MainConfig.MAX_PLAYERS_PER_TEAM))
                )
        );

        bStats.addCustomChart(
                new Metrics.SimplePie(
                        CHART_NETHER,
                        () -> (config.get(MainConfig.ENABLE_NETHER) ? VALUE_ENABLED : VALUE_DISABLED)
                )
        );

        bStats.addCustomChart(new Metrics.AdvancedPie(CHART_SCENARIOS, () -> {
            Map<String, Integer> scenarios = new HashMap<>();

            for (Scenario scenario : scenarioManager.getEnabledScenarios()){
                scenarios.put(scenario.getKey(), 1);
            }

            return scenarios;
        }));

        bStats.addCustomChart(
                new Metrics.SimplePie(
                        CHART_THE_END,
                        () -> (config.get(MainConfig.ENABLE_THE_END) ? VALUE_ENABLED : VALUE_DISABLED)
                )
        );

        bStats.addCustomChart(
                new Metrics.SimplePie(
                        CHART_TEAM_COLORS,
                        () -> (config.get(MainConfig.TEAM_COLORS) ? VALUE_ENABLED : VALUE_DISABLED)
                )
        );

        bStats.addCustomChart(new Metrics.SimplePie(CHART_DEATHMATCH, () -> {
            if (!config.get(MainConfig.ENABLE_DEATHMATCH)){
                return "No deathmatch";
            }

            if (mapLoader.getArena().isUsed()){
                return "Arena deathmatch";
            }

            return "Center deatchmatch";
        }));

        bStats.addCustomChart(
                new Metrics.SimplePie(
                        CHART_AUTO_UPDATE,
                        () -> (config.get(MainConfig.AUTO_UPDATE) ? VALUE_ENABLED : VALUE_DISABLED)
                )
        );

        bStats.addCustomChart(
                new Metrics.SimplePie(
                        CHART_REPLACE_OCEANS,
                        () -> (config.get(MainConfig.REPLACE_OCEAN_BIOMES) ? VALUE_ENABLED : VALUE_DISABLED)
                )
        );

        bStats.addCustomChart(
                new Metrics.SimplePie(
                        CHART_GOLDEN_HEADS, () -> {
                            boolean goldenHeads = config.get(MainConfig.ENABLE_GOLDEN_HEADS);
                            boolean regenHeads = config.get(MainConfig.REGEN_HEAD_DROP_ON_PLAYER_DEATH);

                            if (goldenHeads && regenHeads) {
                                return "Both";
                            }else if (goldenHeads) {
                                return "Golden Heads";
                            }else if (regenHeads) {
                                return "Regen Heads";
                            }else {
                                return "Neither";
                            }
                        }
                )
        );

        bStats.addCustomChart(
                new Metrics.SimplePie(
                        CHART_ALWAYS_READY,
                        () -> String.valueOf(config.get(MainConfig.TEAM_ALWAYS_READY))
                )
        );

        bStats.addCustomChart(
                new Metrics.SimplePie(
                        CHART_GOLD_DROPS,
                        () -> String.valueOf(config.get(MainConfig.ENABLE_GOLD_DROPS))
                )
        );
    }

    // This collects the amount of games started. They are stored anonymously by https://bstats.org/ (If enabled)
    public void addGameToStatistics() {
        if (bStats.isEnabled()){
            YamlFile storage;

            try{
                storage = FileUtils.saveResourceIfNotAvailable(UhcCore.getPlugin(), "storage.yml");
            }catch (InvalidConfigurationException ex){
                ex.printStackTrace();
                return;
            }

            List<Long> games = storage.getLongList("games");
            List<Long> recentGames = new ArrayList<>();

            for (long game : games){
                if (game + TimeUtils.HOUR > System.currentTimeMillis()){
                    recentGames.add(game);
                }
            }

            recentGames.add(System.currentTimeMillis());

            storage.set("games", recentGames);
            try {
                storage.save();
            }catch (IOException ex){
                Bukkit.getLogger().warning("[UhcCore] Failed to save storage.yml file!");
                ex.printStackTrace();
            }
        }
    }

}
