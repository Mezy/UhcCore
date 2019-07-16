package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
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

		if (event.getCause() == TeleportCause.NETHER_PORTAL) {

			if (GameManager.getGameManager().getConfiguration().getBanNether()) {
				event.getPlayer().sendMessage(Lang.PLAYERS_NETHER_OFF);
				event.setCancelled(true);
				return;
			}

			if (UhcCore.getVersion() >= 14) {
				Location loc = event.getFrom();

				if (event.getFrom().getWorld().getEnvironment() == Environment.NETHER) {
					loc.setWorld(Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getOverworldUuid()));
					loc.setX(loc.getX() * 2d);
					loc.setZ(loc.getZ() * 2d);
					event.setTo(loc);
				} else {
					loc.setWorld(Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getNetherUuid()));
					loc.setX(loc.getX() / 2d);
					loc.setZ(loc.getZ() / 2d);
					event.setTo(loc);
				}

			} else if (event.getTo() == null) {
				Location loc = event.getFrom();

				if (event.getFrom().getWorld().getEnvironment() == Environment.NETHER) {
					loc.setWorld(Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getOverworldUuid()));
					loc.setX(loc.getX() * 2d);
					loc.setZ(loc.getZ() * 2d);
					event.setTo(event.getPortalTravelAgent().findOrCreate(loc));
				} else {

					loc.setWorld(Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getNetherUuid()));

					loc.setX(loc.getX() / 2d);
					loc.setZ(loc.getZ() / 2d);

					event.setTo(event.getPortalTravelAgent().findOrCreate(loc));
				}
			}
		}else if (event.getCause() == TeleportCause.END_PORTAL){

			if (GameManager.getGameManager().getConfiguration().getEnableTheEnd() && event.getFrom().getWorld().getEnvironment() == Environment.NORMAL){
				// Teleport to end
				Location end = new Location(Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getTheEndUuid()), -42, 48, -18);

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