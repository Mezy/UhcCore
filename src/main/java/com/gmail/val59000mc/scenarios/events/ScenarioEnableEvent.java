package com.gmail.val59000mc.scenarios.events;

import com.gmail.val59000mc.scenarios.Scenario;

/**
 * Event gets called when a scenario gets enabled.
 */
public class ScenarioEnableEvent extends ScenarioEvent {
  private final Scenario scenario;

  public ScenarioEnableEvent(Scenario scenario) {
    this.scenario = scenario;
  }
}
