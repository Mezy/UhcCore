package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaitForDeathmatchListener implements Listener{
	
	private Listener that;
	private Map<UUID,Long> playersMovements;
	private Map<UUID,Location> playersLocations;
	
	public WaitForDeathmatchListener() {
		this.that = this;
		this.playersMovements = new HashMap<UUID,Long>();
		this.playersLocations = new HashMap<UUID,Location>();
		
		for(Player player : Bukkit.getOnlinePlayers()){
			playersMovements.put(player.getUniqueId(), Calendar.getInstance().getTimeInMillis());
			playersLocations.put(player.getUniqueId(), player.getLocation().clone());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerMove(final PlayerMoveEvent event){
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		if(player.getGameMode() != GameMode.SPECTATOR && isHasMoved(event)){
			Location loc = playersLocations.get(uuid);
			if(loc == null){
				loc = player.getLocation().clone();
				playersLocations.put(uuid,loc);
			}
			
			loc.setYaw(player.getLocation().getYaw());
			loc.setPitch(player.getLocation().getPitch());
			event.getPlayer().teleport(loc);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerDamager(final EntityDamageEvent event){
		if(event.getEntity().getType().equals(EntityType.PLAYER)){
			event.setCancelled(true);
		}
	}
	
	public void unregister(){
		PlayerMoveEvent.getHandlerList().unregister(this);
		
		Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), new Runnable(){

			@Override
			public void run() {
				EntityDamageEvent.getHandlerList().unregister(that);
			}
			
		}, 20);
	}
	
	private boolean isMovementAlongXOrZ(Location from, Location to){
		return from.getX() != to.getX() || from.getZ() != to.getZ();
	}
	
	private boolean isHasMoved(final PlayerMoveEvent event){
		UUID uuid = event.getPlayer().getUniqueId();
		
		if(isMovementAlongXOrZ(event.getFrom(),event.getTo())){
			Long current = Calendar.getInstance().getTimeInMillis();
			Long lastMoved = playersMovements.get(uuid);
			if(lastMoved == null){
				lastMoved = current-301;
				playersMovements.put(uuid, lastMoved);
			}
			if(current-lastMoved >= 300){
				playersMovements.put(uuid, current);
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
}
