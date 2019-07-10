package com.gmail.val59000mc.scenarios.scenariolisteners.todo;

import com.gmail.val59000mc.utils.UniversalMaterial;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GoldenHeadListener implements Listener{
/*
    public GoldenHeadListener(){
        registerGoldenHeadCraft();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){

        if (sm.isActivated(Scenario.TIMEBOMB) || !sm.useGoldenHeads()){
            return;
        }

        Player p = e.getEntity().getPlayer();
        p.getWorld().dropItem(p.getLocation(), ItemFactory.getPlayerSkull(p.getName()));

    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent e){

        if (e.getItem() == null) return;

        if (e.getItem().equals(ItemFactory.getGoldenHead())){
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
        }

    }

    private void registerGoldenHeadCraft(){
        ShapedRecipe goldenAppleRecipe = VersionUtils.getVersionUtils().createShapedRecipe(ItemFactory.getGoldenHead(), "golden_head");

        goldenAppleRecipe.shape("GGG","GHG","GGG");

        ItemStack head = UniversalMaterial.PLAYER_HEAD.getStack();
        goldenAppleRecipe.setIngredient('G', Material.GOLD_INGOT);
        goldenAppleRecipe.setIngredient('H', Material.SKULL_ITEM);
        goldenAppleRecipe.setIngredient('H', head.getData());

        Bukkit.getServer().addRecipe(goldenAppleRecipe);
    }
*/
}
