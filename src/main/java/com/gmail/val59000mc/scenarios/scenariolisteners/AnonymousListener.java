package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.events.PlayerStartsPlayingEvent;
import com.gmail.val59000mc.events.UhcGameStateChangedEvent;
import com.gmail.val59000mc.events.UhcPlayerStateChangedEvent;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.scoreboard.ScoreboardManager;
import com.gmail.val59000mc.utils.ProtocolUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

public class AnonymousListener extends ScenarioListener{

    @Override
    public void onEnable(){
        GameManager gm = GameManager.getGameManager();
        ScoreboardManager sm = gm.getScoreboardManager();

        if (!gm.getConfiguration().getProtocolLibLoaded()){
            Bukkit.broadcastMessage(ChatColor.RED + "[UhcCore] For Anonymous ProtocolLib needs to be installed!");
            getScenarioManager().removeScenario(Scenario.ANONYMOUS);
            return;
        }

        for (UhcPlayer uhcPlayer : gm.getPlayersManager().getAllPlayingPlayers()){
            ProtocolUtils.setPlayerNickName(uhcPlayer, getPlayerNickName(uhcPlayer.getName()));
            sm.updatePlayerTab(uhcPlayer);
        }
    }

    @Override
    public void onDisable(){
        GameManager gm = GameManager.getGameManager();
        ScoreboardManager sm = gm.getScoreboardManager();

        if (!gm.getConfiguration().getProtocolLibLoaded()){
            return; // Never enabled so don't disable.
        }

        for (UhcPlayer uhcPlayer : gm.getPlayersManager().getAllPlayingPlayers()){
            ProtocolUtils.setPlayerNickName(uhcPlayer, null);
            sm.updatePlayerTab(uhcPlayer);
        }
    }

    @EventHandler
    public void onGameStarted(PlayerStartsPlayingEvent e){
        GameManager gm = GameManager.getGameManager();
        ScoreboardManager sm = gm.getScoreboardManager();
        UhcPlayer uhcPlayer = e.getUhcPlayer();

        ProtocolUtils.setPlayerNickName(uhcPlayer, getPlayerNickName(uhcPlayer.getName()));
        sm.updatePlayerTab(uhcPlayer);
    }

    @EventHandler
    public void onGameStateChanged(UhcGameStateChangedEvent e){
        GameManager gm = e.getGameManager();
        ScoreboardManager sm = gm.getScoreboardManager();

        for (UhcPlayer uhcPlayer : gm.getPlayersManager().getPlayersList()){
            if (uhcPlayer.hasNickName()) {
                ProtocolUtils.setPlayerNickName(uhcPlayer, null);
                sm.updatePlayerTab(uhcPlayer);
            }
        }
    }

    @EventHandler
    public void onUhcPlayerStateChange(UhcPlayerStateChangedEvent e){
        if (e.getNewPlayerState() == PlayerState.DEAD){
            GameManager gm = GameManager.getGameManager();
            ScoreboardManager sm = gm.getScoreboardManager();
            UhcPlayer player = e.getPlayer();

            // clear nick
            ProtocolUtils.setPlayerNickName(player, null);
            sm.updatePlayerTab(player);
        }
    }

    private String getPlayerNickName(String name){
        if (name.length() > 12){
            name = name.substring(0, 12);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.MAGIC);
        sb.append(name);

        while (sb.length() < 14){
            sb.append("A");
        }

        sb.append(ChatColor.RESET);
        return sb.toString();
    }

}