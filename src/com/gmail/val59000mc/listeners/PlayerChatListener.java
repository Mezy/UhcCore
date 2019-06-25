package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener{

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent e){
		Player player = e.getPlayer();
		GameManager gm = GameManager.getGameManager();

		if (e.isCancelled()){
		    return;
        }

		UhcPlayer uhcPlayer;
		try {
			uhcPlayer = gm.getPlayersManager().getUhcPlayer(player);
		} catch (UhcPlayerDoesntExistException ex) {
			return;
		}

		// Stop spec chat
        if(!gm.getConfiguration().getCanSendMessagesAfterDeath() && uhcPlayer.getState() == PlayerState.DEAD){
        	// check if has override permissions
			if (player.hasPermission("uhc-core.chat.override")) return;

            uhcPlayer.sendMessage(ChatColor.RED + "You are not allowed to send messaged!");
            e.setCancelled(true);
            return;
        }

        // Team chat
		if (!uhcPlayer.isGlobalChat() && uhcPlayer.getState() == PlayerState.PLAYING){
			e.setCancelled(true);
			uhcPlayer.getTeam().sendChatMessageToTeamMembers(
					ChatColor.WHITE + uhcPlayer.getName() + " : " + e.getMessage()
			);
        }

	}

}