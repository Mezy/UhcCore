package com.gmail.val59000mc.playuhc.mc1_13.sounds;

import com.gmail.val59000mc.playuhc.PlayUhc;
import com.gmail.val59000mc.playuhc.mc1_13.game.GameManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundManager {

    private GameManager gm;

    public SoundManager(GameManager gameManager){
        gm = gameManager;
    }

    private Sound getSound(UhcSound uhcSound){
        return Sound.valueOf(getStringSound(uhcSound));
    }

    private String getStringSound(UhcSound uhcSound){

        if (uhcSound == UhcSound.CLICK){
            if (PlayUhc.getVersion() < 9){
                return "CLICK";
            }else {
                return "UI_BUTTON_CLICK";
            }
        }

        if (uhcSound == UhcSound.ENDERDRAGON_GROWL){
            if (PlayUhc.getVersion() < 9){
                return "ENDERDRAGON_GROWL";
            }else {
                return "ENTITY_ENDERDRAGON_GROWL";
            }
        }

        if (uhcSound == UhcSound.WITHER_SPAWN){
            if (PlayUhc.getVersion() < 9){
                return "WITHER_SPAWN";
            }else {
                return "ENTITY_WITHER_SPAWN";
            }
        }

        if (uhcSound == UhcSound.FIREWORK_LAUNCH){
            if (PlayUhc.getVersion() < 9){
                return "FIREWORK_LAUNCH";
            }else {
                return "ENTITY_FIREWORK_LAUNCH";
            }
        }

        return null;
    }

    public void playSoundTo(Player player, UhcSound uhcSound, float v, float v1){
        player.playSound(player.getLocation(), getSound(uhcSound),v,v1);
    }

}
