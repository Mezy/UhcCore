package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.events.PlayerStartsPlayingEvent;
import com.gmail.val59000mc.events.UhcGameStateChangedEvent;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Option;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.EventExecutor;

public class AchievementHunter extends ScenarioListener implements EventExecutor{

    private enum Type{
        ACHIEVEMENTS("org.bukkit.event.player.PlayerAchievementAwardedEvent"),
        ADVANCEMENTS("org.bukkit.event.player.PlayerAdvancementDoneEvent");

        private final String event;

        Type(String event){
            this.event = event;
        }
    }

    private Type type;
    private Class<? extends Event> event;

    @Option(key = "health-at-start")
    private int healthAtStart = 10;
    @Option(key = "health-added")
    private int healthAdded = 1;

    @Override
    public void onEnable() {
        if (UhcCore.getVersion() < 12){
            type = Type.ACHIEVEMENTS;
        }else{
            type = Type.ADVANCEMENTS;
        }

        try {
            event = (Class<? extends PlayerEvent>) Class.forName(type.event);
        }catch (ClassNotFoundException | ClassCastException ex){
            ex.printStackTrace();
            getScenarioManager().disableScenario(Scenario.ACHIEVEMENT_HUNTER);
        }

        Bukkit.getPluginManager().registerEvent(event, this, EventPriority.NORMAL, this, UhcCore.getPlugin());
    }

    @Override
    public void execute(Listener listener, Event event){
        if (getGameManager().getGameState() == GameState.WAITING){
            return;
        }

        System.out.println("event!");

        if (type == Type.ACHIEVEMENTS){
            addHeart(((PlayerEvent) event).getPlayer());
        }else{
            handleAdvancementEvent((PlayerEvent) event);
        }
    }

    @EventHandler
    private void onGameStart(UhcGameStateChangedEvent e){
        if (e.getNewGameState() != GameState.PLAYING){
            return;
        }

        for (UhcPlayer uhcPlayer : e.getPlayerManager().getAllPlayingPlayers()){
            try {
                Player player = uhcPlayer.getPlayer();
                player.setHealth(healthAtStart);
                VersionUtils.getVersionUtils().setPlayerMaxHealth(player, healthAtStart);
            }catch (UhcPlayerNotOnlineException ex){
                // Don't set max health for offline players.
            }
        }
    }

    @EventHandler
    public void onPlayerStartsPlaying(PlayerStartsPlayingEvent e){
        try {
            Player player = e.getUhcPlayer().getPlayer();
            player.setHealth(healthAtStart);
            VersionUtils.getVersionUtils().setPlayerMaxHealth(player, healthAtStart);
        }catch (UhcPlayerNotOnlineException ex){
            // Don't set max health for offline players.
        }
    }

    private void handleAdvancementEvent(PlayerEvent event){
        if (isValidAdvancement(event)){
            addHeart(event.getPlayer());
        }
    }

    private void addHeart(Player player){
        VersionUtils.getVersionUtils().setPlayerMaxHealth(player, player.getMaxHealth() + healthAdded);
        player.setHealth(player.getHealth() + healthAdded);
    }

    private static boolean isValidAdvancement(PlayerEvent event){
        org.bukkit.event.player.PlayerAdvancementDoneEvent advancementEvent = (org.bukkit.event.player.PlayerAdvancementDoneEvent) event;
        NamespacedKey key = advancementEvent.getAdvancement().getKey();
        System.out.println(key.getKey());
        return key.getKey().startsWith("story/");
    }

}