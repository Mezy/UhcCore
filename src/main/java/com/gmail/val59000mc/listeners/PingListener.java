package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class PingListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPing(ServerListPingEvent event){
		GameManager gm = GameManager.getGameManager();

		if (gm == null){
			return; // Still loading
		}

		if (gm.getConfig().get(MainConfig.DISABLE_MOTD)){
			return; // No motd support
		}

		// Display motd
		switch(gm.getGameState()){
			case ENDED:
				event.setMotd(Lang.DISPLAY_MOTD_ENDED);
				break;
			case LOADING:
				event.setMotd(Lang.DISPLAY_MOTD_LOADING);
				break;
			case DEATHMATCH:
			case PLAYING:
				event.setMotd(Lang.DISPLAY_MOTD_PLAYING);
				break;
			case STARTING:
				event.setMotd(Lang.DISPLAY_MOTD_STARTING);
				break;
			case WAITING:
				event.setMotd(Lang.DISPLAY_MOTD_WAITING);
				break;
			default:
				event.setMotd(Lang.DISPLAY_MESSAGE_PREFIX);
				break;
		}
	}

}