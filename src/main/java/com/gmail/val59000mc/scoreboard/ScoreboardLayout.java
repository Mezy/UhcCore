package com.gmail.val59000mc.scoreboard;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.scoreboard.placeholders.AnimationPlaceholder;
import com.gmail.val59000mc.utils.FileUtils;
import com.gmail.val59000mc.configuration.YamlFile;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardLayout {

    private List<String> waiting;
    private List<String> playing;
    private List<String> deathmatch;
    private List<String> spectating;
    private String title;

    public void loadFile(){
        YamlFile cfg = FileUtils.saveResourceIfNotAvailable("scoreboard.yml");
        waiting = getOpsideDownLines(cfg.getStringList("waiting"));
        playing = getOpsideDownLines(cfg.getStringList("playing"));
        deathmatch = getOpsideDownLines(cfg.getStringList("deathmatch"));
        spectating = getOpsideDownLines(cfg.getStringList("spectating"));
        title = ChatColor.translateAlternateColorCodes('&',cfg.getString("title"));

        ConfigurationSection animations = cfg.getConfigurationSection("animations");
        if (animations == null){
            return;
        }

        for (String key : animations.getKeys(false)){
            loadAnimation(key, animations.getConfigurationSection(key));
        }
    }

    private void loadAnimation(String key, ConfigurationSection section){
        int speed = section.getInt("speed", 1);
        List<String> stages = parseColors(section.getStringList("stages"));

        Placeholder placeholder = new AnimationPlaceholder(key, speed, stages);
        GameManager.getGameManager().getScoreboardManager().registerPlaceholder(placeholder);
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

    private List<String> parseColors(List<String> lines){
        List<String> parsedLines = new ArrayList<>();
        lines.forEach(line -> parsedLines.add(ChatColor.translateAlternateColorCodes('&', line)));
        return parsedLines;
    }

}