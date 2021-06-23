package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Optional;

public class EntityDamageListener implements Listener{

    private final GameManager gameManager;

    public EntityDamageListener(GameManager gameManager){
        this.gameManager = gameManager;
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
        handleOfflinePlayers(e);
    }

    private void handleOfflinePlayers(EntityDamageByEntityEvent e){
        if (e.getEntityType() != EntityType.ZOMBIE || !(e.getDamager() instanceof Player)){
            return;
        }

        MainConfig cfg = gameManager.getConfig();
        PlayerManager pm = gameManager.getPlayerManager();
        
        // Offline players are disabled
        if (!cfg.get(MainConfig.SPAWN_OFFLINE_PLAYERS)){
            return;
        }

        Zombie zombie = (Zombie) e.getEntity();
        UhcPlayer damager = pm.getUhcPlayer((Player) e.getDamager());
        
        // Find zombie owner
        Optional<UhcPlayer> owner = pm.getPlayersList()
                .stream()
                .filter(uhcPlayer -> uhcPlayer.getOfflineZombieUuid() != null && uhcPlayer.getOfflineZombieUuid().equals(zombie.getUniqueId()))
                .findFirst();
        
        // Not a offline player
        if (!owner.isPresent()){
            return;
        }
        
        boolean pvp = gameManager.getPvp();
        boolean isTeamMember = owner.get().isInTeamWith(damager);
        boolean friendlyFire = cfg.get(MainConfig.ENABLE_FRIENDLY_FIRE);
        
        // If PvP is false or is team member & friendly fire is off
        if (!pvp || (isTeamMember && !friendlyFire)){
            e.setCancelled(true);
            // Canceled due to friendly fire, so send message
            if (pvp){
                damager.sendMessage(Lang.PLAYERS_FF_OFF);
            }
        }
    }
    
}