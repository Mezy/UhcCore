package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.MainConfiguration;
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
		MainConfiguration cfg = gm.getConfiguration();

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
        if(!cfg.getCanSendMessagesAfterDeath() && uhcPlayer.getState() == PlayerState.DEAD){
        	// check if has override permissions
			if (player.hasPermission("uhc-core.chat.override")) return;

            uhcPlayer.sendMessage(ChatColor.RED + "You are not allowed to send messaged!");
            e.setCancelled(true);
            return;
        }

        // Team chat
		if (
				uhcPlayer.getState() == PlayerState.PLAYING && isTeamMessage(cfg, e, uhcPlayer)
		){
			e.setCancelled(true);
			uhcPlayer.getTeam().sendChatMessageToTeamMembers(
					ChatColor.WHITE + uhcPlayer.getName() + " : " + e.getMessage()
			);
        }

	}

	private boolean isTeamMessage(MainConfiguration cfg, AsyncPlayerChatEvent e, UhcPlayer uhcPlayer){
		if (cfg.getEnableChatPrefix()){
			if (e.getMessage().startsWith(cfg.getTeamChatPrefix())){
				e.setMessage(e.getMessage().replaceFirst(cfg.getTeamChatPrefix(), ""));
				return true;
			}
			if (e.getMessage().startsWith(cfg.getGlobalChatPrefix())){
				e.setMessage(e.getMessage().replaceFirst(cfg.getGlobalChatPrefix(), ""));
				return false;
			}
		}

		return !uhcPlayer.isGlobalChat();
	}

}