package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.customitems.CraftsManager;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public class RandomizedCraftsListener extends ScenarioListener{

    @Override
    public void onEnable(){
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        List<ItemStack> results = new ArrayList<>();
        Set<ShapedRecipe> removeRecipes = new HashSet<>();

        Recipe recipe;
        while (iterator.hasNext()){
            recipe = iterator.next();
            if (!(recipe instanceof ShapedRecipe)){
                continue;
            }

            results.add(recipe.getResult());
            removeRecipes.add((ShapedRecipe) recipe);
        }

        Collections.shuffle(results);
        Iterator<ItemStack> resultIterator = results.iterator();
        Set<ShapedRecipe> randomizedRecipes = new HashSet<>();

        for (ShapedRecipe oldRecipe : removeRecipes){
            ShapedRecipe newRecipe = cloneRecipeWithResult(oldRecipe, resultIterator.next());
            randomizedRecipes.add(newRecipe);

            VersionUtils.getVersionUtils().removeRecipe(newRecipe.getResult(), oldRecipe);
        }

        randomizedRecipes.forEach(r -> Bukkit.getServer().addRecipe(r));
    }

    @Override
    public void onDisable(){
        Bukkit.resetRecipes();
        CraftsManager.loadBannedCrafts();
        CraftsManager.loadCrafts();
    }

    private ShapedRecipe cloneRecipeWithResult(ShapedRecipe recipe, ItemStack result){
        ShapedRecipe clone = VersionUtils.getVersionUtils().createShapedRecipe(result, UUID.randomUUID().toString());
        clone.shape(recipe.getShape());

        Map<Character, RecipeChoice> recipeChoiceMap = recipe.getChoiceMap();
        for (char c : recipeChoiceMap.keySet()){
            clone.setIngredient(c, recipeChoiceMap.get(c));
        }

        return clone;
    }

}