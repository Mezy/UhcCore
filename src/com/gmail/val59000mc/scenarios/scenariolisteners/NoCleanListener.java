package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NoCleanListener extends ScenarioListener{

    private Map<UUID, Long> pvpCooldown;

    public NoCleanListener(){
        super(Scenario.NOCLEAN);
        pvpCooldown = new HashMap<>();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        if (e.getEntity().getKiller() != null){
            Player killer = e.getEntity().getKiller().getPlayer();
            pvpCooldown.put(killer.getUniqueId(), System.currentTimeMillis() + 30000);
            killer.sendMessage(Lang.SCENARIO_NOCLEAN_INVULNERABLE);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e){

        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player){

            Player player = ((Player) e.getEntity()).getPlayer();
            Player damager = ((Player) e.getDamager()).getPlayer();

            if (pvpCooldown.containsKey(player.getUniqueId())){
                if (pvpCooldown.get(player.getUniqueId()) > System.currentTimeMillis()){
                    e.setCancelled(true);
                    damager.sendMessage(Lang.SCENARIO_NOCLEAN_ERROR);
                }
            }

            if (pvpCooldown.containsKey(damager.getUniqueId())){

                if (pvpCooldown.get(damager.getUniqueId()) > System.currentTimeMillis()){
                    damager.sendMessage(Lang.SCENARIO_NOCLEAN_VULNERABLE);
                }
                pvpCooldown.remove(damager.getUniqueId());
            }

        }

        if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow){

            Arrow arrow = (Arrow) e.getDamager();

            if (!(arrow.getShooter() instanceof Player)){
                return;
            }

            Player player = ((Player) e.getEntity()).getPlayer();
            Player damager = (Player) arrow.getShooter();

            if (pvpCooldown.containsKey(player.getUniqueId())){
                if (pvpCooldown.get(player.getUniqueId()) > System.currentTimeMillis()){
                    e.setCancelled(true);
                    damager.sendMessage(Lang.SCENARIO_NOCLEAN_ERROR);
                }
            }

            if (pvpCooldown.containsKey(damager.getUniqueId())){

                if (pvpCooldown.get(damager.getUniqueId()) > System.currentTimeMillis()){
                    damager.sendMessage(Lang.SCENARIO_NOCLEAN_VULNERABLE);
                }
                pvpCooldown.remove(damager.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e){

        if (e.getEntityType() != EntityType.PLAYER || e.isCancelled()){
            return;
        }

        if (
                e.getCause() != EntityDamageEvent.DamageCause.FIRE &&
                e.getCause() != EntityDamageEvent.DamageCause.LAVA &&
                e.getCause() != EntityDamageEvent.DamageCause.FIRE_TICK){
            return;
        }

        Player player = (Player) e.getEntity();

        if (pvpCooldown.containsKey(player.getUniqueId())){
            if (pvpCooldown.get(player.getUniqueId()) > System.currentTimeMillis()){
                e.setCancelled(true);
            }
        }
    }

}