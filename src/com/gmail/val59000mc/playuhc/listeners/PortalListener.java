package com.gmail.val59000mc.playuhc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.val59000mc.playuhc.game.GameManager;
import com.gmail.val59000mc.playuhc.languages.Lang;

public class PortalListener implements Listener{

	@EventHandler(priority = EventPriority.HIGHEST)
	 public void onPlayerPortalEvent (PlayerPortalEvent event){
	     if(GameManager.getGameManager().getConfiguration().getBanNether()){
	    	 event.setCancelled(true);
	    	 event.getPlayer().sendMessage(ChatColor.RED+Lang.PLAYERS_NETHER_OFF);
	     }else{
	    	 if (event.getTo() == null){
	             Location loc = event.getFrom();
	          
	             if (event.getCause().equals(TeleportCause.NETHER_PORTAL)){

	             	 event.getPortalTravelAgent().setCreationRadius(50);
	             	 
	                 if (event.getFrom().getWorld().getEnvironment() == Environment.NETHER){
	                	 event.getPortalTravelAgent().setSearchRadius(10);
	                	loc.setWorld(Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getOverworldUuid()));
	                     loc.setX( loc.getX()*2d);
	                     loc.setZ( loc.getZ()*2d);
	                     event.setTo(event.getPortalTravelAgent().findOrCreate(loc));
	                 }else{
	                	 event.getPortalTravelAgent().setSearchRadius(0);
	                 	 event.getPortalTravelAgent().setCreationRadius(50);
	                	 loc.setWorld(Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getNetherUuid()));
	                     loc.setX( loc.getX()/2d);
	                     loc.setZ( loc.getZ()/2d);
	                     event.getPortalTravelAgent().createPortal(loc);
	                     event.setTo(event.getPortalTravelAgent().findPortal(loc));
	                 }
	             }
	         }
	     }
 }
}
