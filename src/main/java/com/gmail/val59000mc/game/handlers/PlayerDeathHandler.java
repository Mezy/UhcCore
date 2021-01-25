package com.gmail.val59000mc.game.handlers;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.events.UhcPlayerKillEvent;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioManager;
import com.gmail.val59000mc.scenarios.scenariolisteners.SilentNightListener;
import com.gmail.val59000mc.scenarios.scenariolisteners.TeamInventoryListener;
import com.gmail.val59000mc.threads.TimeBeforeSendBungeeThread;
import com.gmail.val59000mc.utils.UniversalMaterial;
import com.gmail.val59000mc.utils.UniversalSound;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class PlayerDeathHandler {

    private final GameManager gameManager;
    private final ScenarioManager scenarioManager;
    private final PlayerManager playerManager;
    private final MainConfig config;
    private final CustomEventHandler customEventHandler;

    public PlayerDeathHandler(GameManager gameManager, ScenarioManager scenarioManager, PlayerManager playerManager, MainConfig config, CustomEventHandler customEventHandler) {
        this.gameManager = gameManager;
        this.scenarioManager = scenarioManager;
        this.playerManager = playerManager;
        this.config = config;
        this.customEventHandler = customEventHandler;
    }

    public void handlePlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UhcPlayer uhcPlayer = playerManager.getUhcPlayer(player);

        Set<ItemStack> modifiedDrops = handlePlayerDeath(uhcPlayer, player.getLocation(), new HashSet<>(event.getDrops()), player.getKiller());

        // Modify event drops
        event.getDrops().clear();
        event.getDrops().addAll(modifiedDrops);

        // handle player leaving the server
        boolean canContinueToSpectate = player.hasPermission("uhc-core.spectate.override")
                || config.get(MainConfig.CAN_SPECTATE_AFTER_DEATH);

        if (!canContinueToSpectate) {
            if (config.get(MainConfig.ENABLE_BUNGEE_SUPPORT)) {
                Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new TimeBeforeSendBungeeThread(playerManager, uhcPlayer, config.get(MainConfig.TIME_BEFORE_SEND_BUNGEE_AFTER_DEATH)));
            } else {
                player.kickPlayer(Lang.DISPLAY_MESSAGE_PREFIX + " " + Lang.KICK_DEAD);
            }
        }
    }

    public void handleOfflinePlayerDeath(UhcPlayer uhcPlayer, @Nullable Location location, @Nullable Player killer) {
        Set<ItemStack> modifiedDrops = handlePlayerDeath(uhcPlayer, location, new HashSet<>(uhcPlayer.getStoredItems()), killer);

        // Drop player items
        if (location != null) {
            modifiedDrops.forEach(item -> location.getWorld().dropItem(location, item));
        }
    }

    private Set<ItemStack> handlePlayerDeath(UhcPlayer uhcPlayer, @Nullable Location location, Set<ItemStack> playerDrops, @Nullable Player killer) {
        if (uhcPlayer.getState() != PlayerState.PLAYING){
            Bukkit.getLogger().warning("[UhcCore] " + uhcPlayer.getName() + " died while already in 'DEAD' mode!");
            return playerDrops;
        }

        playerManager.setLastDeathTime();

        // kill event
        if(killer != null){
            UhcPlayer uhcKiller = playerManager.getUhcPlayer(killer);

            uhcKiller.addKill();

            // Call Bukkit event
            UhcPlayerKillEvent killEvent = new UhcPlayerKillEvent(uhcKiller, uhcPlayer);
            Bukkit.getServer().getPluginManager().callEvent(killEvent);

            customEventHandler.handleKillEvent(killer, uhcKiller);
        }

        // Drop the team inventory if the last player on a team was killed
        if (scenarioManager.isEnabled(Scenario.TEAM_INVENTORY))
        {
            UhcTeam team = uhcPlayer.getTeam();
            if (team.getPlayingMemberCount() == 1)
            {
                ((TeamInventoryListener) scenarioManager.getScenarioListener(Scenario.TEAM_INVENTORY)).dropTeamInventory(team, location);
            }
        }

        // Store drops in case player gets re-spawned.
        uhcPlayer.getStoredItems().clear();
        uhcPlayer.getStoredItems().addAll(playerDrops);

        // eliminations
        if (!scenarioManager.isEnabled(Scenario.SILENT_NIGHT) || !((SilentNightListener) scenarioManager.getScenarioListener(Scenario.SILENT_NIGHT)).isNightMode()) {
            gameManager.broadcastInfoMessage(Lang.PLAYERS_ELIMINATED.replace("%player%", uhcPlayer.getName()));
        }

        if(config.get(MainConfig.REGEN_HEAD_DROP_ON_PLAYER_DEATH)){
            playerDrops.add(UhcItems.createRegenHead(uhcPlayer));
        }

        if(location != null && config.get(MainConfig.ENABLE_GOLDEN_HEADS)){
            if (config.get(MainConfig.PLACE_HEAD_ON_FENCE) && !scenarioManager.isEnabled(Scenario.TIMEBOMB)){
                // place head on fence
                Location loc = location.clone().add(1,0,0);
                loc.getBlock().setType(UniversalMaterial.OAK_FENCE.getType());
                loc.add(0, 1, 0);
                loc.getBlock().setType(UniversalMaterial.PLAYER_HEAD_BLOCK.getType());

                Skull skull = (Skull) loc.getBlock().getState();
                VersionUtils.getVersionUtils().setSkullOwner(skull, uhcPlayer);
                skull.setRotation(BlockFace.NORTH);
                skull.update();
            }else{
                playerDrops.add(UhcItems.createGoldenHeadPlayerSkull(uhcPlayer.getName(), uhcPlayer.getUuid()));
            }
        }

        if(location != null && config.get(MainConfig.ENABLE_EXP_DROP_ON_DEATH)){
            UhcItems.spawnExtraXp(location, config.get(MainConfig.EXP_DROP_ON_DEATH));
        }

        uhcPlayer.setState(PlayerState.DEAD);

        if (config.get(MainConfig.STRIKE_LIGHTNING_ON_DEATH)) {
            playerManager.strikeLightning(uhcPlayer);
        }
        playerManager.playSoundToAll(UniversalSound.WITHER_SPAWN);

        playerManager.checkIfRemainingPlayers();

        return playerDrops;
    }

}
