package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.events.UhcStartedEvent;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Option;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.TimeUtils;
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
    private final Map<UhcPlayer,Boolean> pveList;
    private int maxHealth;

    @Option
    private long delay = 600;

    public BestPvEListener(){
        taskId = -1;
        pveList = new HashMap<>();
    }

    @Override
    public void onEnable(){
        maxHealth = 20;
        MainConfig cfg = getGameManager().getConfig();
        if (cfg.get(MainConfig.ENABLE_EXTRA_HALF_HEARTS)){
            maxHealth += cfg.get(MainConfig.EXTRA_HALF_HEARTS);
        }
    }

    @Override
    public void onDisable(){
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    @EventHandler
    public void onGameStart(UhcStartedEvent e){
        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), this, delay*TimeUtils.SECOND_TICKS);

        for (UhcPlayer uhcPlayer : e.getPlayerManager().getPlayersList()){
            pveList.put(uhcPlayer, true);
            uhcPlayer.sendMessage(Lang.SCENARIO_BESTPVE_ADDED);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent e){
        if (e.isCancelled()){
            return;
        }

        if (e.getDamage() < 0.2){
            return;
        }

        if (!(e.getEntity() instanceof Player)){
            return;
        }

        Player p = (Player) e.getEntity();
        UhcPlayer uhcPlayer = GameManager.getGameManager().getPlayerManager().getUhcPlayer(p);

        if (!pveList.containsKey(uhcPlayer)){
            return; // Only playing players on list
        }

        if (pveList.get(uhcPlayer)) {
            pveList.put(uhcPlayer, false);
            uhcPlayer.sendMessage(Lang.SCENARIO_BESTPVE_REMOVED);
        }

        if (p.getMaxHealth() > maxHealth){
            double hp = p.getHealth();

            if (hp < maxHealth){
                p.setMaxHealth(maxHealth);
            }else{
                p.setMaxHealth(hp + 1);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        if (e.getEntity().getKiller() != null){
            UhcPlayer uhcPlayer = GameManager.getGameManager().getPlayerManager().getUhcPlayer(e.getEntity().getKiller());

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
    public void run(){
        for (UhcPlayer uhcPlayer : GameManager.getGameManager().getPlayerManager().getOnlinePlayingPlayers()){
            Player player;

            try{
                player = uhcPlayer.getPlayer();
            }catch (UhcPlayerNotOnlineException ex){
                continue; // No hp for offline players
            }

            if (!pveList.containsKey(uhcPlayer)){
                pveList.put(uhcPlayer,true); // Should never occur, playing players are always on list.
                Bukkit.getLogger().warning("[UhcCore] " + player.getName() + " was not on best PvE list yet! Please contact a server administrator.");
            }

            if (player.getGameMode().equals(GameMode.SURVIVAL) && pveList.get(uhcPlayer)){
                // heal player
                if (player.getHealth() + 2 > player.getMaxHealth()){
                    player.setMaxHealth(player.getMaxHealth() + 2);
                }

                player.setHealth(player.getHealth() + 2);
            }
        }

        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(),this,delay*TimeUtils.SECOND_TICKS);
    }

}