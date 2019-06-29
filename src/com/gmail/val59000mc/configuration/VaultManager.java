package com.gmail.val59000mc.configuration;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultManager {
	
    private static Economy economy = null;
    
    public static void setupEconomy(){
    	if(GameManager.getGameManager().getConfiguration().getVaultLoaded()){
    		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	        if (economyProvider != null) {
	            economy = economyProvider.getProvider();
	        }else{
	        	Bukkit.getLogger().severe("Error trying to load economy provider. Check that you have a economy plugin installed");
	        }
    	}
    }

    public static double getPlayerMoney(Player player){
    	return economy == null ? 0 : economy.getBalance(player);
	}
	
	public static void addMoney(Player player, final Double amount){
		if(GameManager.getGameManager().getConfiguration().getVaultLoaded()){
			if(economy == null){
				Bukkit.getLogger().warning("[UhcCore] Vault is not loaded ! Couldnt pay "+amount+" to "+player.getName()+" !");
			}else{
				
				final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
				if(offlinePlayer != null){
					Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new Runnable(){

						@Override
						public void run() {
							economy.depositPlayer(offlinePlayer, amount);
						}
						
					});
				}
				
				
			}
		}
	}
	

}
