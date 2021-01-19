package com.gmail.val59000mc.customitems;

import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

public class Craft {

	private final String name;
	private final List<ItemStack> recipe;
	private final ItemStack displayItem, craft;
	private final int limit;
	private final Set<OnCraftListener> onCraftListeners;
	private final Set<OnConsumeListener> onConsumeListeners;

	public Craft(String name, List<ItemStack> recipe, ItemStack craft, int limit, boolean defaultName){
		this.name = name;
		this.recipe = recipe;
		this.craft = craft;
		this.limit = limit;
		onCraftListeners = new HashSet<>();
		onConsumeListeners = new HashSet<>();

		if (!defaultName){
			ItemMeta im = craft.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + ChatColor.translateAlternateColorCodes('&', name));
			craft.setItemMeta(im);
		}

		displayItem = craft.clone();

		ItemMeta im = displayItem.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + ChatColor.translateAlternateColorCodes('&', name));
		displayItem.setItemMeta(im);

		register();
	}
	
	public boolean isLimited(){
		return limit != -1;
	}
	
	public String getName() {
		return name;
	}

	public List<ItemStack> getRecipe() {
		return recipe;
	}

	public ItemStack getCraft() {
		return craft;
	}

	public ItemStack getDisplayItem() {
		return displayItem;
	}

	public int getLimit() {
		return limit;
	}

	public boolean hasLimit(){
		return limit != -1;
	}

	public Set<OnCraftListener> getOnCraftListeners() {
		return onCraftListeners;
	}

	public Set<OnConsumeListener> getOnConsumeListeners() {
		return onConsumeListeners;
	}

	public void registerListener(OnCraftListener listener) {
		onCraftListeners.add(listener);
	}

	public void registerListener(OnConsumeListener listener) {
		onConsumeListeners.add(listener);
	}

	@SuppressWarnings("deprecation")
	private void register(){
		ShapedRecipe craftRecipe = VersionUtils.getVersionUtils().createShapedRecipe(craft, UUID.randomUUID().toString());
		
		craftRecipe.shape("abc","def","ghi");
		
		List<Character> symbols = Arrays.asList('a','b','c','d','e','f','g','h','i');
		for(int i=0 ; i<9 ; i++){
			if(!recipe.get(i).getType().equals(Material.AIR)){
				Material material = recipe.get(i).getType();
				MaterialData data = recipe.get(i).getData();
				if (data != null && data.getItemType() == material) {
					craftRecipe.setIngredient(symbols.get(i), data);
				}else {
					craftRecipe.setIngredient(symbols.get(i), material);
				}
			}
		}
		
		Bukkit.getLogger().info("[UhcCore] "+name+" custom craft registered");
		Bukkit.getServer().addRecipe(craftRecipe);
	}

	public static class Builder {

		private String name;
		private final ItemStack[] recipe;
		private ItemStack craft;
		private int limit;
		private boolean defaultName;

		public Builder() {
			name = null;
			recipe = new ItemStack[9];
			craft = null;
			limit = -1;
			defaultName = false;
		}

		public Builder setCraftName(String name) {
			this.name = name;
			return this;
		}

		public Builder setRecipeItem(int i, ItemStack recipeItem) {
			recipe[i] = new ItemStack(recipeItem.getType(), 1, recipeItem.getDurability());
			return this;
		}

		public Builder setCraft(ItemStack craft) {
			this.craft = craft;
			return this;
		}

		public Builder setCraftLimit(int limit) {
			this.limit = limit;
			return this;
		}

		public Builder useDefaultName(boolean defaultName) {
			this.defaultName = defaultName;
			return this;
		}

		public Craft create() throws IllegalArgumentException {
			List<ItemStack> recipeList = new ArrayList<>();

			boolean noneAir = false;
			for (int i = 0; i < 9; i++){
				if (recipe[i] == null){
					recipeList.add(new ItemStack(Material.AIR));
				}else {
					recipeList.add(recipe[i]);
					noneAir = true;
				}
			}

			if (!noneAir){
				throw new IllegalArgumentException("No recipe items assigned!");
			}

			if (name == null){
				throw new IllegalArgumentException("Craft name is not assigned!");
			}

			if (craft == null){
				throw new IllegalArgumentException("Craft item is not assigned!");
			}

			return new Craft(name, recipeList, craft, limit, defaultName);
		}

	}

	public interface OnCraftListener {
		/**
		 * Gets called when a player completes the craft.
		 * Make sure the listener is registered using {@link #registerListener(OnCraftListener)}
		 * @param uhcPlayer Player that crafts the item.
		 * @return Should return true to cancel the craft event.
		 */
		boolean onCraft(UhcPlayer uhcPlayer);
	}

	public interface OnConsumeListener {
		/**
		 * Gets called when a player consumes a crafted item.
		 * Make sure the listener is registered using {@link #registerListener(OnConsumeListener)}
		 * @param uhcPlayer Player that consumes the item.
		 * @return Should return true to cancel the consume action.
		 */
		boolean onConsume(UhcPlayer uhcPlayer);
	}

}
