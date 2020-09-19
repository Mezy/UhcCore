package com.gmail.val59000mc.players;

import com.gmail.val59000mc.exceptions.UhcTeamException;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.utils.CompareUtils;
import org.bukkit.ChatColor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TeamManager{

    private static final String[] TEAM_COLORS = new String[]{
        ChatColor.RED.toString(),
        ChatColor.BLUE.toString(),
        ChatColor.DARK_GREEN.toString(),
        ChatColor.DARK_AQUA.toString(),
        ChatColor.DARK_PURPLE.toString(),
        ChatColor.YELLOW.toString(),
        ChatColor.GOLD.toString(),
        ChatColor.GREEN.toString(),
        ChatColor.AQUA.toString(),
        ChatColor.LIGHT_PURPLE.toString()
    };

    private static final String[] TEAM_COLOR_VARIATIONS = new String[]{
        "",
        ChatColor.BOLD.toString(),
        ChatColor.ITALIC.toString(),
        ChatColor.UNDERLINE.toString(),
        ChatColor.BOLD.toString() + ChatColor.ITALIC.toString(),
        ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString(),
        ChatColor.ITALIC.toString() + ChatColor.UNDERLINE.toString(),
        ChatColor.ITALIC.toString() + ChatColor.UNDERLINE.toString() + ChatColor.BOLD.toString()
    };

    private final PlayersManager playersManager;
    private int lastTeamNumber;
    private List<String> prefixes;

    public TeamManager(PlayersManager playersManager){
        this.playersManager = playersManager;
        lastTeamNumber = 0;
        loadPrefixes();
    }

    public List<UhcTeam> getPlayingUhcTeams(){
        List<UhcTeam> teams = new ArrayList<>();
        for(UhcTeam team : getUhcTeams()){
            if (!team.getPlayingMembers().isEmpty()){
                teams.add(team);
            }
        }
        return teams;
    }

    public List<UhcTeam> getUhcTeams(){
        List<UhcTeam> teams = new ArrayList<>();
        for(UhcPlayer player : playersManager.getPlayersList()){

            UhcTeam team = player.getTeam();
            if(!teams.contains(team)) {
                teams.add(team);
            }
        }
        return teams;
    }

    public void replyToTeamInvite(UhcPlayer uhcPlayer, UhcTeam team, boolean accepted){
        uhcPlayer.getTeamInvites().remove(team);

        if (!accepted){
            uhcPlayer.sendMessage(Lang.TEAM_MESSAGE_DENY_REQUEST);
            return;
        }

        try{
            team.join(uhcPlayer);
        }catch (UhcTeamException ex){
            uhcPlayer.sendMessage(ex.getMessage());
        }
    }

    public boolean isValidTeamName(String name){
        return CompareUtils.validateName(name)
                && getTeamByName(name) == null;
    }

    @Nullable
    public UhcTeam getTeamByName(String name){
        for (UhcTeam team : getUhcTeams()){
            if (team.getTeamName().equals(name)){
                return team;
            }
        }

        return null;
    }

    public int getNewTeamNumber(){
        lastTeamNumber++;
        return lastTeamNumber;
    }

    private void loadPrefixes(){
        prefixes = new ArrayList<>();

        for (String colorVariation : TEAM_COLOR_VARIATIONS){
            for (String color : TEAM_COLORS){
                prefixes.add(color + colorVariation);
            }
        }
    }

    private List<String> getUsedPrefixes(){
        List<String> used = new ArrayList<>();
        for (UhcTeam team : getUhcTeams()){
            used.add(team.getColor());
        }
        return used;
    }

    public List<String> getFreePrefixes(){
        List<String> used = getUsedPrefixes();
        List<String> free = new ArrayList<>();
        for (String prefix : prefixes){
            if (!used.contains(prefix)){
                free.add(prefix);
            }
        }
        return free;
    }

    public String getTeamPrefix(){
        List<String> free = getFreePrefixes();

        if (free.isEmpty()){
            return ChatColor.DARK_GRAY.toString();
        }

        return free.get(0);
    }

    public String getTeamPrefix(String preferenceColor){
        for (String prefix : getFreePrefixes()){
            if (prefix.contains(preferenceColor)){
                return prefix;
            }
        }

        return null;
    }

}