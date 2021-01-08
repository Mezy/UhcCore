package com.gmail.val59000mc;

import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.utils.FileUtils;
import com.gmail.val59000mc.configuration.YamlFile;
import com.gmail.val59000mc.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UhcCore extends JavaPlugin{

	private static final int MIN_VERSION = 8;
	private static final int MAX_VERSION = 17;

	private static UhcCore pl;
	private static int version;
	private boolean bStats;
	private GameManager gameManager;
	private Updater updater;

	@Override
	public void onEnable(){
		pl = this;

		loadServerVersion();
		addBStats();

		gameManager = new GameManager();
		Bukkit.getScheduler().runTaskLater(this, () -> gameManager.loadNewGame(), 1);

		updater = new Updater(this);

		// Delete files that are scheduled for deletion
		FileUtils.removeScheduledDeletionFiles();
	}

	// Load the Minecraft version.
	private void loadServerVersion(){
		String versionString = Bukkit.getBukkitVersion();
		version = 0;

		for (int i = MIN_VERSION; i <= MAX_VERSION; i ++){
			if (versionString.contains("1." + i)){
				version = i;
			}
		}

		if (version == 0) {
			version = MIN_VERSION;
			Bukkit.getLogger().warning("[UhcCore] Failed to detect server version! " + versionString + "?");
		}else {
			Bukkit.getLogger().info("[UhcCore] 1." + version + " Server detected!");
		}
	}

	private void addBStats(){
		Metrics metrics = new Metrics(this);
		bStats = metrics.isEnabled();

		metrics.addCustomChart(new Metrics.SingleLineChart("game_count", () -> {
			YamlFile storage = FileUtils.saveResourceIfNotAvailable("storage.yml", true);

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

		metrics.addCustomChart(new Metrics.SimplePie("team_size", () -> String.valueOf(gameManager.getConfig().get(MainConfig.MAX_PLAYERS_PER_TEAM))));

		metrics.addCustomChart(new Metrics.SimplePie("nether", () -> (gameManager.getConfig().get(MainConfig.ENABLE_NETHER) ? "enabled" : "disabled")));

		metrics.addCustomChart(new Metrics.AdvancedPie("scenarios", () -> {
			Map<String, Integer> scenarios = new HashMap<>();

			for (Scenario scenario : gameManager.getScenarioManager().getActiveScenarios()){
				scenarios.put(scenario.getInfo().getName(), 1);
			}

			return scenarios;
		}));

		metrics.addCustomChart(new Metrics.SimplePie("the_end", () -> (gameManager.getConfig().get(MainConfig.ENABLE_THE_END) ? "enabled" : "disabled")));

		metrics.addCustomChart(new Metrics.SimplePie("team_colors", () -> (gameManager.getConfig().get(MainConfig.TEAM_COLORS) ? "enabled" : "disabled")));

		metrics.addCustomChart(new Metrics.SimplePie("deathmatch", () -> {
			if (!gameManager.getConfig().get(MainConfig.ENABLE_TIME_LIMIT)){
				return "No deathmatch";
			}

			if (gameManager.getArena().isUsed()){
				return "Arena deathmatch";
			}

			return "Center deatchmatch";
		}));

		metrics.addCustomChart(new Metrics.SimplePie("auto_update", () -> (gameManager.getConfig().get(MainConfig.AUTO_UPDATE) ? "enabled" : "disabled")));

		metrics.addCustomChart(new Metrics.SimplePie("replace_oceans", () -> (gameManager.getConfig().get(MainConfig.REPLACE_OCEAN_BIOMES) ? "enabled" : "disabled")));
	}

	// This collects the amount of games started. They are stored anonymously by https://bstats.org/ (If enabled)
	public void addGameToStatistics(){
		if (bStats){
			YamlFile storage;

			try{
				storage = FileUtils.saveResourceIfNotAvailable("storage.yml");
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

	public static int getVersion() {
		return version;
	}
	
	public static UhcCore getPlugin(){
		return pl;
	}

	@Override
	public void onDisable(){
		gameManager.getScenarioManager().disableAllScenarios();
		
		updater.runAutoUpdate();
		Bukkit.getLogger().info("[UhcCore] Plugin disabled");
	}

}
