package com.gmail.val59000mc.scenarios.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.scenariolisteners.BestPvEListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class BestPvETread implements Runnable{

    private BestPvEListener bestPvEListener;
    private int taskId;

    public BestPvETread(BestPvEListener bestPvEListener){
        this.bestPvEListener = bestPvEListener;
        taskId = -1;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public void run() {

        for (UhcPlayer uhcPlayer : GameManager.getGameManager().getPlayersManager().getOnlinePlayingPlayers()){

            try {

                Player p = uhcPlayer.getPlayer();

                if (!bestPvEListener.pveList.containsKey(uhcPlayer)) {
                    bestPvEListener.pveList.put(uhcPlayer,true); // Should never occur, playing players are always on list.
                    Bukkit.getLogger().warning("[UhcCore] " + p.getName() + " was not on best PvE list yet! Please contact a server administrator.");
                }

                if (p.getGameMode().equals(GameMode.SURVIVAL) && bestPvEListener.pveList.get(uhcPlayer)){

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