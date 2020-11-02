package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.LobbyPvpConfiguration;
import com.gmail.val59000mc.events.UhcGameStateChangedEvent;
import com.gmail.val59000mc.events.UhcLobbyPlayerDamageByPlayerEvent;
import com.gmail.val59000mc.events.UhcLobbyPlayerKilledByPlayerEvent;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.lobby.pvp.LobbyPvpManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class LobbyPvpListener implements Listener {

    private GameManager gameManager;
    private LobbyPvpManager lobbyPvpManager;

    public LobbyPvpListener(GameManager gameManager, LobbyPvpManager lobbyPvpManager) {
        this.gameManager = gameManager;
        this.lobbyPvpManager = lobbyPvpManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(UhcLobbyPlayerDamageByPlayerEvent event) {
        Player player = event.getPlayer();
        Player damager = event.getDamager();

        boolean playerInZone = lobbyPvpManager.inZone(player.getUniqueId());
        boolean damagerInZone = lobbyPvpManager.inZone(damager.getUniqueId());

        if (!playerInZone || !damagerInZone) return;
        event.setPassOriginal(true);

        if (player.getHealth() - event.getFinalDamage() > 0.0) return;
        event.setCancelled(true);

        Bukkit.getPluginManager().callEvent(new UhcLobbyPlayerKilledByPlayerEvent(player, damager));
        LobbyPvpConfiguration configuration = gameManager.getLobbyPvpConfiguration();

        if (configuration.isUseCustomRespawnLocation()) {
            World world = gameManager.getLobby().getLoc().getWorld();
            Location location = configuration.getCustomRespawnLocation();

            player.teleport(new Location(world, location.getX(), location.getY(), location.getZ()));
        }
        else {
            player.teleport(gameManager.getLobby().getLoc());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(PlayerMoveEvent event) {
        handleMove(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(PlayerTeleportEvent event) {
        handleMove(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(PlayerQuitEvent event) {
        if (gameManager.getGameState() == GameState.WAITING) lobbyPvpManager.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (lobbyPvpManager.inZone(event.getWhoClicked().getUniqueId())) {
            if (gameManager.getGameState() == GameState.WAITING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        if (lobbyPvpManager.inZone(event.getPlayer().getUniqueId())) {
            if (gameManager.getGameState() == GameState.WAITING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(UhcGameStateChangedEvent event) {
        if (event.getNewGameState().ordinal() > GameState.WAITING.ordinal()) {
            HandlerList.unregisterAll(this);
        }
    }

    private void handleMove(Player player, Location from, Location to) {
        if (from == null || to == null) return;
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) return;

        boolean inZone = lobbyPvpManager.inZone(player.getUniqueId());
        boolean nowInZone = lobbyPvpManager.nowInZone(to);

        if (!inZone && !nowInZone) return;

        if (nowInZone && !inZone) {
            lobbyPvpManager.addPlayer(player);
        }

        if (inZone && !nowInZone) {
            GameState gameState = gameManager.getGameState();
            if (gameState == GameState.WAITING) this.lobbyPvpManager.removePlayer(player);
        }
    }

}
