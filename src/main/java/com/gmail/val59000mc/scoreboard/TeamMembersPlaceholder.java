package com.gmail.val59000mc.scoreboard;

import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamMembersPlaceholder extends Placeholder{

    private Map<UUID, Integer> lastShownMember;

    public TeamMembersPlaceholder(){
        super("%members");
        lastShownMember = new HashMap<>();
    }

    @Override
    public String getReplacement(UhcPlayer uhcPlayer, Player player, ScoreboardType scoreboardType) {

        if (scoreboardType.equals(ScoreboardType.WAITING)){

            if (uhcPlayer.getTeam().getMembers().isEmpty()){
                return "-";
            }

            int showPlayer = lastShownMember.getOrDefault(player.getUniqueId(), -1) + 1;
            if (showPlayer >= uhcPlayer.getTeam().getMembers().size()){
                showPlayer = 0;
            }
            lastShownMember.put(player.getUniqueId(), showPlayer);
            return uhcPlayer.getTeam().getMembers().get(showPlayer).getName();
        }

        if (uhcPlayer.getTeam().getPlayingMembers().isEmpty()){
            return "-";
        }

        int showPlayer = lastShownMember.getOrDefault(player.getUniqueId(), -1) + 1;
        if (showPlayer >= uhcPlayer.getTeam().getPlayingMembers().size()){
            showPlayer = 0;
        }
        lastShownMember.put(player.getUniqueId(), showPlayer);
        return uhcPlayer.getTeam().getPlayingMembers().get(showPlayer).getName();
    }

}