package com.gmail.val59000mc.players;

import com.gmail.val59000mc.game.GameManager;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class TeamManager {

    private PlayersManager pm;
    private List<String> prefixes;
    private int lastTeamNumber;

    public TeamManager(){
        pm = GameManager.getGameManager().getPlayersManager();
        lastTeamNumber = 0;
        loadPrefixes();
    }

    public List<UhcTeam> getPlayingUhcTeams(){
        List<UhcTeam> teams = new ArrayList<>();
        for(UhcPlayer player : pm.getPlayersList()){
            if (player.getState() == PlayerState.PLAYING) {
                UhcTeam team = player.getTeam();
                if (!teams.contains(team)) {
                    teams.add(team);
                }
            }
        }
        return teams;
    }

    public List<UhcTeam> getUhcTeams(){
        List<UhcTeam> teams = new ArrayList<>();
        for(UhcPlayer player : pm.getPlayersList()){

            UhcTeam team = player.getTeam();
            if(!teams.contains(team)) {
                teams.add(team);
            }
        }
        return teams;
    }

    public int getNewTeamNumber(){
        lastTeamNumber++;
        return lastTeamNumber;
    }

    private void loadPrefixes(){
        prefixes = new ArrayList<>();

        // team prefix's
        List<ChatColor> colors = new ArrayList<>();

        colors.add(ChatColor.RED);
        colors.add(ChatColor.BLUE);
        colors.add(ChatColor.DARK_GREEN);
        colors.add(ChatColor.DARK_AQUA);
        colors.add(ChatColor.DARK_PURPLE);
        colors.add(ChatColor.YELLOW);
        colors.add(ChatColor.GOLD);
        colors.add(ChatColor.GREEN);
        colors.add(ChatColor.AQUA);
        colors.add(ChatColor.LIGHT_PURPLE);

        List<String> colorEdits = new ArrayList<>();
        colorEdits.add("");
        colorEdits.add("" + ChatColor.BOLD);
        colorEdits.add("" + ChatColor.ITALIC);
        colorEdits.add("" + ChatColor.UNDERLINE);
        colorEdits.add("" + ChatColor.BOLD + "" + ChatColor.ITALIC);
        colorEdits.add("" + ChatColor.BOLD + "" + ChatColor.UNDERLINE);
        colorEdits.add("" + ChatColor.ITALIC + "" + ChatColor.UNDERLINE);
        colorEdits.add("" + ChatColor.ITALIC + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD);

        for (String colorEdit : colorEdits){
            for (ChatColor color : colors){
                prefixes.add(color + colorEdit);
            }
        }
    }

    private List<String> getUsedPrefixes(){
        List<String> used = new ArrayList<>();
        for (UhcTeam team : getUhcTeams()){
            used.add(team.getPrefix().replace("\u25A0 ", ""));
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
        for (String s : prefixes){

            if (!getUsedPrefixes().contains(s)){
                return s;
            }
        }

        return ChatColor.DARK_GRAY + "";
    }

    public String getTeamPrefix(String preferenceColor){
        for (String s : getFreePrefixes()){

            if (s.contains(preferenceColor)){
                return s;
            }
        }

        return null;
    }

}