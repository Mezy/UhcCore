package com.gmail.val59000mc.scoreboard;

import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.entity.Player;

/**
 * Placeholders are used to get custom dynamic data on the scoreboard.
 * You must register them using the {@link ScoreboardManager#registerPlaceholder(Placeholder)} method.
 */
public abstract class Placeholder{

    private final String[] placeholders;

    /**
     * Include the placeholders you want this placeholder object to deal with.
     * @param placeholders placeholders without '%' characters.
     */
    public Placeholder(String... placeholders){
        this.placeholders = placeholders;
    }

    /**
     * Used to get the placeholders.
     * @return Returns the placeholders without '%' characters.
     */
    public String[] getPlaceholders() {
        return placeholders;
    }

    /**
     * Used to get replacements for placeholder.
     * This method must be overridden in your custom placeholder!
     * @param uhcPlayer The {@link UhcPlayer} the replacement is requested for.
     * @param player The {@link Player} the replacement is requested for.
     * @param scoreboardType The {@link ScoreboardType} at the time of getting the placeholder.
     * @param placeholder The placeholder found in the replacement string.
     * @return Returns the replacement.
     */
    public abstract String getReplacement(UhcPlayer uhcPlayer, Player player, ScoreboardType scoreboardType, String placeholder);

    /**
     * Used to parse a string trough the placeholder.
     * @param string The parsed string.
     * @param uhcPlayer The {@link UhcPlayer} the string is parsed for.
     * @param player The {@link Player} the string is parsed for.
     * @param scoreboardType The {@link ScoreboardType} the string is parsed for.
     * @return Returns the string with parsed placeholders.
     */
    public String parseString(String string, UhcPlayer uhcPlayer, Player player, ScoreboardType scoreboardType){
        for (String placeholder : placeholders){
            if (string.contains("%"+placeholder+"%")){
                string = string.replace("%"+placeholder+"%", getReplacement(uhcPlayer, player, scoreboardType, placeholder));
            }
        }
        return string;
    }

}