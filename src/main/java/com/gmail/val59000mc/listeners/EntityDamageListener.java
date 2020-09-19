package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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

        MainConfiguration cfg = gameManager.getConfiguration();
        PlayersManager pm = gameManager.getPlayersManager();
        
        // Offline players are disabled
        if (!cfg.getSpawnOfflinePlayers()){
            return;
        }

        Zombie zombie = (Zombie) e.getEntity();
        UhcPlayer damager = pm.getUhcPlayer((Player) e.getDamager());
        UhcPlayer owner = null;
        
        // Find zombie owner
        for (UhcPlayer uhcPlayer : pm.getPlayersList()){
            if (uhcPlayer.getOfflineZombie() != null && uhcPlayer.getOfflineZombie() == zombie){
                owner = uhcPlayer;
                break;
            }
        }
        
        // Not a offline player
        if (owner == null){
            return;
        }
        
        boolean pvp = gameManager.getPvp();
        boolean isTeamMember = owner.isInTeamWith(damager);
        boolean friendlyFire = cfg.getEnableFriendlyFire();
        
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