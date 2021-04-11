package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerDoesNotExistException;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class PlayerPreLoginListener implements Listener {
    private final PlayerManager playerManager;
    private final List<Role> allowedRoles = Collections.EMPTY_LIST;

    public PlayerPreLoginListener(long[] allowedRoles, DiscordSRV discordAPI) {
        playerManager = UhcCore.getPlugin().getGameManager().getPlayerManager();
        Role[] _allowedRoles = new Role[0];
        for (Long allowedRole : allowedRoles) {
            this.allowedRoles.add(discordAPI.getMainGuild().getRoleById(allowedRole));
        }
    }

    @EventHandler
    public void AsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) throws UhcPlayerDoesNotExistException {
        UhcPlayer uhcPlayer = playerManager.getUhcPlayer(event.getUniqueId());
        Member member = uhcPlayer.getDiscordUser();
        if (!Collections.disjoint(Arrays.asList(member.getRoles().stream().mapToLong(r -> r.getIdLong()).toArray()), allowedRoles)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You aren't allowed to play in this event.");
            return;
        }
    }
}
