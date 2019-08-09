package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.events.UhcStartedEvent;
import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.scenarios.threads.BestPvETread;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;

public class BestPvEListener extends ScenarioListener{

    private BestPvETread bestPvETread;
    public Map<UhcPlayer,Boolean> pveList;

    public BestPvEListener(){
        super(Scenario.BESTPVE);
        pveList = new HashMap<>();
        bestPvETread = new BestPvETread(this);
    }

    @Override
    public void onDisable() {
        if (bestPvETread.getTaskId() != -1) {
            Bukkit.getScheduler().cancelTask(bestPvETread.getTaskId());
        }
    }

    @EventHandler
    public void onGameStart(UhcStartedEvent e) {
        int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), bestPvETread, 12000L);
        bestPvETread.setTaskId(taskId);

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

        if (e.getEntity() instanceof Player){

            Player p = ((Player) e.getEntity()).getPlayer();
            UhcPlayer uhcPlayer;

            try {
                uhcPlayer = GameManager.getGameManager().getPlayersManager().getUhcPlayer(p);
            }catch (UhcPlayerDoesntExistException ex){
                return; // Should never occur
            }

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

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){

        if (e.getEntity().getKiller() != null){

            UhcPlayer uhcPlayer;

            try {
                uhcPlayer = GameManager.getGameManager().getPlayersManager().getUhcPlayer(e.getEntity().getKiller());
            }catch (UhcPlayerDoesntExistException ex){
                return; // Should never occur
            }

            if (!pveList.containsKey(uhcPlayer)){
                return; // Only playing players on list
            }

            if (!pveList.get(uhcPlayer)){
                pveList.put(uhcPlayer,true);
                uhcPlayer.sendMessage(Lang.SCENARIO_BESTPVE_BACK);
            }
        }
    }

}