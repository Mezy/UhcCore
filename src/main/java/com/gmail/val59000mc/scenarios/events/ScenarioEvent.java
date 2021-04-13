package com.gmail.val59000mc.scenarios.events;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.PlayerManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class ScenarioEvent extends Event {

  private static final HandlerList handlers;

  static {
    handlers = new HandlerList();
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public GameManager getGameManager() {
    return GameManager.getGameManager();
  }

  public PlayerManager getPlayerManager() {
    return getGameManager().getPlayerManager();
  }

}
