package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.events.PlayerStartsPlayingEvent;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.RandomUtils;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SuperHeroesListener extends ScenarioListener{

    @EventHandler
    public void onGameStart(PlayerStartsPlayingEvent e){
        addHeroesEffect(e.getUhcPlayer(), RandomUtils.randomInteger(0, 5));
    }

    private void addHeroesEffect(UhcPlayer uhcPlayer, int effect){

        Player player;

        try {
            player = uhcPlayer.getPlayer();
        }catch (UhcPlayerNotOnlineException ex){
            // No effect for offline player
            return;
        }

        switch (effect){
            case 0:
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999,0));
                break;
            case 1:
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999,0));
                break;
            case 2:
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999,1));
                break;
            case 3:
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999,0));
                break;
            case 4:
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999,3));
                break;
            case 5:
                double maxHealth = 40;
                VersionUtils.getVersionUtils().setPlayerMaxHealth(player, maxHealth);
                player.setHealth(maxHealth);
                break;
            default:
                System.out.println("No effect for: " + effect);
                break;
        }
    }

}