package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.players.UhcPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpigotUtils{

    public enum Action{
        COMMAND,
        SUGGEST
    }

    public static void sendMessage(UhcPlayer uhcPlayer, String message, String hover, String event, Action action){
        try{
            sendMessage(uhcPlayer.getPlayer(), message, hover, event, action);
        }catch (UhcPlayerNotOnlineException ex){
            // No messages for offline players
        }
    }

    public static void sendMessage(Player player, String message, String hover, String event, Action action){
        TextComponent textComponent = new TextComponent(insertLastColorCodes(message));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        if (action == Action.SUGGEST){
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, event));
        }else if (action == Action.COMMAND){
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, event));
        }
        player.spigot().sendMessage(textComponent);
    }

    private static String insertLastColorCodes(String s){
        String[] words = s.split(" ");

        StringBuilder sb = new StringBuilder();

        String lastColor = "";
        for (String word : words){
            word = lastColor + word;
            sb.append(word);
            sb.append(" ");
            lastColor = ChatColor.getLastColors(word);
        }

        return sb.toString();
    }

}