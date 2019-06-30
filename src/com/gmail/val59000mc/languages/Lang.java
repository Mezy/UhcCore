package com.gmail.val59000mc.languages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Lang {
	public static String GAME_ENOUGH_TEAMS_READY;
	public static String GAME_STARTING;
	public static String GAME_PLEASE_WAIT_TELEPORTING;
	public static String GAME_STARTING_IN;
	public static String GAME_STARTING_CANCELLED;
	public static String GAME_FINISHED;
	public static String GAME_END_STOPPED;
	public static String GAME_SHUTDOWN;
	public static String GAME_SENDING_TO_HUB;
	public static String GAME_START_DEATHMATCH;
	public static String GAME_BORDER_START_SHRINKING;


    public static String PLAYERS_NETHER_OFF;
    public static String PLAYERS_BUILD_HEIGHT;
	public static String PLAYERS_WELCOME_NEW;
	public static String PLAYERS_WELCOME_BACK_IN_GAME;
	public static String PLAYERS_WELCOME_BACK_SPECTATING;
	public static String PLAYERS_ELIMINATED;
	public static String PLAYERS_WON;
	public static String PLAYERS_ALL_HAVE_LEFT;
	public static String PLAYERS_FF_OFF;
	public static String PLAYERS_SEND_BUNGEE;
	
	public static String DISPLAY_MESSAGE_PREFIX;
	public static String DISPLAY_EPISODE_MARK;
	public static String DISPLAY_MOTD_LOADING;
	public static String DISPLAY_MOTD_WAITING;
	public static String DISPLAY_MOTD_STARTING;
	public static String DISPLAY_MOTD_PLAYING;
	public static String DISPLAY_MOTD_ENDED;
	
	public static String KICK_LOADING;
	public static String KICK_STARTING;
	public static String KICK_PLAYING;
	public static String KICK_ENDED;
	public static String KICK_DEAD;
	

	public static String COMMAND_CHAT_GLOBAL;
	public static String COMMAND_CHAT_TEAM;
	public static String COMMAND_CHAT_HELP;
	public static String COMMAND_CHAT_ERROR;
	public static String COMMAND_SPECTATING_TELEPORT_ERROR;
	public static String COMMAND_SPECTATING_TELEPORT;
	public static String COMMAND_SPECTATING_HELP;
	
	public static String TEAM_PLAYER_NOT_ONLINE;
	public static String TEAM_PLAYER_JOIN_NOT_ONLINE;
	public static String TEAM_LEADER_JOIN_NOT_ONLINE;
	public static String TEAM_INVENTORY;
	public static String TEAM_CANNOT_JOIN_OWN_TEAM;
	public static String TEAM_NOW_READY;
	public static String TEAM_NOW_NOT_READY;
	public static String TEAM_READY;
	public static String TEAM_NOT_READY;
	public static String TEAM_READY_TOGGLE;
	public static String TEAM_READY_TOGGLE_ERROR;
	public static String TEAM_NOT_LEADER;
	public static String TEAM_JOIN_AS_PLAYER;
	public static String TEAM_PLAYER_JOINS;
	public static String TEAM_LEAVE_AS_LEADER;
	public static String TEAM_LEAVE_AS_PLAYER;
	public static String TEAM_PLAYER_LEAVES;
	public static String TEAM_LEADER_LEAVES;
	public static String TEAM_CANT_LEAVE;
	public static String TEAM_DENY_REQUEST;
	public static String TEAM_DENIED_REQUEST;
	public static String TEAM_NO_LONGER_EXISTS;
	public static String TEAM_REQUEST_HEAD;
	public static String TEAM_REQUEST_SENT;
	public static String TEAM_REQUEST_RECEIVED;
	public static String TEAM_REQUEST_ALREADY_SENT;
	public static String TEAM_ALREADY_IN_TEAM;
	public static String TEAM_PLAYER_ALREADY_IN_TEAM;
	public static String TEAM_FULL;
	
	public static String ITEMS_SWORD;
	public static String ITEMS_BARRIER;
	public static String ITEMS_REGEN_HEAD;
	public static String ITEMS_REGEN_HEAD_ACTION;
	public static String ITEMS_COMPASS_PLAYING;
	public static String ITEMS_COMPASS_PLAYING_ERROR;
	public static String ITEMS_COMPASS_PLAYING_POINTING;
	public static String ITEMS_KIT_SELECTION;
	public static String ITEMS_KIT_INVENTORY;
	public static String ITEMS_KIT_SELECTED;
	public static String ITEMS_KIT_NO_PERMISSION;
	public static String ITEMS_CRAFT_NO_PERMISSION;
	public static String ITEMS_CRAFT_CRAFTED;
	public static String ITEMS_CRAFT_LEFT_CLICK;
	public static String ITEMS_CRAFT_LIMIT;
	public static String ITEMS_CRAFT_BOOK;
	public static String ITEMS_CRAFT_BOOK_INVENTORY;
	public static String ITEMS_CRAFT_BOOK_BACK;
	public static String ITEMS_CRAFT_BANNED;
	public static String ITEMS_POTION_BANNED;
	

	public static String PVP_ENABLED;
	public static String PVP_START_IN;
	
	public static String EVENT_TIME_REWARD;
	public static String EVENT_KILL_REWARD;
	public static String EVENT_WIN_REWARD;
						
	
	public Lang(){
		loadLangConfig();
	}

	private void loadLangConfig() {
		File langFile = new File("plugins/UhcCore/lang.yml");
		if(langFile.exists()){
			FileConfiguration lang = YamlConfiguration.loadConfiguration(langFile);
			
			// Game
			GAME_ENOUGH_TEAMS_READY = ChatColor.translateAlternateColorCodes('&', lang.getString("game.enough-teams-ready"));
			Bukkit.getLogger().info(lang.getString("game.enough-teams-ready"));
			Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', lang.getString("game.enough-teams-ready")));
			Bukkit.getLogger().info(GAME_ENOUGH_TEAMS_READY);
			
			GAME_ENOUGH_TEAMS_READY = ChatColor.translateAlternateColorCodes('&', lang.getString("game.enough-teams-ready"));
			GAME_STARTING = ChatColor.translateAlternateColorCodes('&', lang.getString("game.starting"));
			GAME_STARTING_IN = ChatColor.translateAlternateColorCodes('&', lang.getString("game.starting-in"));
			GAME_STARTING_CANCELLED = ChatColor.translateAlternateColorCodes('&', lang.getString("game.starting-cancelled"));
			GAME_FINISHED = ChatColor.translateAlternateColorCodes('&', lang.getString("game.finished"));
			GAME_END_STOPPED = ChatColor.translateAlternateColorCodes('&', lang.getString("game.end-stopped"));
			GAME_SHUTDOWN = ChatColor.translateAlternateColorCodes('&', lang.getString("game.shutdown"));
			GAME_SENDING_TO_HUB = ChatColor.translateAlternateColorCodes('&', lang.getString("game.sending-to-hub"));
			GAME_PLEASE_WAIT_TELEPORTING = ChatColor.translateAlternateColorCodes('&', lang.getString("game.please-wait-teleporting"));
			GAME_START_DEATHMATCH = ChatColor.translateAlternateColorCodes('&', lang.getString("game.start-deathmatch"));
			GAME_BORDER_START_SHRINKING = ChatColor.translateAlternateColorCodes('&', lang.getString("game.border-start-shrinking"));
			
			// Players
            PLAYERS_NETHER_OFF = ChatColor.translateAlternateColorCodes('&', lang.getString("players.nether-off"));
            PLAYERS_BUILD_HEIGHT = ChatColor.translateAlternateColorCodes('&', lang.getString("players.build-height", "&cMax build height reached!"));
			PLAYERS_WELCOME_NEW = ChatColor.translateAlternateColorCodes('&', lang.getString("players.welcome-new"));
			PLAYERS_WELCOME_BACK_IN_GAME = ChatColor.translateAlternateColorCodes('&', lang.getString("players.welcome-back-in-game"));
			PLAYERS_WELCOME_BACK_SPECTATING = ChatColor.translateAlternateColorCodes('&', lang.getString("players.welcome-back-spectating"));
			PLAYERS_ELIMINATED = ChatColor.translateAlternateColorCodes('&', lang.getString("players.eliminated"));
			PLAYERS_WON = ChatColor.translateAlternateColorCodes('&', lang.getString("players.won"));
			PLAYERS_ALL_HAVE_LEFT = ChatColor.translateAlternateColorCodes('&', lang.getString("players.all-have-left"));
			PLAYERS_FF_OFF = ChatColor.translateAlternateColorCodes('&', lang.getString("players.ff-off"));
			PLAYERS_SEND_BUNGEE = ChatColor.translateAlternateColorCodes('&', lang.getString("players.send-bungee"));
			
			// Display
			DISPLAY_MESSAGE_PREFIX = ChatColor.translateAlternateColorCodes('&', lang.getString("display.message-prefix"));		
			DISPLAY_EPISODE_MARK = ChatColor.translateAlternateColorCodes('&', lang.getString("display.episode-mark", "End of episode %episode%!"));
			DISPLAY_MOTD_LOADING  = ChatColor.translateAlternateColorCodes('&', lang.getString("display.motd-loading"));
			DISPLAY_MOTD_WAITING  = ChatColor.translateAlternateColorCodes('&', lang.getString("display.motd-waiting"));
			DISPLAY_MOTD_STARTING  = ChatColor.translateAlternateColorCodes('&', lang.getString("display.motd-starting"));
			DISPLAY_MOTD_PLAYING  = ChatColor.translateAlternateColorCodes('&', lang.getString("display.motd-playing"));
			DISPLAY_MOTD_ENDED  = ChatColor.translateAlternateColorCodes('&', lang.getString("display.motd-ended"));
			
			// Kick
			KICK_LOADING = ChatColor.translateAlternateColorCodes('&', lang.getString("kick.loading"));	
			KICK_STARTING = ChatColor.translateAlternateColorCodes('&', lang.getString("kick.starting"));
			KICK_PLAYING = ChatColor.translateAlternateColorCodes('&', lang.getString("kick.playing"));
			KICK_ENDED = ChatColor.translateAlternateColorCodes('&', lang.getString("kick.ended"));
			KICK_DEAD = ChatColor.translateAlternateColorCodes('&', lang.getString("kick.dead"));
			
			// Command
			COMMAND_CHAT_GLOBAL = ChatColor.translateAlternateColorCodes('&', lang.getString("command.chat-global"));	
			COMMAND_CHAT_TEAM = ChatColor.translateAlternateColorCodes('&', lang.getString("command.chat-team"));	
			COMMAND_CHAT_HELP = ChatColor.translateAlternateColorCodes('&', lang.getString("command.chat-help"));	
			COMMAND_CHAT_ERROR = ChatColor.translateAlternateColorCodes('&', lang.getString("command.chat-error"));	
			COMMAND_SPECTATING_HELP = ChatColor.translateAlternateColorCodes('&', lang.getString("command.spectating-help"));
			COMMAND_SPECTATING_TELEPORT = ChatColor.translateAlternateColorCodes('&', lang.getString("command.spectating-teleport"));
			COMMAND_SPECTATING_TELEPORT_ERROR = ChatColor.translateAlternateColorCodes('&', lang.getString("command.spectating-teleport-error"));
			
			// Team
			TEAM_PLAYER_NOT_ONLINE = ChatColor.translateAlternateColorCodes('&', lang.getString("team.player-not-online"));	
			TEAM_PLAYER_JOIN_NOT_ONLINE = ChatColor.translateAlternateColorCodes('&', lang.getString("team.player-join-not-online"));	
			TEAM_LEADER_JOIN_NOT_ONLINE = ChatColor.translateAlternateColorCodes('&', lang.getString("team.leader-join-not-online"));	
			TEAM_INVENTORY  = ChatColor.translateAlternateColorCodes('&', lang.getString("team.inventory"));
			TEAM_CANNOT_JOIN_OWN_TEAM = ChatColor.translateAlternateColorCodes('&', lang.getString("team.cannot-join-own-team"));
			TEAM_READY_TOGGLE = ChatColor.translateAlternateColorCodes('&', lang.getString("team.ready-toggle"));
			TEAM_READY_TOGGLE_ERROR = ChatColor.translateAlternateColorCodes('&', lang.getString("team.ready-toggle-error"));
			TEAM_NOW_READY = ChatColor.translateAlternateColorCodes('&', lang.getString("team.now-ready"));
			TEAM_NOW_NOT_READY = ChatColor.translateAlternateColorCodes('&', lang.getString("team.now-not-ready"));
			TEAM_READY = ChatColor.translateAlternateColorCodes('&', lang.getString("team.ready"));
			TEAM_NOT_READY = ChatColor.translateAlternateColorCodes('&', lang.getString("team.not-ready"));
			TEAM_NOT_LEADER = ChatColor.translateAlternateColorCodes('&', lang.getString("team.not-leader"));
			TEAM_JOIN_AS_PLAYER = ChatColor.translateAlternateColorCodes('&', lang.getString("team.join-as-player"));
			TEAM_PLAYER_JOINS = ChatColor.translateAlternateColorCodes('&', lang.getString("team.player-joins"));
			TEAM_LEAVE_AS_LEADER = ChatColor.translateAlternateColorCodes('&', lang.getString("team.leave-as-leader"));
			TEAM_LEAVE_AS_PLAYER = ChatColor.translateAlternateColorCodes('&', lang.getString("team.leave-as-player"));
			TEAM_PLAYER_LEAVES = ChatColor.translateAlternateColorCodes('&', lang.getString("team.player-leaves"));
			TEAM_LEADER_LEAVES = ChatColor.translateAlternateColorCodes('&', lang.getString("team.leader-leaves"));
			TEAM_CANT_LEAVE = ChatColor.translateAlternateColorCodes('&', lang.getString("team.cant-leave"));
			TEAM_DENY_REQUEST = ChatColor.translateAlternateColorCodes('&', lang.getString("team.deny-request"));
			TEAM_DENIED_REQUEST = ChatColor.translateAlternateColorCodes('&', lang.getString("team.denied-request"));
			TEAM_NO_LONGER_EXISTS = ChatColor.translateAlternateColorCodes('&', lang.getString("team.no-longer-exists"));
			TEAM_REQUEST_HEAD = ChatColor.translateAlternateColorCodes('&', lang.getString("team.request-head"));
			TEAM_REQUEST_SENT = ChatColor.translateAlternateColorCodes('&', lang.getString("team.request-sent"));
			TEAM_REQUEST_RECEIVED = ChatColor.translateAlternateColorCodes('&', lang.getString("team.request-received"));
			TEAM_REQUEST_ALREADY_SENT = ChatColor.translateAlternateColorCodes('&', lang.getString("team.request-already-sent"));
			TEAM_ALREADY_IN_TEAM = ChatColor.translateAlternateColorCodes('&', lang.getString("team.already-in-team"));
			TEAM_PLAYER_ALREADY_IN_TEAM = ChatColor.translateAlternateColorCodes('&', lang.getString("team.player-already-in-team"));
			TEAM_FULL = ChatColor.translateAlternateColorCodes('&', lang.getString("team.full"));
			
			// Items
			ITEMS_SWORD = ChatColor.translateAlternateColorCodes('&', lang.getString("items.sword"));
			ITEMS_BARRIER = ChatColor.translateAlternateColorCodes('&', lang.getString("items.barrier"));
			ITEMS_REGEN_HEAD = ChatColor.translateAlternateColorCodes('&', lang.getString("items.regen-head"));
			ITEMS_REGEN_HEAD_ACTION = ChatColor.translateAlternateColorCodes('&', lang.getString("items.regen-head-action"));;
			ITEMS_COMPASS_PLAYING = ChatColor.translateAlternateColorCodes('&', lang.getString("items.compass-playing"));
			ITEMS_COMPASS_PLAYING_ERROR = ChatColor.translateAlternateColorCodes('&', lang.getString("items.compass-playing-error"));
			ITEMS_COMPASS_PLAYING_POINTING = ChatColor.translateAlternateColorCodes('&', lang.getString("items.compass-playing-pointing"));
			ITEMS_KIT_SELECTION = ChatColor.translateAlternateColorCodes('&', lang.getString("items.kit-selection"));
			ITEMS_KIT_INVENTORY = ChatColor.translateAlternateColorCodes('&', lang.getString("items.kit-inventory"));
			ITEMS_KIT_SELECTED =  ChatColor.translateAlternateColorCodes('&', lang.getString("items.kit-selected"));
			ITEMS_KIT_NO_PERMISSION = ChatColor.translateAlternateColorCodes('&', lang.getString("items.kit-no-permission"));
			ITEMS_CRAFT_NO_PERMISSION = ChatColor.translateAlternateColorCodes('&', lang.getString("items.craft-no-permission"));
			ITEMS_CRAFT_CRAFTED = ChatColor.translateAlternateColorCodes('&', lang.getString("items.craft-crafted"));
			ITEMS_CRAFT_LEFT_CLICK = ChatColor.translateAlternateColorCodes('&', lang.getString("items.craft-left-click"));
			ITEMS_CRAFT_LIMIT = ChatColor.translateAlternateColorCodes('&', lang.getString("items.craft-limit"));
			ITEMS_CRAFT_BOOK = ChatColor.translateAlternateColorCodes('&', lang.getString("items.craft-book"));
			ITEMS_CRAFT_BOOK_INVENTORY = ChatColor.translateAlternateColorCodes('&', lang.getString("items.craft-book-inventory"));
			ITEMS_CRAFT_BOOK_BACK = ChatColor.translateAlternateColorCodes('&', lang.getString("items.craft-book-back"));
			ITEMS_CRAFT_BANNED = ChatColor.translateAlternateColorCodes('&', lang.getString("items.craft-banned"));
			ITEMS_POTION_BANNED = ChatColor.translateAlternateColorCodes('&', lang.getString("items.potion-banned"));
			
			// PVP
			PVP_ENABLED = ChatColor.translateAlternateColorCodes('&', lang.getString("pvp.enabled"));
			PVP_START_IN = ChatColor.translateAlternateColorCodes('&', lang.getString("pvp.start-in"));
			
			// Event
			EVENT_TIME_REWARD = ChatColor.translateAlternateColorCodes('&', lang.getString("event.time-reward"));
			EVENT_KILL_REWARD = ChatColor.translateAlternateColorCodes('&', lang.getString("event.kill-reward"));
			EVENT_WIN_REWARD = ChatColor.translateAlternateColorCodes('&', lang.getString("event.win-reward"));
		}else{
			try {
				saveDefaultEnglighLang();
				loadLangConfig();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	}

	private void saveDefaultEnglighLang() throws IOException {
		File langFile = new File("plugins/UhcCore/lang.yml");
		langFile.createNewFile();
		FileConfiguration lang = YamlConfiguration.loadConfiguration(langFile);
		
		// Game
		lang.set("game.enough-teams-ready", "Ok, enough teams are ready.");
		lang.set("game.starting", "Starting the game now !");
		lang.set("game.starting-in", "Starting in %time% seconds.");
		lang.set("game.starting-cancelled", "Game starting was cancelled because not enough teams are ready");
		lang.set("game.finished", "The game is finished !");
		lang.set("game.end-stopped", "Game ending stopped");
		lang.set("game.shutdown", "Server will shutdown in %time% seconds.");
		lang.set("game.sending-to-hub", "Sending you to the lobby");
		lang.set("game.please-wait-teleporting", "Please wait while all players are being teleported.");
		lang.set("game.start-deathmatch", "Starting the deathmatch ! Prepare yourself until PVP is enabled !");
		lang.set("game.border-start-shrinking", "The border will now begin to shrink");
		
		// Players
		lang.set("players.nether-off", "The nether has been deactivated for this game.");
		lang.set("players.build-height", "&cMax build height reached!");
		lang.set("players.welcome-new", "Welcome to UHC, please select your team");
		lang.set("players.welcome-back-in-game", "You logged back in the game");
		lang.set("players.welcome-back-spectating", "You are dead and are now spectating.");
		lang.set("players.eliminated", "%player% has been eliminated !");
		lang.set("players.won", "%player% won the game !");
		lang.set("players.all-have-left", "All players have left, game will end in");
		lang.set("players.ff-off", "Friendly-Fire is disabled");
		lang.set("players.send-bungee", "Sending you to the hub in %time%");

		// Display
		lang.set("display.message-prefix", "[UhcCore]");
		lang.set("display.episode-mark", "End of episode %episode%!");
		lang.set("display.motd-loading", "Loading ...");
		lang.set("display.motd-waiting", "Waiting ...");
		lang.set("display.motd-starting", "Starting");
		lang.set("display.motd-playing", "Playing");
		lang.set("display.motd-ended", "Ended");
		
		// Kick
		lang.set("kick.loading", "Loading. Please retry in a few minutes.");
		lang.set("kick.starting", "Starting ... Too late to join.");
		lang.set("kick.playing", "Playing ... You can't join.");
		lang.set("kick.ended", "Ended ... Please retry in a few minutes.");
		lang.set("kick.dead", "You are dead !");
		
		// Command
		lang.set("command.chat-global", "You are now talking to everyone");
		lang.set("command.chat-team", "You are now talking to your team");
		lang.set("command.chat-help", "Type '/chat' or '/c' to toggle global chat");
		lang.set("command.chat-error", "You can only use that command while playing");
		lang.set("command.spectating-teleport-error", "You can't teleport to that player");
		lang.set("command.spectating-teleport", "Teleporting to %player%");
		lang.set("command.spectating-help", "Use '/teleport <player>' to teleport to a playing player");
		
		// Team
		lang.set("team.player-not-online", "%player% isn't online.");
		lang.set("team.player-join-not-online", "That player isn't online, he can't join your team");
		lang.set("team.leader-join-not-online", "The team leader isn't online, you can't join his team");
		lang.set("team.inventory", "Team selection");
		lang.set("team.cannot-join-own-team", "You can't join your own team");
		lang.set("team.ready-toggle", "Click to change");
		lang.set("team.ready-toggle-error", "The game is starting, you can't change that now !");
		lang.set("team.now-ready", "Your team is now ready !");
		lang.set("team.now-not-ready", "Your team is now NOT ready !");
		lang.set("team.ready", "Ready");
		lang.set("team.not-ready", "Not ready");
		lang.set("team.not-leader", "You are not the leader of that team");
		lang.set("team.join-as-player", "You have join %leader%'s team");
		lang.set("team.player-joins", "%player% has joined the team");
		lang.set("team.leave-as-leader", "You have left your team, %newleader% will be the new leader");
		lang.set("team.leave-as-player", "You have left the team");
		lang.set("team.player-leaves", "%player% has left the team");
		lang.set("team.leader-leaves", "Team leader %leader% has left the team, %newleader% is the new leader");
		lang.set("team.cant-leave", "You can't leave your team, you are alone.");
		lang.set("team.deny-request", "You denied %player% to join your team.");
		lang.set("team.denied-request", "The team leader %leader% denied your team request.");		
		lang.set("team.no-longer-exists", "That team no longer exists.");		
		lang.set("team.request-head", "Team request");
		lang.set("team.request-sent", "Request sent to %leader%");
		lang.set("team.request-received", "%player% has sent you a team request, Right click to accept, Throw it to deny");
		lang.set("team.request-already-sent", "You have already sent a request to that team");
		lang.set("team.already-in-team", "You are already in a team");
		lang.set("team.player-already-in-team", "%player% is already in a team");
		lang.set("team.full", "%player% cannot join %leader%'s team because the team is full (%limit% players)");
		
		// Items
		lang.set("items.sword", "Right click to choose your team");
		lang.set("items.barrier", "Leave your team");
		lang.set("items.regen-head", "Right click to regen your team for 5 seconds");
		lang.set("items.regen-head-action", "You get a 5 seconds regen effect for eating a player head");
		lang.set("items.compass-playing", "Right click to point to a teammate");
		lang.set("items.compass-playing-error", "There is no playing teammate to point to.");
		lang.set("items.compass-playing-pointing", "Pointing towards %player%'s last location");
		lang.set("items.kit-selection", "Right click to choose a kit");
		lang.set("items.kit-inventory", "Kit selection");
		lang.set("items.kit-selected", "You selected the kit %kit%");
		lang.set("items.kit-no-permission", "You don't have the permission to use that kit");
		lang.set("items.craft-no-permission", "You don't have the permission to craft %craft%");
		lang.set("items.craft-left-click", "You can only craft one %craft% at a time (left click).");
		lang.set("items.craft-limit", "You have used all of your %limit% %craft% crafts.");
		lang.set("items.craft-crafted", "You have crafted a %craft%");
		lang.set("items.craft-book", "Right click to see the custom crafts");
		lang.set("items.craft-book-inventory", "Custom crafts");
		lang.set("items.craft-book-back", "Back to crafts list");
		lang.set("items.craft-banned", "Sorry, this craft is banned.");
		lang.set("items.potion-banned", "Sorry, level 2 potions are banned.");
		
		// PVP
		lang.set("pvp.enabled", "PVP enabled !");
		lang.set("pvp.start-in", "PVP will start in");
		
		// Event
		lang.set("event.time-reward", "&eYou have received %money% in your account for playing %time% , total playing time %totaltime%");
		lang.set("event.kill-reward", "&eYou have received %money% in your account for killing a player");
		lang.set("event.win-reward", "&eYou have received %money% in your account for winning the game");
		
		lang.save(langFile);
	}
}
