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
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KitsManager {

	private static List<Kit> kits;
	
	public static boolean isAtLeastOneKit(){
		return (kits != null && kits.size() > 0); 
	}

	public static Kit getFirstKitFor(Player player){
		for (Kit kit : kits){
			if (kit.canBeUsedBy(player)){
				return kit;
			}
		}
		return null;
	}
	
	public static void loadKits(){
		Bukkit.getLogger().info("[UhcCore] Start loading kits");

		YamlFile cfg = FileUtils.saveResourceIfNotAvailable("kits.yml");
		ConfigurationSection kitsSection = cfg.getConfigurationSection("kits");

		kits = new ArrayList<>();

		if (kitsSection == null){
			Bukkit.getLogger().info("[UhcCore] Loaded 0 kits");
			return;
		}

		Set<String> kitsKeys = kitsSection.getKeys(false);
		for(String kitKey : kitsKeys){

			try{
				Bukkit.getLogger().info("[UhcCore] Loading kit " + kitKey);

				Kit kit = new Kit();
				kit.key = kitKey;
				kit.name = cfg.getString("kits." + kitKey + ".symbol.name");
				kit.items = new ArrayList<>();

				String symbolItem = cfg.getString("kits." + kitKey + ".symbol.item", "");
				kit.symbol = JsonItemUtils.getItemFromJson(symbolItem);

				ItemMeta im = kit.symbol.getItemMeta();

				if (!im.hasDisplayName()) {
					im.setDisplayName(ChatColor.GREEN + kit.name);
				}

				List<String> lore = new ArrayList<>();

				for (String itemStr : cfg.getStringList("kits." + kitKey + ".items")){
					ItemStack item = JsonItemUtils.getItemFromJson(itemStr);
					kit.items.add(item);
					lore.add(ChatColor.WHITE + "" + item.getAmount() + " x " + item.getType().toString().toLowerCase());
				}

				if (!im.hasLore()) {
					im.setLore(lore);
				}

				kit.symbol.setItemMeta(im);
				kits.add(kit);

				Bukkit.getLogger().info("[UhcCore] Added kit " + kitKey);
			}catch(ParseException ex){
				Bukkit.getLogger().severe("[UhcCore] Kit "+kitKey+" was disabled because of an error of syntax.");
				System.out.println(ex.getMessage());
				ex.printStackTrace();
			}
		}

		Bukkit.getLogger().info("[UhcCore] Loaded " + kits.size() + " kits");
	}

	public static void moveKitsToKitsYaml(){
		YamlFile config = FileUtils.saveResourceIfNotAvailable("config.yml");
		YamlFile kits = FileUtils.saveResourceIfNotAvailable("kits.yml");
		ConfigurationSection kitsSection = config.getConfigurationSection("kits");
		if (kitsSection != null){
			Bukkit.getLogger().info("[UhcCore] Moving kits to kits.yml file.");

			kits.set("kits", kitsSection);
			try{
				kits.saveWithComments();
			}catch (IOException ex){
				Bukkit.getLogger().warning("Failed to move kits to kits.yml");
				ex.printStackTrace();
				return;
			}

			config.remove("kits");
			try{
				config.saveWithComments();
			}catch (IOException ex){
				Bukkit.getLogger().warning("Failed to save config.yml");
				ex.printStackTrace();
			}
		}
	}

	private static void saveKit(YamlFile cfg, Kit kit, String kitKey){
		cfg.set("kits." + kitKey + ".symbol.item", JsonItemUtils.getItemJson(new ItemStack(kit.symbol.getType())));

		List<String> items = new ArrayList<>();
		for (ItemStack kitItem : kit.items){
			items.add(JsonItemUtils.getItemJson(kitItem));
		}

		cfg.set("kits."+kitKey+".items", items);

		try {
			cfg.saveWithComments();
		}catch (IOException ex){
			ex.printStackTrace();
		}
	}

	public static void openKitSelectionInventory(Player player){
		int maxSlots = 6*9;
		Inventory inv = Bukkit.createInventory(null, maxSlots, Lang.ITEMS_KIT_INVENTORY);
		int slot = 0;
		for(Kit kit : kits){
			if(slot < maxSlots){
				inv.setItem(slot, kit.symbol);
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
			for(ItemStack item : uhcPlayer.getKit().items){
				player.getInventory().addItem(item);
			}
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