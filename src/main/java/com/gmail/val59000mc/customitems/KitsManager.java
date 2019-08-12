package com.gmail.val59000mc.customitems;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KitsManager {

	private static List<Kit> kits;
	
	public static boolean isAtLeastOneKit(){
		return (kits != null && kits.size() > 0); 
	}
	
	public static Kit getFirstKit(){
		if(isAtLeastOneKit()){
			return kits.get(0);
		}else{
			return null;
		}
	}
	
	
	public static void loadKits(){
		Bukkit.getLogger().info("[UhcCore] Start loading kits");

		FileConfiguration cfg = UhcCore.getPlugin().getConfig();
		Set<String> kitsKeys = cfg.getConfigurationSection("kits").getKeys(false);
		kits = new ArrayList<>();
		for(String kitKey : kitsKeys){
			try{
				Bukkit.getLogger().info("[UhcCore] Loading kit " + kitKey);

				Kit kit = new Kit();
				kit.key = kitKey;
				kit.name = cfg.getString("kits."+kitKey+".symbol.name");
				kit.items = new ArrayList<>();
				kit.symbol = new ItemStack(Material.valueOf(cfg.getString("kits."+kitKey+".symbol.item")));
				
				ItemMeta im = kit.symbol.getItemMeta();
				im.setDisplayName(ChatColor.GREEN+kit.name);				
				List<String> lore = new ArrayList<String>();
				
				for(String itemStr : cfg.getStringList("kits."+kitKey+".items")){
					String[] itemStrArr = itemStr.split(" ");
					if(itemStrArr.length != 2)
						throw new IllegalArgumentException("Correct usage: AMOUNT ITEM (" + itemStr + ")");
					
					int amount = Integer.parseInt(itemStrArr[0]);
					ItemStack item = new ItemStack(Material.valueOf(itemStrArr[1]),amount);
					kit.items.add(item);
					lore.add(ChatColor.WHITE+""+amount+" x "+Material.valueOf(itemStrArr[1]).toString().toLowerCase());
				}
				
				im.setLore(lore);
				kit.symbol.setItemMeta(im);
				
				kits.add(kit);

				Bukkit.getLogger().info("[UhcCore] Added kit " + kitKey);
			}catch(IllegalArgumentException e){
				Bukkit.getLogger().warning("[UhcCore] Kit "+kitKey+" was disabled because of an error of syntax.");
				System.out.println(e.getMessage());
			}
		}

		Bukkit.getLogger().info("[UhcCore] Loaded " + kits.size() + " kits");
	}
	
	
	public static void openKitSelectionInventory(Player player){
		int maxSlots = 6*9;
		Inventory inv = Bukkit.createInventory(null, maxSlots, ChatColor.GREEN+Lang.DISPLAY_MESSAGE_PREFIX+" "+ChatColor.DARK_GREEN+Lang.ITEMS_KIT_INVENTORY);
		int slot = 0;
		for(Kit kit : kits){
			if(slot < maxSlots){
				inv.setItem(slot, kit.symbol);
				slot++;
			}
		}
		
		player.openInventory(inv);
	}
	
	public static void giveKitTo(Player player) {
		try {
			UhcPlayer uhcPlayer = GameManager.getGameManager().getPlayersManager().getUhcPlayer(player);
			if(uhcPlayer.getKit() == null){
				uhcPlayer.setKit(KitsManager.getFirstKit());
			}
			
			if(uhcPlayer.getKit() != null && isAtLeastOneKit()){
				for(ItemStack item : uhcPlayer.getKit().items){
					player.getInventory().addItem(item);
				}
			}
		} catch (UhcPlayerDoesntExistException e) {
		}
	}
	
	
	public static boolean isKitItem(ItemStack item) {
		if(item == null || item.getType().equals(Material.AIR))
			return false;
		
		for(Kit kit : kits){
			if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN+kit.name))
				return true;
		}
		return false;
	}
	public static Kit getKitByName(String displayName) {
		for(Kit kit : kits){
			if(kit.symbol.getItemMeta().getDisplayName().equals(displayName))
				return kit;
		}
		return null;
	}
}
