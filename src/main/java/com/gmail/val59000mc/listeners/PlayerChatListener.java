package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener{

	private final PlayersManager playersManager;
	private final MainConfiguration configuration;

	public PlayerChatListener(PlayersManager playersManager, MainConfiguration configuration){
		this.playersManager = playersManager;
		this.configuration = configuration;
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerChat(AsyncPlayerChatEvent e){
		Player player = e.getPlayer();

		if (e.isCancelled()){
		    return;
        }

		UhcPlayer uhcPlayer = playersManager.getUhcPlayer(player);

		// Spec chat
        if(!configuration.getCanSendMessagesAfterDeath() && uhcPlayer.getState() == PlayerState.DEAD){
        	// check if has override permissions
			if (player.hasPermission("uhc-core.chat.override")) return;

			// Send message in spec chat.
			String message = Lang.DISPLAY_SPECTATOR_CHAT
					.replace("%player%", player.getDisplayName())
					.replace("%message%", e.getMessage());

			playersManager.getOnlineSpectatingPlayers().forEach(p -> p.sendMessage(message));
            e.setCancelled(true);
            return;
        }

        // Team chat
		if (
				uhcPlayer.getState() == PlayerState.PLAYING && isTeamMessage(e, uhcPlayer)
		){
			e.setCancelled(true);
			uhcPlayer.getTeam().sendChatMessageToTeamMembers(uhcPlayer, e.getMessage());
        }

	}

	private boolean isTeamMessage(AsyncPlayerChatEvent e, UhcPlayer uhcPlayer){
		if (configuration.getEnableChatPrefix()){
			if (e.getMessage().startsWith(configuration.getTeamChatPrefix())){
				e.setMessage(e.getMessage().replaceFirst(configuration.getTeamChatPrefix(), ""));
				return true;
			}
			if (e.getMessage().startsWith(configuration.getGlobalChatPrefix())){
				e.setMessage(e.getMessage().replaceFirst(configuration.getGlobalChatPrefix(), ""));
				return false;
			}
		}

		return !uhcPlayer.isGlobalChat();
	}

}