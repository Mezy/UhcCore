package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.events.UhcStartedEvent;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;

public class BestPvEListener extends ScenarioListener implements Runnable{

    private int taskId;
    private Map<UhcPlayer,Boolean> pveList;

    public BestPvEListener(){
        taskId = -1;
        pveList = new HashMap<>();
    }

    @Override
    public void onDisable() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    @EventHandler
    public void onGameStart(UhcStartedEvent e) {
        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), this, 12000L);

        for (UhcPlayer uhcPlayer : e.getPlayersManager().getPlayersList()){
            pveList.put(uhcPlayer, true);
            uhcPlayer.sendMessage(Lang.SCENARIO_BESTPVE_ADDED);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent e){
        if (e.isCancelled()){
            return;
        }

        if (e.getDamage() < 0.1){
            return;
        }

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getEntity();
        UhcPlayer uhcPlayer = GameManager.getGameManager().getPlayersManager().getUhcPlayer(p);

        if (!pveList.containsKey(uhcPlayer)){
            return; // Only playing players on list
        }

        if (pveList.get(uhcPlayer)) {
            pveList.put(uhcPlayer, false);
            uhcPlayer.sendMessage(Lang.SCENARIO_BESTPVE_REMOVED);
        }

        if (p.getMaxHealth() > 20){
            double newHP = p.getHealth() - e.getDamage();

            if (newHP < 20){
                p.setMaxHealth(20);
            }else {
                p.setMaxHealth(newHP + 1);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        if (e.getEntity().getKiller() != null){
            UhcPlayer uhcPlayer = GameManager.getGameManager().getPlayersManager().getUhcPlayer(e.getEntity().getKiller());

            if (!pveList.containsKey(uhcPlayer)){
                return; // Only playing players on list
            }

            if (!pveList.get(uhcPlayer)){
                pveList.put(uhcPlayer,true);
                uhcPlayer.sendMessage(Lang.SCENARIO_BESTPVE_BACK);
            }
        }
    }

    @Override
    public void run() {
        for (UhcPlayer uhcPlayer : GameManager.getGameManager().getPlayersManager().getOnlinePlayingPlayers()){

            try{
                Player p = uhcPlayer.getPlayer();

                if (!pveList.containsKey(uhcPlayer)) {
                    pveList.put(uhcPlayer,true); // Should never occur, playing players are always on list.
                    Bukkit.getLogger().warning("[UhcCore] " + p.getName() + " was not on best PvE list yet! Please contact a server administrator.");
                }

                if (p.getGameMode().equals(GameMode.SURVIVAL) && pveList.get(uhcPlayer)){
                    // heal player
                    if (p.getHealth() + 2 > p.getMaxHealth()){
                        p.setMaxHealth(p.getMaxHealth() + 2);
                    }

                    p.setHealth(p.getHealth() + 2);
                }

            }catch (UhcPlayerNotOnlineException ex){
                // No hp for offline players
            }
        }

        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(),this,12000L);
    }

}