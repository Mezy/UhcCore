package com.gmail.val59000mc.playuhc.mc1_13.scoreboard;

import com.gmail.val59000mc.playuhc.PlayUhc;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardLayout {

    private List<String> waiting;
    private List<String> playing;
    private List<String> deathmatch;
    private List<String> spectating;
    private String title;

    public void loadFile(){

        PlayUhc.getPlugin().saveResource("scoreboard.yml",false);

        File file = new File(PlayUhc.getPlugin().getDataFolder() + "/scoreboard.yml");

        Configuration cfg = YamlConfiguration.loadConfiguration(file);

        waiting = getOpsideDownLines(cfg.getStringList("waiting"));
        playing = getOpsideDownLines(cfg.getStringList("playing"));
        deathmatch = getOpsideDownLines(cfg.getStringList("deathmatch"));
        spectating = getOpsideDownLines(cfg.getStringList("spectating"));
        title = ChatColor.translateAlternateColorCodes('&',cfg.getString("title"));

    }

    public List<String> getLines(ScoreboardType scoreboardType){
        if (scoreboardType.equals(ScoreboardType.WAITING)){
            return waiting;
        }
        if (scoreboardType.equals(ScoreboardType.PLAYING)){
            return playing;
        }
        if (scoreboardType.equals(ScoreboardType.DEATHMATCH)){
            return deathmatch;
        }
        if (scoreboardType.equals(ScoreboardType.SPECTATING)){
            return spectating;
        }
        return null;
    }

    public void setLines(ScoreboardType scoreboardType, List<String> lines){
        switch (scoreboardType){
            case WAITING:
                waiting = getOpsideDownLines(lines);
                break;
            case PLAYING:
                playing = getOpsideDownLines(lines);
                break;
            case DEATHMATCH:
                deathmatch = getOpsideDownLines(lines);
                break;
            case SPECTATING:
                spectating = getOpsideDownLines(lines);
                break;
        }
    }

    public String getTitle() {
        return title;
    }

    private List<String> getOpsideDownLines(List<String> list){

        List<String> newList = new ArrayList<>();

        for (int i = list.size()-1; i >= 0; i--){
            newList.add(ChatColor.translateAlternateColorCodes('&',list.get(i)));
        }

        return newList;

    }

}