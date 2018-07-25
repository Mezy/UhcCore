package com.gmail.val59000mc.playuhc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayUhc extends JavaPlugin{
	
	private static PlayUhc pl;
	private static int version = 0;

	@Override
	public void onEnable(){
		pl = this;
	
		// Blocks players joins while loading the plugin
		Bukkit.getServer().setWhitelist(true);
		saveDefaultConfig();

		loadServerVersion();
		addBStats();
		
		Bukkit.getScheduler().runTaskLater(this, new Runnable(){
			
			@Override
			public void run() {

			    if (version < 13) {
                    new com.gmail.val59000mc.playuhc.mc1_8.game.GameManager().loadNewGame();
                }else {
                    new com.gmail.val59000mc.playuhc.mc1_13.game.GameManager().loadNewGame();
                }
				
				// Unlock players joins and rely on UhcPlayerJoinListener
				Bukkit.getServer().setWhitelist(false);
			}
			
		}, 1);
		
		
	}

	private void loadServerVersion(){
		// get minecraft version
		String versionString = Bukkit.getBukkitVersion();
        int maxVersion = 15;

		for (int i = 8; i <= maxVersion; i ++){
			if (versionString.contains("1." + i)){
				version = i;
			}
		}

		if (version == 0) {
			version = 8;
			Bukkit.getLogger().warning("[PlayUHC] Failed to detect server version! " + versionString + "?");
		}else {
			Bukkit.getLogger().info("[PlayUHC] 1." + version + " Server detected!");
		}
	}

	private void addBStats(){
		new Metrics(this);
	}

	public static int getVersion() {
		return version;
	}
	
	public static PlayUhc getPlugin(){
		return pl;
	}

	@Override
	public void onDisable(){
		Bukkit.getLogger().info("Plugin PlayUHC disabled");
	}

}
