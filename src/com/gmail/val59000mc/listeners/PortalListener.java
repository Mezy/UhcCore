package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class PortalListener implements Listener{

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPortalEvent (PlayerPortalEvent event){

		if(GameManager.getGameManager().getConfiguration().getBanNether() || UhcCore.getVersion() >= 14){

			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED+ Lang.PLAYERS_NETHER_OFF);

		}else{

			if (event.getTo() == null){
				Location loc = event.getFrom();

				if (event.getCause().equals(TeleportCause.NETHER_PORTAL)){

					if (event.getFrom().getWorld().getEnvironment() == Environment.NETHER){
						loc.setWorld(Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getOverworldUuid()));
						loc.setX( loc.getX()*2d);
						loc.setZ( loc.getZ()*2d);
						event.setTo(event.getPortalTravelAgent().findOrCreate(loc));
					}else{

						loc.setWorld(Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getNetherUuid()));

						loc.setX( loc.getX()/2d);
						loc.setZ( loc.getZ()/2d);

						event.setTo(event.getPortalTravelAgent().findOrCreate(loc));

					}
				}
			}
		}
	}

}