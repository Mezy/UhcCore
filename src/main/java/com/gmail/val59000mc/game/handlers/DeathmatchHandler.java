package com.gmail.val59000mc.game.handlers;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.maploader.MapLoader;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import com.gmail.val59000mc.schematics.DeathmatchArena;
import com.gmail.val59000mc.threads.StartDeathmatchThread;
import com.gmail.val59000mc.utils.LocationUtils;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class DeathmatchHandler {

    private final GameManager gameManager;
    private final MainConfig config;
    private final PlayersManager playersManager;
    private final MapLoader mapLoader;

    public DeathmatchHandler(GameManager gameManager, MainConfig config, PlayersManager playersManager, MapLoader mapLoader) {
        this.gameManager = gameManager;
        this.config = config;
        this.playersManager = playersManager;
        this.mapLoader = mapLoader;
    }

    public void startDeathmatch(){
        // DeathMatch can only be stated while GameState = Playing
        if (gameManager.getGameState() != GameState.PLAYING){
            return;
        }

        gameManager.setGameState(GameState.DEATHMATCH);
        gameManager.setPvp(false);
        gameManager.broadcastInfoMessage(Lang.GAME_START_DEATHMATCH);
        playersManager.playSoundToAll(UniversalSound.ENDERDRAGON_GROWL);

        // DeathMatch arena DeathMatch
        if (mapLoader.getArena().isUsed()) {
            DeathmatchArena arena = mapLoader.getArena();
            Location arenaLocation = arena.getLocation();

            //Set big border size to avoid hurting players
            mapLoader.setBorderSize(arenaLocation.getWorld(), arenaLocation.getBlockX(), arenaLocation.getBlockZ(), 50000);

            // Teleport players
            setAllPlayersStartDeathmatch();

            // Shrink border to arena size
            mapLoader.setBorderSize(arenaLocation.getWorld(), arenaLocation.getBlockX(), arenaLocation.getBlockZ(), arena.getMaxSize());

            // Start Enable pvp thread
            Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new StartDeathmatchThread(gameManager, false), 20);
        }
        // 0 0 DeathMach
        else{
            //Set big border size to avoid hurting players
            mapLoader.setBorderSize(mapLoader.getUhcWorld(World.Environment.NORMAL), 0, 0, 50000);

            // Teleport players
            setAllPlayersStartDeathmatch();

            // Shrink border to arena size
            mapLoader.setBorderSize(mapLoader.getUhcWorld(World.Environment.NORMAL), 0, 0, config.get(MainConfig.DEATHMATCH_START_SIZE)*2);

            // Start Enable pvp thread
            Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new StartDeathmatchThread(gameManager, true), 20);
        }
    }

    private void setAllPlayersStartDeathmatch() {
        DeathmatchArena arena = mapLoader.getArena();

        if (arena.isUsed()) {
            List<Location> spots = arena.getTeleportSpots();

            int spotIndex = 0;

            for (UhcTeam teams : playersManager.listUhcTeams()) {
                boolean playingPlayer = false;
                for (UhcPlayer player : teams.getMembers()) {
                    try {
                        Player bukkitPlayer = player.getPlayer();
                        if (player.getState().equals(PlayerState.PLAYING)) {
                            if (config.get(MainConfig.DEATHMATCH_ADVENTURE_MODE)) {
                                bukkitPlayer.setGameMode(GameMode.ADVENTURE);
                            } else {
                                bukkitPlayer.setGameMode(GameMode.SURVIVAL);
                            }
                            Location loc = spots.get(spotIndex);
                            player.freezePlayer(loc);
                            bukkitPlayer.teleport(loc);
                            playingPlayer = true;
                        } else {
                            bukkitPlayer.teleport(arena.getLocation());
                        }
                    } catch (UhcPlayerNotOnlineException e) {
                        // Do nothing for offline players
                    }
                }
                if (playingPlayer) {
                    spotIndex++;
                }
                if (spotIndex == spots.size()) {
                    spotIndex = 0;
                }
            }
        }

        // DeathMatch at 0 0
        else{
            for (UhcTeam teams : playersManager.listUhcTeams()) {
                Location teleportSpot = LocationUtils.findRandomSafeLocation(mapLoader.getUhcWorld(World.Environment.NORMAL), config.get(MainConfig.DEATHMATCH_START_SIZE)-10);

                for (UhcPlayer player : teams.getMembers()){
                    try {
                        Player bukkitPlayer = player.getPlayer();

                        if (player.getState().equals(PlayerState.PLAYING)){
                            if (config.get(MainConfig.DEATHMATCH_ADVENTURE_MODE)){
                                bukkitPlayer.setGameMode(GameMode.ADVENTURE);
                            }else{
                                bukkitPlayer.setGameMode(GameMode.SURVIVAL);
                            }

                            player.freezePlayer(teleportSpot);
                            bukkitPlayer.teleport(teleportSpot);
                        }else{
                            Location spectatingLocation = new Location(mapLoader.getUhcWorld(World.Environment.NORMAL),0, 100,0);
                            bukkitPlayer.teleport(spectatingLocation);
                        }
                    } catch (UhcPlayerNotOnlineException e) {
                        // Do nothing for offline players
                    }
                }
            }
        }
    }

}
