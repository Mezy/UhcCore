package com.gmail.val59000mc.customitems;

import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Craft {
	private String name;
	private List<ItemStack> recipe;
	private ItemStack displayItem, craft;
	private int limit;
	
	public Craft(String name, List<ItemStack> recipe, ItemStack craft, int limit, boolean defaultName){
		this.name = name;
		this.recipe = recipe;
		this.craft = craft;
		this.limit = limit;

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

}