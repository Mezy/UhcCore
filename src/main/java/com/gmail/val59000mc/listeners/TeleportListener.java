package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class TeleportListener implements Listener{

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPortalEvent (PlayerPortalEvent event){
		GameManager gm = GameManager.getGameManager();
		Player player = event.getPlayer();

		// Disable nether/end in deathmatch
		if (gm.getGameState() == GameState.DEATHMATCH){
			event.setCancelled(true);
			return;
		}
		
		if (event.getCause() == TeleportCause.NETHER_PORTAL) {

			if (gm.getConfiguration().getBanNether()) {
				player.sendMessage(Lang.PLAYERS_NETHER_OFF);
				event.setCancelled(true);
				return;
			}

			// No Going back!
			if (gm.getScenarioManager().isActivated(Scenario.NOGOINGBACK) && event.getFrom().getWorld().getEnvironment() == Environment.NETHER){
				player.sendMessage(Lang.SCENARIO_NOGOINGBACK_ERROR);
				event.setCancelled(true);
				return;
			}

			// Handle event using versions utils as on 1.14+ PortalTravelAgent got removed.
			VersionUtils.getVersionUtils().handleNetherPortalEvent(event);

		}else if (event.getCause() == TeleportCause.END_PORTAL){

			if (gm.getConfiguration().getEnableTheEnd() && event.getFrom().getWorld().getEnvironment() == Environment.NORMAL){
				// Teleport to end
				Location end = new Location(Bukkit.getWorld(gm.getConfiguration().getTheEndUuid()), -42, 48, -18);

				createEndSpawnAir(end);
				createEndSpawnObsidian(end);

				event.setTo(end);
			}
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e){
		if (e.getCause() == TeleportCause.SPECTATE && !GameManager.getGameManager().getConfiguration().getSpectatingTeleport()){
			Player player = e.getPlayer();
			if (!player.hasPermission("uhc-core.commands.teleport-admin")){
				e.setCancelled(true);
				player.sendMessage(Lang.COMMAND_SPECTATING_TELEPORT_ERROR);
			}
		}
	}

	private void createEndSpawnAir(Location loc){
		int topBlockX = (-41);
		int bottomBlockX = (-44);

		int topBlockY = (50);
		int bottomBlockY = (48);

		int topBlockZ = (-17);
		int bottomBlockZ = (-20);

		for(int x = bottomBlockX; x <= topBlockX; x++) {

			for(int z = bottomBlockZ; z <= topBlockZ; z++) {

				for(int y = bottomBlockY; y <= topBlockY; y++) {

					Block block = loc.getWorld().getBlockAt(x, y, z);

					block.setType(Material.AIR);
				}
			}
		}
	}

	private void createEndSpawnObsidian(Location loc){
		int topBlockX = (-41);
		int bottomBlockX = (-44);

		int topBlockY = (47);
		int bottomBlockY = (47);

		int topBlockZ = (-17);
		int bottomBlockZ = (-20);

		for(int x = bottomBlockX; x <= topBlockX; x++) {

			for(int z = bottomBlockZ; z <= topBlockZ; z++) {

				for(int y = bottomBlockY; y <= topBlockY; y++) {

					Block block = loc.getWorld().getBlockAt(x, y, z);

					block.setType(Material.OBSIDIAN);
				}
			}
		}
	}

}