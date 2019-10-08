package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.events.UhcStartedEvent;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class SkyHighListener extends ScenarioListener{

    private int taskId;

    public SkyHighListener(){
        super(Scenario.SKYHIGH);
        taskId = -1;
    }

    @EventHandler
    public void onGameStarted(UhcStartedEvent e){
        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new SkyHighThread(this), 20*60*30);
    }

    @Override
    public void onEnable() {
        // start thread
        if (GameManager.getGameManager().getGameState() == GameState.PLAYING ||
                GameManager.getGameManager().getGameState() == GameState.DEATHMATCH){
            long timeUntilFirstRun = 30*60 - GameManager.getGameManager().getElapsedTime();
            if (timeUntilFirstRun < 0){
                timeUntilFirstRun = 0;
            }
            taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new SkyHighThread(this), timeUntilFirstRun);
        }
    }

    @Override
    public void onDisable() {
        // stop thread
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public class SkyHighThread implements Runnable{

        private SkyHighListener listener;

        public SkyHighThread(SkyHighListener listener){
            this.listener = listener;
        }

        @Override
        public void run() {
            // damage players
            for (UhcPlayer uhcPlayer : GameManager.getGameManager().getPlayersManager().getOnlinePlayingPlayers()){
                if (uhcPlayer.getState() == PlayerState.PLAYING) {
                    try {
                        Player player = uhcPlayer.getPlayer();
                        if (player.getLocation().getBlockY() < 120) {
                            player.sendMessage(Lang.SCENARIO_SKYHIGH_DAMAGE);
                            player.setHealth(player.getHealth() - 1);
                        }
                    } catch (UhcPlayerNotOnlineException ex) {
                        // No los of hp for offline players.
                    }
                }
            }
            listener.taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), this, 20*30);
        }

    }

}