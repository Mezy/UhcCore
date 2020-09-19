package com.gmail.val59000mc.customitems;

import com.gmail.val59000mc.configuration.YamlFile;
import com.gmail.val59000mc.exceptions.ParseException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.utils.FileUtils;
import com.gmail.val59000mc.utils.JsonItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KitsManager{

	private final static List<Kit> kits;

	static{
		kits = new ArrayList<>();
	}
	
	public static boolean isAtLeastOneKit(){
		return (kits != null && kits.size() > 0); 
	}

	public static Kit getFirstKitFor(Player player){
		for (Kit kit : kits){
			if (kit.canBeUsedBy(player, GameManager.getGameManager().getConfiguration())){
				return kit;
			}
		}
		return null;
	}
	
	public static void loadKits(){
		Bukkit.getLogger().info("[UhcCore] Start loading kits");

		YamlFile cfg;

		try{
			cfg = FileUtils.saveResourceIfNotAvailable("kits.yml");
		}catch (InvalidConfigurationException ex){
			ex.printStackTrace();
			return;
		}

		ConfigurationSection kitsSection = cfg.getConfigurationSection("kits");

		kits.clear();

		if (kitsSection == null){
			Bukkit.getLogger().info("[UhcCore] Loaded 0 kits");
			return;
		}

		Set<String> kitsKeys = kitsSection.getKeys(false);
		for(String kitKey : kitsKeys){

			try{
				Bukkit.getLogger().info("[UhcCore] Loading kit " + kitKey);
				Kit.Builder builder = new Kit.Builder(kitKey);

				String name = cfg.getString("kits." + kitKey + ".symbol.name");

				String symbolItem = cfg.getString("kits." + kitKey + ".symbol.item", "");
				ItemStack symbol = JsonItemUtils.getItemFromJson(symbolItem);

				ItemMeta im = symbol.getItemMeta();

				if (!im.hasDisplayName()) {
					im.setDisplayName(ChatColor.GREEN + name);
				}

				List<String> lore = new ArrayList<>();

				for (String itemStr : cfg.getStringList("kits." + kitKey + ".items")){
					ItemStack item = JsonItemUtils.getItemFromJson(itemStr);
					builder.addItem(item);
					lore.add(ChatColor.WHITE + "" + item.getAmount() + " x " + item.getType().toString().toLowerCase());
				}

				if (!im.hasLore()) {
					im.setLore(lore);
				}

				symbol.setItemMeta(im);

				builder.setName(name)
						.setSymbol(symbol);

				kits.add(builder.build());

				Bukkit.getLogger().info("[UhcCore] Added kit " + kitKey);

			// IllegalArgumentException, Thrown by builder.build() when kit has no items.
			}catch(ParseException | IllegalArgumentException ex){
				Bukkit.getLogger().severe("[UhcCore] Kit "+kitKey+" was disabled because of an error of syntax.");
				System.out.println(ex.getMessage());
				ex.printStackTrace();
			}
		}

		Bukkit.getLogger().info("[UhcCore] Loaded " + kits.size() + " kits");
	}

	public static void openKitSelectionInventory(Player player){
		int maxSlots = 6*9;
		Inventory inv = Bukkit.createInventory(null, maxSlots, Lang.ITEMS_KIT_INVENTORY);
		int slot = 0;
		for(Kit kit : kits){
			if(slot < maxSlots){
				inv.setItem(slot, kit.getSymbol());
				slot++;
			}
		}
		
		player.openInventory(inv);
	}
	
	public static void giveKitTo(Player player){
		UhcPlayer uhcPlayer = GameManager.getGameManager().getPlayersManager().getUhcPlayer(player);
		if(uhcPlayer.getKit() == null){
			uhcPlayer.setKit(KitsManager.getFirstKitFor(player));
		}

		if(uhcPlayer.getKit() != null && isAtLeastOneKit()){
			player.getInventory().addItem(uhcPlayer.getKit().getItems());
		}
	}

	public static boolean isKitItem(ItemStack item){
		if(item == null || item.getType().equals(Material.AIR))
			return false;
		
		for(Kit kit : kits){
			if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN+kit.getName()))
				return true;
		}
		return false;
	}

	public static Kit getKitByName(String displayName){
		for(Kit kit : kits){
			if(kit.getSymbol().getItemMeta().getDisplayName().equals(displayName))
				return kit;
		}
		return null;
	}

}