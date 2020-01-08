package com.gmail.val59000mc;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.utils.FileUtils;
import com.gmail.val59000mc.configuration.YamlFile;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class UhcCore extends JavaPlugin{

	private static final int MIN_VERSION = 8;
	private static final int MAX_VERSION = 16;

	private static UhcCore pl;
	private static int version;
	private boolean bStats;
	private Updater updater;

	@Override
	public void onEnable(){
		pl = this;

		loadServerVersion();
		addBStats();
		
		Bukkit.getScheduler().runTaskLater(this, new Runnable(){
			
			@Override
			public void run() {
				new GameManager().loadNewGame();
			}
			
		}, 1);

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

		metrics.addCustomChart(new Metrics.SingleLineChart("game_count", new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				YamlFile storage = FileUtils.saveResourceIfNotAvailable("storage.yml", true);

				List<Long> games = storage.getLongList("games");
				List<Long> recentGames = new ArrayList<>();

				for (long game : games){
					if (game + 1000*60*60 > System.currentTimeMillis()){
						recentGames.add(game);
					}
				}

				storage.set("games", recentGames);
				storage.save();
				return recentGames.size();
			}
		}));

		metrics.addCustomChart(new Metrics.SimplePie("team_size", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return String.valueOf(GameManager.getGameManager().getConfiguration().getMaxPlayersPerTeam());
			}
		}));

		metrics.addCustomChart(new Metrics.SimplePie("nether", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return (GameManager.getGameManager().getConfiguration().getBanNether() ? "disabled" : "enabled");
			}
		}));

		metrics.addCustomChart(new Metrics.AdvancedPie("scenarios", new Callable<Map<String, Integer>>() {
			@Override
			public Map<String, Integer> call() throws Exception {
				Map<String, Integer> scenarios = new HashMap<>();

				for (Scenario scenario : GameManager.getGameManager().getScenarioManager().getActiveScenarios()){
					scenarios.put(scenario.getName(), 1);
				}

				return scenarios;
			}
		}));

		metrics.addCustomChart(new Metrics.SimplePie("the_end", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return (GameManager.getGameManager().getConfiguration().getEnableTheEnd() ? "enabled" : "disabled");
			}
		}));

		metrics.addCustomChart(new Metrics.SimplePie("team_colors", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return (GameManager.getGameManager().getConfiguration().getUseTeamColors() ? "enabled" : "disabled");
			}
		}));

		metrics.addCustomChart(new Metrics.SimplePie("deathmatch", new Callable<String>() {
			@Override
			public String call() throws Exception {
				if (!GameManager.getGameManager().getConfiguration().getEnableTimeLimit()){
					return "No deathmatch";
				}

				if (GameManager.getGameManager().getArena().isUsed()){
					return "Arena deathmatch";
				}

				return "Center deatchmatch";
			}
		}));
	}

	// This collects the amount of games started. They are stored anonymously by https://bstats.org/ (If enabled)
	public void addGameToStatistics(){
		if (bStats){
			YamlFile storage = FileUtils.saveResourceIfNotAvailable("storage.yml");

			List<Long> games = storage.getLongList("games");
			List<Long> recentGames = new ArrayList<>();

			for (long game : games){
				if (game + 1000*60*60 > System.currentTimeMillis()){
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
		updater.runAutoUpdate();
		Bukkit.getLogger().info("[UhcCore] Plugin disabled");
	}

}