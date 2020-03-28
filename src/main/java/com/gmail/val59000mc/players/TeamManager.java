package com.gmail.val59000mc.players;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.configuration.MainConfiguration;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class TeamManager {

    private PlayersManager pm;
    private MainConfiguration cfg;
    private List<String> prefixes;
    private int lastTeamNumber;

    public TeamManager(){
        pm = GameManager.getGameManager().getPlayersManager();
        cfg = GameManager.getGameManager().getConfiguration();
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
            // When there are enough prefixes for the number of teams (and a new team), break
            if(cfg.getAvoidTeamColorVariations() && prefixes.size() > getUhcTeams().size()){
                break;
            }
            for (ChatColor color : colors){
                prefixes.add(color + colorEdit);
            }
        }

        // Change the prefixes of existing teams not in the new list of prefixes
        List<String> used = getUsedPrefixes();
        for (UhcTeam team : getUhcTeams()){
            // If the prefix is not the default and is not in the list of prefixes
            if(!team.getPrefix().equals("\u25A0 ") && !prefixes.contains(team.getPrefix())){
                // Give it a new prefix
                for (String prefix : prefixes){
                    if(!used.contains(prefix)){
                        team.setPrefix(prefix);
                        used.add(prefix);
                    }
                }
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
        loadPrefixes(); // Regenerate prefixes every time free ones are requested
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
        String prefix = null;
        for (String s : prefixes){
            if (!getUsedPrefixes().contains(s)){
                prefix = s;
            }
        }

        // Reload prefixes so there is one available for another team joining
        loadPrefixes();

        return prefix == null ? ChatColor.DARK_GRAY + "" : prefix;
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