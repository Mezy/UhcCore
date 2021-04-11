package com.gmail.val59000mc.jda;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.jda.commands.StartEventCommand;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.plugin.Plugin;

public class DiscordSRVListener {
    private final Plugin plugin;
    private CommandClient client;

    public DiscordSRVListener(Plugin plugin) {
        this.plugin = plugin;
        UhcCore.setEventWaiter(new EventWaiter());
    }

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        UhcCore.getPlugin().setDiscordSupported(true);
        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix("$");
        builder.addCommands(new StartEventCommand());

        builder.setOwnerId("306447845601312779");

        DiscordUtil.getJda().addEventListener(UhcCore.getEventWaiter());
        DiscordUtil.getJda().addEventListener(builder.build());
    }

    public CommandClient getCommandClient() {
        return client;
    }
}