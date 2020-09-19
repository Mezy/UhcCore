package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.events.UhcStartedEvent;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Option;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class SkyHighListener extends ScenarioListener{

    private int taskId;
    @Option(key = "time-before-start")
    private long delay = 60*30;
    @Option(key = "time-between-damage")
    private long period = 30;
    @Option(key = "y-layer")
    private int yLayer = 120;

    public SkyHighListener(){
        taskId = -1;
    }

    @EventHandler
    public void onGameStarted(UhcStartedEvent e){
        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new SkyHighThread(this), delay*TimeUtils.SECOND_TICKS);
    }

    @Override
    public void onEnable() {
        // start thread
        if (getGameManager().getGameState() == GameState.PLAYING ||
                getGameManager().getGameState() == GameState.DEATHMATCH){
            long timeUntilFirstRun = delay - GameManager.getGameManager().getElapsedTime();
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

    public static class SkyHighThread implements Runnable{

        private final SkyHighListener listener;

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
                        if (player.getLocation().getBlockY() < listener.yLayer) {
                            player.sendMessage(Lang.SCENARIO_SKYHIGH_DAMAGE);
                            player.setHealth(player.getHealth() - 1);
                        }
                    } catch (UhcPlayerNotOnlineException ex) {
                        // No los of hp for offline players.
                    }
                }
            }
            listener.taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), this, listener.period*TimeUtils.SECOND_TICKS);
        }

    }

}