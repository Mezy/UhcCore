package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import com.gmail.val59000mc.utils.UniversalSound;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.VoiceChannel;
import org.bukkit.Bukkit;

import java.util.List;

public class PreStartThread implements Runnable{

	private static PreStartThread instance;

	private final GameManager gameManager;
	private final int timeBeforeStart;
	private int remainingTime;
	private final int minPlayers;
	private final boolean teamsAlwaysReady;
	private boolean pause, force;
	
	public PreStartThread(GameManager gameManager){
		this.gameManager = gameManager;
		MainConfig cfg = gameManager.getConfig();
		instance = this;
		this.timeBeforeStart = cfg.get(MainConfig.TIME_BEFORE_START_WHEN_READY);
		this.remainingTime = cfg.get(MainConfig.TIME_BEFORE_START_WHEN_READY);
		this.minPlayers = cfg.get(MainConfig.MIN_PLAYERS_TO_START);
		this.teamsAlwaysReady = cfg.get(MainConfig.TEAM_ALWAYS_READY);
		this.pause = false;
		this.force = false;
	}
	
	public static String togglePause(){
		instance.pause = !instance.pause;
		return "pause:"+instance.pause+"  "+"force:"+instance.force;
	}
	
	public static String toggleForce(){
		instance.force = !instance.force;
		return "pause:"+instance.pause+"  "+"force:"+instance.force;
	}
	
	@Override
	public void run() {
		List<UhcTeam> teams = gameManager.getPlayerManager().listUhcTeams();
		double readyTeams = 0;
		double teamsNumber = teams.size();

		for(UhcTeam team : teams){
			if((teamsAlwaysReady || team.isReadyToStart()) && team.isOnline()) {
				readyTeams += 1;
			}
		}

		double percentageReadyTeams = 100*readyTeams/teamsNumber;
		int playersNumber = Bukkit.getOnlinePlayers().size();

		if(
				force ||
				(!pause && (remainingTime < 5 || (playersNumber >= minPlayers && readyTeams >= gameManager.getConfig().get(MainConfig.MINIMAL_READY_TEAMS_TO_START) && percentageReadyTeams >= gameManager.getConfig().get(MainConfig.MINIMAL_READY_TEAMS_PERCENTAGE_TO_START))))
		){
			if(remainingTime == timeBeforeStart+1){
				gameManager.broadcastInfoMessage(Lang.GAME_ENOUGH_TEAMS_READY);
				gameManager.broadcastInfoMessage(Lang.GAME_STARTING_IN.replace("%time%", String.valueOf(remainingTime)));
				gameManager.getPlayerManager().playSoundToAll(UniversalSound.CLICK);
			}else if((remainingTime > 0 && remainingTime <= 10) || (remainingTime > 0 && remainingTime%10 == 0)){
				gameManager.broadcastInfoMessage(Lang.GAME_STARTING_IN.replace("%time%", String.valueOf(remainingTime)));
				gameManager.getPlayerManager().playSoundToAll(UniversalSound.CLICK);
			}

			remainingTime--;

			if(remainingTime == -1) {
				if (UhcCore.getPlugin().isDiscordSupported()) {
					DiscordSRV DiscordAPI = UhcCore.getDiscordAPI();
					String CategoryID = UhcCore.getPlugin().getConfig().getString("discord.event-category-id");
					github.scarsz.discordsrv.dependencies.jda.api.entities.Category category = DiscordAPI.getJda().getCategoryById(CategoryID);
					if (category == null) {
						category = DiscordAPI.getMainGuild().createCategory("UHC Event").complete();
						UhcCore.getPlugin().getConfig().set("discord.event-category-id", category.getId());
					} else category.getManager().setName("UHC Event").queue();
					for (UhcTeam team : teams) {
						String channelName = "Team " + team.getTeamNumber();
						if (team.getTeamName() != null) channelName = team.getTeamName();
						VoiceChannel teamChannel = category.createVoiceChannel(channelName).complete();
						team.setTeamChannel(teamChannel);
						teamChannel.putPermissionOverride(DiscordAPI.getMainGuild().getPublicRole()).setDeny(Permission.VIEW_CHANNEL).queue();
						for (UhcPlayer uhcPlayer : team.getMembers()) {
							Member member = uhcPlayer.getDiscordUser();
							teamChannel.putPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT).queue();
							if (member.getVoiceState().inVoiceChannel())
								DiscordAPI.getMainGuild().moveVoiceMember(member, teamChannel).queue();
							else {
								uhcPlayer.sendMessage("[UHC] Please enter the voice channel for your team named: " + channelName + "\n" + teamChannel.createInvite().complete().getUrl());
							}
						}
					}
				}
				GameManager.getGameManager().startGame();
			}
			else{
				Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this, 20);
			}
		}else{
			if(!pause && remainingTime < timeBeforeStart+1){
				gameManager.broadcastInfoMessage(Lang.GAME_STARTING_CANCELLED);
			}
			remainingTime = timeBeforeStart+1;
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this,20);
		}
	}

}