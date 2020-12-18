package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.customitems.CraftsManager;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public class UpsideDownCraftsListener extends ScenarioListener{

    @Override
    public void onEnable(){
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        Set<Recipe> upsideDownRecipes = new HashSet<>();
        Set<Recipe> removeRecipes = new HashSet<>();

        Recipe recipe;
        while (iterator.hasNext()){
            recipe = iterator.next();
            if (!(recipe instanceof ShapedRecipe)){
                continue;
            }

            upsideDownRecipes.add(getUpsideDownRecipeFor((ShapedRecipe) recipe));
            removeRecipes.add(recipe);
        }

        for (Recipe remove : removeRecipes){
            VersionUtils.getVersionUtils().removeRecipe(remove.getResult(), remove);
        }

        for (Recipe add : upsideDownRecipes){
            Bukkit.getServer().addRecipe(add);
        }
    }

    @Override
    public void onDisable(){
        Bukkit.resetRecipes();
        CraftsManager.loadBannedCrafts();
        CraftsManager.loadCrafts();
    }

    private ShapedRecipe getUpsideDownRecipeFor(ShapedRecipe recipe){
        ShapedRecipe upsideDown = VersionUtils.getVersionUtils().createShapedRecipe(recipe.getResult(), UUID.randomUUID().toString());
        upsideDown.shape(getUpsideDownShape(recipe.getShape()));

        Map<Character, RecipeChoice> recipeChoiceMap = recipe.getChoiceMap();
        for (char c : recipeChoiceMap.keySet()){
            upsideDown.setIngredient(c, recipeChoiceMap.get(c));
        }

        return upsideDown;
    }

    private String[] getUpsideDownShape(String[] shape){
        String[] upsideDown = new String[shape.length];

        for (int i = 0; i < shape.length; i++) {
            upsideDown[i] = getMirroredString(shape[shape.length-i-1]);
        }

        return upsideDown;
    }

    private String getMirroredString(String string){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            sb.append(string.charAt(string.length()-i-1));
        }

        return sb.toString();
    }

}