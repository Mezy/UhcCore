package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener{

	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerChat(AsyncPlayerChatEvent e){
		Player player = e.getPlayer();
		GameManager gm = GameManager.getGameManager();
		MainConfiguration cfg = gm.getConfiguration();

		if (e.isCancelled()){
		    return;
        }

		UhcPlayer uhcPlayer = gm.getPlayersManager().getUhcPlayer(player);

		// Spec chat
        if(!cfg.getCanSendMessagesAfterDeath() && uhcPlayer.getState() == PlayerState.DEAD){
        	// check if has override permissions
			if (player.hasPermission("uhc-core.chat.override")) return;

			// Send message in spec chat.
			String message = Lang.DISPLAY_SPECTATOR_CHAT
					.replace("%player%", player.getDisplayName())
					.replace("%message%", e.getMessage());

			gm.getPlayersManager().getOnlineSpectatingPlayers().forEach(p -> p.sendMessage(message));
            e.setCancelled(true);
            return;
        }

        // Team chat
		if (
				uhcPlayer.getState() == PlayerState.PLAYING && isTeamMessage(cfg, e, uhcPlayer)
		){
			e.setCancelled(true);
			uhcPlayer.getTeam().sendChatMessageToTeamMembers(uhcPlayer, e.getMessage());
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