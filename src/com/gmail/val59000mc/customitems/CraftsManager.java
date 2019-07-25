package com.gmail.val59000mc.customitems;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.utils.UniversalMaterial;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

public class CraftsManager {
	
	private static List<Craft> crafts;
	
	public static synchronized List<Craft> getCrafts(){
		return crafts;
	}

	public static boolean isAtLeastOneCraft() {
		return (getCrafts() != null && getCrafts().size() >= 1);
	}

	@SuppressWarnings("deprecation")
	public static void loadBannedCrafts(){
		Bukkit.getLogger().info("[UhcCore] Loading banned crafts list");
		
		FileConfiguration cfg = UhcCore.getPlugin().getConfig();
		for(String itemLine : cfg.getStringList("customize-game-behavior.ban-items-crafts")){
			
			String[] itemData = itemLine.split("/");
			try{
				if(itemData.length !=2){
					throw new IllegalArgumentException("Couldn't parse "+itemLine+" : Each banned craft should be formatted like ITEM/DATA");
				}else{
					Material material = Material.valueOf(itemData[0]);
					short data = Short.parseShort(itemData[1]);
					Iterator<Recipe> recipes = Bukkit.getServer().recipeIterator();

					while (recipes.hasNext()){
						Recipe recipe = recipes.next();
						if (recipe.getResult().isSimilar(new ItemStack(material, 1, data))){
							recipes.remove();
						}
					}
					Bukkit.getLogger().info("[UhcCore] Banned item "+itemLine+" registered");
				}
			}catch(IllegalArgumentException | UnsupportedOperationException e){
				Bukkit.getLogger().warning("[UhcCore] Failed to register "+itemLine+" banned craft");
				Bukkit.getLogger().warning(e.getMessage());
			}
		}
		
	}

	@SuppressWarnings("deprecation")
	public static void loadCrafts(){
		Bukkit.getLogger().info("[UhcCore] Loading custom crafts");
		crafts = Collections.synchronizedList(new ArrayList<>());
		FileConfiguration cfg = UhcCore.getPlugin().getConfig();

		// loading golden heads craft if enabled
		if (cfg.getBoolean("customize-game-behavior.enable-golden-heads", false)){
			Bukkit.getLogger().info("[UhcCore] Loading custom craft for golden heads");
			registerGoldenHeadCraft();
		}

		Set<String> craftsKeys = cfg.getConfigurationSection("customize-game-behavior.add-custom-crafts").getKeys(false);
		for(String name : craftsKeys){
			ConfigurationSection section = cfg.getConfigurationSection("customize-game-behavior.add-custom-crafts."+name);
			
			List<ItemStack> recipe = new ArrayList<ItemStack>();
			ItemStack craft;
			int limit;
			boolean defaultName;
			
			try{

				Bukkit.getLogger().info("[UhcCore] Loading custom craft "+name);
				
				// Recipe
				String[] lines = new String[3];
				lines[0] = section.getString("1", "");
				lines[1] = section.getString("2", "");
				lines[2] = section.getString("3", "");
				
				for(int i=0 ; i<3; i++){
					String[] itemsInLine = lines[i].split(" ");
					if(itemsInLine.length != 3)
						throw new IllegalArgumentException("Each line should be formatted like ITEM/QUANTITY/DATA ITEM/QUANTITY/DATA ITEM/QUANTITY/DATA");
					for(int j=0 ; j<3 ;j++){
						String[] itemData = itemsInLine[j].split("/");
						if(itemData.length !=3)
							throw new IllegalArgumentException("Each item should be formatted like ITEM/QUANTITY/DATA");
						recipe.add(new ItemStack(Material.valueOf(itemData[0]), Integer.parseInt(itemData[1]), Short.parseShort(itemData[2])));
					}
				}
				
				// Craft
				String craftString = section.getString("craft","");
				String[] craftData = craftString.split("/");
				if(craftData.length != 3)
					throw new IllegalArgumentException("The craft result must be formatted like ITEM/QUANTITY/DATA");
				craft = new ItemStack(Material.valueOf(craftData[0]), Integer.parseInt(craftData[1]), Short.parseShort(craftData[2]));
				

				List<String> enchStringList = section.getStringList("enchants");
				ItemMeta im = craft.getItemMeta();
				for(String enchString : enchStringList){
					String[] enchData = enchString.split(" ");
					Enchantment ench = Enchantment.getByName(enchData[0]);
					if(ench != null){
						int level = 1;
						if(enchData.length > 1){
							level = Integer.parseInt(enchData[1]);
						}
						if(craft.getType().equals(Material.ENCHANTED_BOOK)){
							((EnchantmentStorageMeta) im).addStoredEnchant(ench, level, true);
						}else{
							im.addEnchant(ench, level, true);
						}
					}
				}

				craft.setItemMeta(im);
				
				// Limit
				limit = section.getInt("limit",-1);
				defaultName = section.getBoolean("default-name", false);
				getCrafts().add(new Craft(name, recipe, craft, limit, defaultName));
			}catch(IllegalArgumentException e){
				//ignore craft if bad formatting
				Bukkit.getLogger().warning("[UhcCore] Failed to register "+name+" custom craft : syntax error");
				Bukkit.getLogger().warning(e.getMessage());
			}
			
		}
	}

	public static Craft getCraft(ItemStack result) {
		if(result.hasItemMeta() && result.getItemMeta().hasDisplayName()){
			String displayName = result.getItemMeta().getDisplayName();
			for(Craft craft : getCrafts()){
				if(displayName.equals(craft.getCraft().getItemMeta().getDisplayName())){
					return craft;
				}
			}
		}
		return null;
	}
	
	public static Craft getCraftByName(String craftName) {
		for(Craft craft : getCrafts()){
			if(craft.getName().equals(craftName)){
				return craft;
			}
		}
		return null;
	}
	
	public static Craft getCraftByDisplayName(String craftName){
		for(Craft craft : getCrafts()){
			if(craft.getDisplayItem().getItemMeta().getDisplayName().equals(craftName)){
				return craft;
			}
		}
		return null;
	}
	
	public static void openCraftBookInventory(Player player){
		int maxSlots = 6*9;
		Inventory inv = Bukkit.createInventory(null, maxSlots, ChatColor.GREEN+Lang.DISPLAY_MESSAGE_PREFIX+" "+ChatColor.DARK_GREEN+Lang.ITEMS_CRAFT_BOOK_INVENTORY);
		int slot = 0;
		for(Craft craft : getCrafts()){
			if(slot < maxSlots){
				inv.setItem(slot, craft.getDisplayItem());
				slot++;
			}
		}
		
		player.openInventory(inv);
	}

	public static boolean isCraftItem(ItemStack item) {
		if(item == null || item.getType().equals(Material.AIR))
			return false;


		String name = item.getItemMeta().getDisplayName();
		if(name != null){
			for(Craft craft : getCrafts()){
				 if(name.equals(craft.getDisplayItem().getItemMeta().getDisplayName())
				   && item.getType().equals(craft.getCraft().getType()))
					return true;
			}
		}
		return false;
	}

	public static boolean isCraftBookBackItem(ItemStack item) {
		if(item == null || item.getType().equals(Material.AIR))
			return false;

		if(item.getType().equals(UniversalMaterial.PUFFERFISH.getType()) && item.getItemMeta().getDisplayName().equals(ChatColor.GRAY+Lang.ITEMS_CRAFT_BOOK_BACK))
			return true;
		return false;
	}

	public static void openCraftInventory(Player player, Craft craft) {
		int maxSlots = 6*9;
		Inventory inv = Bukkit.createInventory(null, maxSlots, ChatColor.GREEN+Lang.DISPLAY_MESSAGE_PREFIX+" "+ChatColor.DARK_GREEN+Lang.ITEMS_CRAFT_BOOK_INVENTORY);

		for(int i = 0 ; i < maxSlots-9 ; i++){
			inv.setItem(i, UniversalMaterial.BLACK_STAINED_GLASS_PANE.getStack());
		}
		
		for(int i = maxSlots-9 ; i < maxSlots ; i++){
			inv.setItem(i, UniversalMaterial.WHITE_STAINED_GLASS_PANE.getStack());
		}
		
		// Recipe
		inv.setItem(11, craft.getRecipe().get(0));
		inv.setItem(12, craft.getRecipe().get(1));
		inv.setItem(13, craft.getRecipe().get(2));
		inv.setItem(20, craft.getRecipe().get(3));
		inv.setItem(21, craft.getRecipe().get(4));
		inv.setItem(22, craft.getRecipe().get(5));
		inv.setItem(29, craft.getRecipe().get(6));
		inv.setItem(30, craft.getRecipe().get(7));
		inv.setItem(31, craft.getRecipe().get(8));
		
		// Craft
		inv.setItem(24, craft.getCraft());		

		// Back
		ItemStack back = UniversalMaterial.PUFFERFISH.getStack();
		ItemMeta im = back.getItemMeta();
		im.setDisplayName(ChatColor.GRAY+Lang.ITEMS_CRAFT_BOOK_BACK);
		back.setItemMeta(im);
		inv.setItem(49, back);
		
		player.openInventory(inv);
		
	}

	@SuppressWarnings("deprecation")
	private static void registerGoldenHeadCraft(){
		ItemStack goldenHead = UhcItems.createGoldenHead();
		ShapedRecipe headRecipe = VersionUtils.getVersionUtils().createShapedRecipe(goldenHead, "golden_head");

		headRecipe.shape("GGG", "GHG", "GGG");

		Material material = UniversalMaterial.PLAYER_HEAD.getType();
		MaterialData data = UniversalMaterial.PLAYER_HEAD.getStack().getData();

		headRecipe.setIngredient('G', Material.GOLD_INGOT);

		if (data != null && data.getItemType() == material) {
			headRecipe.setIngredient('H', data);
		}else {
			headRecipe.setIngredient('H', material);
		}

		Bukkit.getServer().addRecipe(headRecipe);
	}

}