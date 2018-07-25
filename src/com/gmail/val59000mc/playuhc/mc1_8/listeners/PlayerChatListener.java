package com.gmail.val59000mc.playuhc.mc1_8.listeners;

import com.gmail.val59000mc.playuhc.mc1_8.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.playuhc.mc1_8.game.GameManager;
import com.gmail.val59000mc.playuhc.mc1_8.players.UhcPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener{
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		
		Player player = event.getPlayer();
		GameManager gm = GameManager.getGameManager();
		UhcPlayer uhcPlayer;
		try {
			uhcPlayer = gm.getPlayersManager().getUhcPlayer(player);
			switch(uhcPlayer.getState()){
			case WAITING:
				event.setCancelled(true);
				gm.broadcastMessage(ChatColor.WHITE+"<"+ChatColor.AQUA+player.getName()+ChatColor.WHITE+"> "+event.getMessage());
				break;
			case PLAYING:
				event.setCancelled(true);
				if(uhcPlayer.isGlobalChat())
					gm.broadcastMessage(ChatColor.WHITE+"<"+ChatColor.AQUA+uhcPlayer.getName()+ChatColor.WHITE+"> "+event.getMessage());
				else
					uhcPlayer.getTeam().sendChatMessageToTeamMembers(ChatColor.WHITE+uhcPlayer.getName()+" : "+event.getMessage());
				break;
			case DEAD:
				event.setCancelled(true);
				if(gm.getConfiguration().getCanSendMessagesAfterDeath())
					gm.broadcastMessage(ChatColor.GRAY+"[Spec] "+uhcPlayer.getName()+" : "+event.getMessage());
				break;
		}
		} catch (UhcPlayerDoesntExistException e) {
		}
				
		
	}
}
