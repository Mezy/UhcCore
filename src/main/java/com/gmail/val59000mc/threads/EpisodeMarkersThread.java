package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;

public class EpisodeMarkersThread implements Runnable{

    private final GameManager gameManager;
    private final long delay;
    private int episodeNr;

    public EpisodeMarkersThread(GameManager gameManager) {
        this.gameManager = gameManager;
        this.delay = gameManager.getConfiguration().getEpisodeMarkersDelay() * 20;
        this.episodeNr = 0;
    }

    @Override
    public void run() {
        if (episodeNr > 0) {
            gameManager.broadcastInfoMessage(Lang.DISPLAY_EPISODE_MARK.replace("%episode%", episodeNr + ""));
            gameManager.getPlayersManager().playSoundToAll(UniversalSound.FIREWORK_LAUNCH,1,1);
        }
        episodeNr ++;
        gameManager.setEpisodeNumber(episodeNr);
        Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this, delay);
    }

}