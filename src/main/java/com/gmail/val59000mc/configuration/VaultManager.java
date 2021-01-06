package com.gmail.val59000mc.configuration;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultManager {
	
    private static Economy economy;

    static{
    	economy = null;
	}
    
    public static void setupEconomy(){
    	if(!Dependencies.getVaultLoaded()){
    		return;
		}

    	RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
	    if (economyProvider != null) {
	        economy = economyProvider.getProvider();
	    }else{
	    	Bukkit.getLogger().severe("[UhcCore] Error trying to load economy provider. Check that you have a economy plugin installed");
	    }
    }

    public static double getPlayerMoney(Player player){
		Validate.notNull(player);
    	return economy == null ? 0 : economy.getBalance(player);
	}

	public static void addMoney(final Player player, final double amount){
		Validate.notNull(player);

		if(!Dependencies.getVaultLoaded()){
			return;
		}

		if(economy == null){
			Bukkit.getLogger().warning("[UhcCore] Vault is not loaded! Couldn't pay "+amount+" to "+player.getName()+"!");
			return;
		}

		final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());

		Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), () -> economy.depositPlayer(offlinePlayer, amount));
	}

	public static void removeMoney(final Player player, final double amount){
		Validate.notNull(player);

		if(!Dependencies.getVaultLoaded()){
			return;
		}

		if(economy == null){
			Bukkit.getLogger().warning("[UhcCore] Vault is not loaded! Couldn't withdraw "+amount+" to "+player.getName()+"!");
			return;
		}

		Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), () -> economy.withdrawPlayer(player, amount));
	}

}