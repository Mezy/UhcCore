package com.gmail.val59000mc.jda.commands;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StartEventCommand extends Command {

    public StartEventCommand() {
        this.name = "start-uhc";
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }

    @Override
    protected void execute(CommandEvent event) {
        EventWaiter eventWaiter = UhcCore.getEventWaiter();
        DiscordSRV DiscordAPI = UhcCore.getDiscordAPI();
        AccountLinkManager AccountLinkManager = DiscordAPI.getAccountLinkManager();
        OfflinePlayer player = Bukkit.getOfflinePlayer(AccountLinkManager.getUuid(event.getAuthor().getId()));
        if (!player.isOp()) {
            if (!(player.isOnline() && Bukkit.getPlayer(player.getUniqueId()).hasPermission("uhc-core.discord.channels.create"))) {
                event.getMessage().reply("You don't have permissions in minecraft!").queue();
                return;
            }
        }

        String CategoryID = UhcCore.getPlugin().getConfig().getString("discord.event-category-id");
        github.scarsz.discordsrv.dependencies.jda.api.entities.Category _category = DiscordAPI.getJda().getCategoryById(CategoryID);
        if (_category == null) {
            event.getMessage().reply("The Category provided in the config doesn't exists, creating a new one.").queue();
            _category = DiscordAPI.getMainGuild().createCategory("UHC Event").complete();
            UhcCore.getPlugin().getConfig().set("discord.event-category-id", _category.getId());
        } else _category.getManager().setName("UHC Event").queue();
        if (_category.getChannels().size() > 0) {
            event.getChannel().sendMessage("Found existing channels in the category, deleting them.").queue();
            for (GuildChannel channel : _category.getChannels())
                channel.delete().queue();
        }

        event.getMessage().reply("Please mention all the roles you want to participate in the event in one message.\nIf you want everyone to participate type `everyone` only").queue();
        github.scarsz.discordsrv.dependencies.jda.api.entities.Category category = _category;
        eventWaiter.waitForEvent(GuildMessageReceivedEvent.class, (e) -> e.getAuthor().equals(event.getAuthor()), (e) -> {
            Boolean isEveryone = e.getMessage().getContentRaw().toLowerCase() == "everyone";
            if (e.getMessage().getMentionedRoles().size() == 0 && !isEveryone) {
                e.getMessage().reply("You didn't mention any role, canceling...").queue();
                return;
            }
            List<Role> allowedRoles = e.getMessage().getMentionedRoles();
            TextChannel UHCChat = category.createTextChannel("UHC").complete();
            VoiceChannel UHCVoice = category.createVoiceChannel("UHC Pre game").complete();

            UHCChat.putPermissionOverride(UHCChat.getGuild().getPublicRole()).setAllow(Permission.VIEW_CHANNEL).setDeny(Permission.MESSAGE_WRITE);
            UHCVoice.putPermissionOverride(UHCChat.getGuild().getPublicRole()).setAllow(Permission.VIEW_CHANNEL).setDeny(Permission.VOICE_SPEAK);
            if (!isEveryone) {
                for (Role _allowedRole : allowedRoles) {
                    github.scarsz.discordsrv.dependencies.jda.api.entities.Role allowedRole = DiscordAPI.getMainGuild().getRoleById(_allowedRole.getIdLong());
                    UHCChat.putPermissionOverride(allowedRole).setAllow(Permission.VIEW_CHANNEL, Permission.MESSAGE_READ).queue();
                    UHCVoice.putPermissionOverride(allowedRole).setAllow(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT).queue();
                }
                UHCChat.putPermissionOverride(UHCChat.getGuild().getPublicRole()).setDeny(Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE);
                UHCVoice.putPermissionOverride(UHCChat.getGuild().getPublicRole()).setDeny(Permission.VIEW_CHANNEL, Permission.VOICE_SPEAK);
            }
            if (!isEveryone) {
                //Bukkit.getServer().getPluginManager().registerEvents(new PlayerPreLoginListener(allowedRoles.stream().mapToLong(r-> r.getIdLong()).toArray(), DiscordAPI), UhcCore.getPlugin());
            }
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("UHC Event")
                    .addField("IP", "ixprumxi.duckdns.org", true)
                    .addField("Version", "1.8", true)
                    .setDescription("Members must have one of these roles to play in this event:\n" + allowedRoles.stream().map((role) -> role.getAsMention()).collect(Collectors.joining(" - ")))
                    .build();
            UHCChat.sendMessage(embed).queue();

            String invite = UHCVoice.createInvite().complete().getUrl();
            for (Player _player : Bukkit.getOnlinePlayers()) {
                UhcPlayer uhcPlayer = GameManager.getGameManager().getPlayerManager().getUhcPlayer(_player);
                Member member = uhcPlayer.getDiscordUser();
                if (Collections.disjoint(member.getRoles(), allowedRoles) && !isEveryone) {
                    System.out.println("#1");
                    Bukkit.getScheduler().runTask(UhcCore.getPlugin(), () -> _player.kickPlayer("You aren't allowed to play in this event."));
                    continue;
                }
                if (member.getVoiceState().inVoiceChannel())
                    DiscordAPI.getMainGuild().moveVoiceMember(member, UHCVoice).queue();
                else {
                    _player.sendMessage("[UHC-Discord] Please enter the pre game voice channel: " + invite);
                }
            }
            e.getChannel().sendMessage("Notified " + Bukkit.getOnlinePlayers().size() + " players!").queue();
            UhcCore.getPlugin().setDiscordSupported(true);
            e.getMessage().reply("Done!").queue();
        });
    }
}
