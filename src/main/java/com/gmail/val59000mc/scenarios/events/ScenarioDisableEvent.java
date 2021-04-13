package com.gmail.val59000mc.scenarios.events;

import com.gmail.val59000mc.scenarios.Scenario;

/**
 * Event gets called when a scenario gets disabled.
 */
public class ScenarioDisableEvent extends ScenarioEvent {
  private final Scenario scenario;

  public ScenarioDisableEvent(Scenario scenario) {
    this.scenario = scenario;
  }
}
