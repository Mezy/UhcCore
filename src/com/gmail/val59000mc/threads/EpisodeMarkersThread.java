package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;

public class EpisodeMarkersThread implements Runnable{

    private long delay;
    private int episodeNr;

    public EpisodeMarkersThread() {
        this.delay = GameManager.getGameManager().getConfiguration().getEpisodeMarkersDelay() * 20;
        this.episodeNr = 0;
    }

    @Override
    public void run() {
        if (episodeNr > 0) {
            GameManager.getGameManager().broadcastInfoMessage(Lang.DISPLAY_EPISODE_MARK.replace("%episode%", episodeNr + ""));
            GameManager.getGameManager().getPlayersManager().playSoundToAll(UniversalSound.FIREWORK_LAUNCH,1,1);
        }
        episodeNr ++;
        GameManager.getGameManager().setEpisodeNumber(episodeNr);
        Bukkit.getScheduler().runTaskLaterAsynchronously(UhcCore.getPlugin(), this, delay);
    }

}