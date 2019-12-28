package com.gmail.val59000mc.scoreboard.placeholders;

import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scoreboard.Placeholder;
import com.gmail.val59000mc.scoreboard.ScoreboardType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TeamMembersPlaceholder extends Placeholder {

    private Map<UUID, Integer> lastShownMember;

    public TeamMembersPlaceholder(){
        super("members");
        lastShownMember = new HashMap<>();
    }

    @Override
    public String getReplacement(UhcPlayer uhcPlayer, Player player, ScoreboardType scoreboardType, String placeholder){

        List<UhcPlayer> teamMembers;

        if (scoreboardType.equals(ScoreboardType.WAITING)){
            teamMembers = uhcPlayer.getTeam().getMembers();
        }else{
            teamMembers = uhcPlayer.getTeam().getPlayingMembers();
        }

        if (teamMembers.isEmpty()){
            return "-";
        }

        int showPlayer = lastShownMember.getOrDefault(player.getUniqueId(), -1) + 1;
        if (showPlayer >= teamMembers.size()){
            showPlayer = 0;
        }
        lastShownMember.put(player.getUniqueId(), showPlayer);
        return teamMembers.get(showPlayer).getName();
    }

}