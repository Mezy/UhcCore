package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Option;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class SpectatorNearAliveListener extends ScenarioListener {

  @Option(key = "spectators-radius")
  private final int spectatorsRadius = 50;

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) throws UhcPlayerNotOnlineException {
    UhcPlayer uhcPlayer = getPlayerManager().getOrCreateUhcPlayer(event.getPlayer());
    if (uhcPlayer.getState().equals(PlayerState.DEAD)) {
      UhcPlayer closestTeammate = getClosestTeammate(uhcPlayer);
      if (closestTeammate.getPlayer().getLocation().distance(uhcPlayer.getPlayer().getLocation()) > spectatorsRadius) {
        uhcPlayer.getPlayer().teleport(closestTeammate.getPlayer());
      }
    } else {
      List<UhcPlayer> spectators = uhcPlayer.getTeam().getMembers(p -> p.getState().equals(PlayerState.DEAD) && p.isOnline());
      for (UhcPlayer spectator : spectators) {
        UhcPlayer closestTeammate = getClosestTeammate(spectator);
        if (closestTeammate.getPlayer().getLocation().distance(spectator.getPlayer().getLocation()) > spectatorsRadius) {
          spectator.getPlayer().teleport(closestTeammate.getPlayer());
        }
      }
    }
  }

  private UhcPlayer getClosestTeammate(UhcPlayer player) throws UhcPlayerNotOnlineException {
    Location spectatorLocation = player.getPlayer().getLocation();
    UhcPlayer closetTeammate = null;
    for (UhcPlayer teammate : player.getTeam().getOnlinePlayingMembers()) {
      if (closetTeammate == null) {
        closetTeammate = teammate;
        continue;
      }
      Double teammateDistance = teammate.getPlayer().getLocation().distance(spectatorLocation);
      Double closetTeammateDistance = closetTeammate.getPlayer().getLocation().distance(spectatorLocation);
      if (teammateDistance < closetTeammateDistance) closetTeammate = teammate;
    }
    return closetTeammate;
  }
}
