package com.gmail.val59000mc;

import com.gmail.val59000mc.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class UhcCore extends JavaPlugin{
	
	private static UhcCore pl;
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

				new GameManager().loadNewGame();
				
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
			Bukkit.getLogger().warning("[UHC Core] Failed to detect server version! " + versionString + "?");
		}else {
			Bukkit.getLogger().info("[UHC Core] 1." + version + " Server detected!");
		}
	}

	private void addBStats(){
		new Metrics(this);
	}

	public static int getVersion() {
		return version;
	}
	
	public static UhcCore getPlugin(){
		return pl;
	}

	@Override
	public void onDisable(){
		Bukkit.getLogger().info("Plugin UhcCore disabled");
	}

}