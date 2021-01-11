package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener{

	private final PlayerManager playerManager;
	private final MainConfig configuration;

	public PlayerChatListener(PlayerManager playerManager, MainConfig configuration){
		this.playerManager = playerManager;
		this.configuration = configuration;
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerChat(AsyncPlayerChatEvent e){
		Player player = e.getPlayer();

		if (e.isCancelled()){
		    return;
        }

		UhcPlayer uhcPlayer = playerManager.getUhcPlayer(player);

		// Spec chat
        if(!configuration.get(MainConfig.CAN_SEND_MESSAGES_AFTER_DEATH) && uhcPlayer.getState() == PlayerState.DEAD){
        	// check if has override permissions
			if (player.hasPermission("uhc-core.chat.override")) return;

			// Send message in spec chat.
			String message = Lang.DISPLAY_SPECTATOR_CHAT
					.replace("%player%", player.getDisplayName())
					.replace("%message%", e.getMessage());

			playerManager.getPlayersList()
					.stream()
					.filter(UhcPlayer::isDeath)
					.forEach(p -> p.sendMessage(message));

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
		if (configuration.get(MainConfig.ENABLE_CHAT_PREFIX)){
			if (e.getMessage().startsWith(configuration.get(MainConfig.TEAM_CHAT_PREFIX))){
				e.setMessage(e.getMessage().replaceFirst(configuration.get(MainConfig.TEAM_CHAT_PREFIX), ""));
				return true;
			}
			if (e.getMessage().startsWith(configuration.get(MainConfig.GLOBAL_CHAT_PREFIX))){
				e.setMessage(e.getMessage().replaceFirst(configuration.get(MainConfig.GLOBAL_CHAT_PREFIX), ""));
				return false;
			}
		}

		return !uhcPlayer.isGlobalChat();
	}

}