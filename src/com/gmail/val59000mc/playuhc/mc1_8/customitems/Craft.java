package com.gmail.val59000mc.playuhc.mc1_8.customitems;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class Craft {
	private String name;
	private List<ItemStack> recipe;
	private ItemStack craft;
	private int limit;
	
	public Craft(String name, List<ItemStack> recipe, ItemStack craft, int limit){
		this.name = name;
		this.recipe = recipe;
		
		ItemMeta im = craft.getItemMeta();
		im.setDisplayName(ChatColor.GREEN+name);
		craft.setItemMeta(im);
		this.craft = craft;
		
		this.limit = limit;
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
	public int getLimit() {
		return limit;
	}

	
	private void register(){
		ShapedRecipe craftRecipe = new ShapedRecipe(craft);
		
		craftRecipe.shape("abc","def","ghi");
		
		List<Character> symbols = Arrays.asList('a','b','c','d','e','f','g','h','i');
		for(int i=0 ; i<9 ; i++){
			if(!recipe.get(i).getType().equals(Material.AIR)){
				craftRecipe.setIngredient(symbols.get(i),recipe.get(i).getData());
			}
		}
		
		Bukkit.getLogger().info("[PlayUHC] "+name+" custom craft registered");
		//removeDefaultRecipe(craftRecipe);
		
		Bukkit.getServer().addRecipe(craftRecipe);
	}
	
	public void removeDefaultRecipe(ShapedRecipe recipeNew){
        Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
        
        Map<Character, ItemStack> mapNew = recipeNew.getIngredientMap();
        String[] listNew = recipeNew.getShape();
        String stringNew = listNew[0] + listNew[1] + listNew[2];
        Bukkit.getLogger().info("string new " + stringNew);
        
        while(it.hasNext()){
            Recipe itRecipe = it.next();
            if(itRecipe instanceof ShapedRecipe){
                ShapedRecipe defaultRecipe = (ShapedRecipe) itRecipe;
                Map<Character, ItemStack> mapDefault = defaultRecipe.getIngredientMap();
                Bukkit.getLogger().info("check default recipe " + defaultRecipe.getResult().getType());

                if(mapDefault.values().containsAll(mapNew.values())){
                    String[] listDefault = defaultRecipe.getShape();
                    String stringDefault = listDefault[0] + listDefault[1] + listDefault[2];
                    Bukkit.getLogger().info("string default " + stringNew);
                    
                    for(int i = 0; i < stringDefault.length(); i++){
                    	ItemStack stackDefault = mapDefault.get(stringDefault.charAt(i));
                    	ItemStack stackNew = mapNew.get(stringNew.charAt(i));
                    	

                        Bukkit.getLogger().info("item new :" + stackNew.getType() + " "+stackNew.getData().toString());
                        Bukkit.getLogger().info("item default :" + stackDefault.getType() + " "+stackDefault.getData().toString());
                        if(stackDefault.getType() != stackNew.getType() || stackDefault.getData() != stackNew.getData()){
                        	return;
                        }
                    }
                    it.remove();
                    Bukkit.getLogger().info("[PlayUHC] Default recipe for "+recipeNew.getResult().getType()+" removed!");
                }
            }
        }
    }
}
