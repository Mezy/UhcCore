package com.gmail.val59000mc.playuhc.sounds;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundManager {

    private int version = 0;
    private int maxVersion = 15;

    public SoundManager(){
        // get minecraft version
        String versionString = Bukkit.getBukkitVersion();

        for (int i = 8; i <= maxVersion; i ++){
            if (versionString.contains("1." + i)){
                version = i;
            }
        }

        if (version == 0) {
            version = 8;
            Bukkit.getLogger().warning("[PlayUHC] Failed to detect server version! " + versionString + "?");
        }else {
            Bukkit.getLogger().info("[PlayUHC] 1." + version + " Server detected!");
        }

    }

    public int getVersion() {
        return version;
    }

    private Sound getSound(UhcSound uhcSound){
        return Sound.valueOf(getStringSound(uhcSound));
    }

    private String getStringSound(UhcSound uhcSound){

        if (uhcSound == UhcSound.CLICK){
            if (version < 9){
                return "CLICK";
            }else {
                return "UI_BUTTON_CLICK";
            }
        }

        if (uhcSound == UhcSound.ENDERDRAGON_GROWL){
            if (version < 9){
                return "ENDERDRAGON_GROWL";
            }else {
                return "ENTITY_ENDERDRAGON_GROWL";
            }
        }

        if (uhcSound == UhcSound.WITHER_SPAWN){
            if (version < 9){
                return "WITHER_SPAWN";
            }else {
                return "ENTITY_WITHER_SPAWN";
            }
        }

        if (uhcSound == UhcSound.FIREWORK_LAUNCH){
            if (version < 9){
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
