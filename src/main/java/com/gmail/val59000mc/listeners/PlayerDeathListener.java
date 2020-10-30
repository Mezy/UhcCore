package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.configuration.VaultManager;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.events.UhcPlayerKillEvent;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioManager;
import com.gmail.val59000mc.scenarios.scenariolisteners.SilentNightListener;
import com.gmail.val59000mc.threads.TimeBeforeSendBungeeThread;
import com.gmail.val59000mc.utils.UniversalMaterial;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

public class PlayerDeathListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		GameManager gm = GameManager.getGameManager();
		PlayersManager pm = gm.getPlayersManager();
		MainConfiguration cfg = gm.getConfiguration();
		UhcPlayer uhcPlayer = pm.getUhcPlayer(player);

		if (uhcPlayer.getState() != PlayerState.PLAYING){
			Bukkit.getLogger().warning("[UhcCore] " + player.getName() + " died while already in 'DEAD' mode!");
			player.kickPlayer("Don't cheat!");
			return;
		}

		pm.setLastDeathTime();

		// kill event
		Player killer = player.getKiller();
		if(killer != null){
			UhcPlayer uhcKiller = pm.getUhcPlayer(killer);

			uhcKiller.kills++;

			// Call Bukkit event
			UhcPlayerKillEvent killEvent = new UhcPlayerKillEvent(uhcPlayer, uhcKiller);
			Bukkit.getServer().getPluginManager().callEvent(killEvent);

			if(cfg.getEnableKillEvent()){
				double reward = cfg.getRewardKillEvent();
				List<String> killCommands = cfg.getKillCommands();
				if (reward > 0) {
					VaultManager.addMoney(killer, reward);
					if (!Lang.EVENT_KILL_REWARD.isEmpty()) {
						killer.sendMessage(Lang.EVENT_KILL_REWARD.replace("%money%", "" + reward));
					}
				}
				// If the list is empty, this will never execute
				killCommands.forEach(cmd -> {
					try {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%name%", uhcKiller.getRealName()));
					} catch (CommandException exception){
						Bukkit.getLogger().warning("[UhcCore] Failed to execute kill reward command: " + cmd);
						exception.printStackTrace();
					}
				});
			}
		}

		// Store drops in case player gets re-spawned.
		uhcPlayer.getStoredItems().clear();
		uhcPlayer.getStoredItems().addAll(event.getDrops());

		// eliminations
		ScenarioManager sm = gm.getScenarioManager();
		if (!sm.isActivated(Scenario.SILENTNIGHT) || !((SilentNightListener) sm.getScenarioListener(Scenario.SILENTNIGHT)).isNightMode()) {
			gm.broadcastInfoMessage(Lang.PLAYERS_ELIMINATED.replace("%player%", player.getName()));
		}

		if(cfg.getRegenHeadDropOnPlayerDeath()){
			event.getDrops().add(UhcItems.createRegenHead(uhcPlayer));
		}

		if(cfg.getEnableGoldenHeads()){
			if (cfg.getPlaceHeadOnFence() && !gm.getScenarioManager().isActivated(Scenario.TIMEBOMB)){
				// place head on fence
				Location loc = player.getLocation().clone().add(1,0,0);
				loc.getBlock().setType(UniversalMaterial.OAK_FENCE.getType());
				loc.add(0, 1, 0);
				loc.getBlock().setType(UniversalMaterial.PLAYER_HEAD_BLOCK.getType());

				Skull skull = (Skull) loc.getBlock().getState();
				VersionUtils.getVersionUtils().setSkullOwner(skull, uhcPlayer);
				skull.setRotation(BlockFace.NORTH);
				skull.update();
			}else{
				event.getDrops().add(UhcItems.createGoldenHeadPlayerSkull(player.getName(), player.getUniqueId()));
			}
		}

		if(cfg.getEnableExpDropOnDeath()){
			UhcItems.spawnExtraXp(player.getLocation(), cfg.getExpDropOnDeath());
		}

		uhcPlayer.setState(PlayerState.DEAD);
		pm.strikeLightning(uhcPlayer);
		pm.playSoundPlayerDeath();

		// handle player leaving the server
		boolean canContinueToSpectate = player.hasPermission("uhc-core.spectate.override")
				|| cfg.getCanSpectateAfterDeath();

		if (!canContinueToSpectate) {
			if (cfg.getEnableBungeeSupport()) {
				Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new TimeBeforeSendBungeeThread(pm, uhcPlayer, cfg.getTimeBeforeSendBungeeAfterDeath()));
			} else {
				Bukkit.getScheduler().runTask(UhcCore.getPlugin(), () -> {
					player.kickPlayer(Lang.DISPLAY_MESSAGE_PREFIX + " " + Lang.KICK_DEAD);
				});
			}
		}

		pm.checkIfRemainingPlayers();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event){
		PlayersManager pm = GameManager.getGameManager().getPlayersManager();
		UhcPlayer uhcPlayer = pm.getUhcPlayer(event.getPlayer());

		if(uhcPlayer.getState().equals(PlayerState.DEAD)){
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), () -> pm.setPlayerSpectateAtLobby(uhcPlayer), 1);
		}
	}
	
}