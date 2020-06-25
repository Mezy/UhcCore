package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.events.UhcStartingEvent;
import com.gmail.val59000mc.players.UhcTeam;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.RandomUtils;
import org.bukkit.event.EventHandler;

import java.util.List;

public class DoubleDatesListener extends ScenarioListener{

    @EventHandler
    public void onGameStateChanged(UhcStartingEvent e){
        // Only 1-3 teams so don't match as this would be unfair
        if (getTeamManager().getUhcTeams().size() < 4){
            return;
        }

        // Match teams
        List<UhcTeam> teams = getTeamManager().getUhcTeams();

        UhcTeam firstTeam;
        while (teams.size() > 1){
            firstTeam = teams.get(0);
            teams.remove(firstTeam);

            // Find random team to match with
            int i = RandomUtils.randomInteger(0, teams.size()-1);
            UhcTeam matchTeam = teams.get(i);
            teams.remove(matchTeam);

            matchTeams(firstTeam, matchTeam);
        }
    }

    private void matchTeams(UhcTeam team1, UhcTeam team2){
        team2.getMembers().forEach(member -> member.setTeam(team1));
        team1.getMembers().addAll(team2.getMembers());
    }

}