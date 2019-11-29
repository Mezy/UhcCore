package com.gmail.val59000mc.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class SpigotUtils{

    public static boolean isSpigotServer(){
        try {
            Class.forName("net.md_5.bungee.api.chat.TextComponent");
            return true;
        }catch (ClassNotFoundException ex){
            return false;
        }
    }

    public static void sendMessage(Player player, String text, String hover, String suggest){
        TextComponent message = new TextComponent(text);
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest));
        player.spigot().sendMessage(message);
    }

}