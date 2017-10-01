package com.gmail.val59000mc.playuhc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.val59000mc.playuhc.game.GameManager;

public class PlayUhc extends JavaPlugin{
	
	private static PlayUhc pl;
	
	
	public void onEnable(){
		pl = this;
	
		// Blocks players joins while loading the plugin
		Bukkit.getServer().setWhitelist(true);
		saveDefaultConfig();
		
		
		Bukkit.getScheduler().runTaskLater(this, new Runnable(){
			
			@Override
			public void run() {
				GameManager gameManager = new GameManager();
				gameManager.loadNewGame();
				
				// Unlock players joins and rely on UhcPlayerJoinListener
				Bukkit.getServer().setWhitelist(false);
			}
			
		}, 1);
		
		
	}
	
	public static PlayUhc getPlugin(){
		return pl;
	}
	
	public void onDisable(){
		Bukkit.getLogger().info("Plugin PlayUHC disabled");
	}
}
