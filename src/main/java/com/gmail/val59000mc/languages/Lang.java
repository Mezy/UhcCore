package com.gmail.val59000mc.languages;

import com.gmail.val59000mc.configuration.YamlFile;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lang {

	public static String GAME_ENOUGH_TEAMS_READY;
	public static String GAME_STARTING;
	public static String GAME_PLEASE_WAIT_TELEPORTING;
	public static String GAME_STARTING_IN;
	public static String GAME_STARTING_CANCELLED;
	public static String GAME_FINISHED;
	public static String GAME_END_STOPPED;
	public static String GAME_SHUTDOWN;
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
	public static String PLAYERS_SEND_BUNGEE_NOW;
	public static String PLAYERS_SEND_BUNGEE_DISABLED;


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
	public static String ITEMS_BUNGEE;
	public static String ITEMS_BARRIER;
	public static String ITEMS_REGEN_HEAD;
	public static String ITEMS_REGEN_HEAD_ACTION;
	public static String ITEMS_GOLDEN_HEAD_SKULL_NAME;
	public static String ITEMS_GOLDEN_HEAD_SKULL_HELP;
	public static String ITEMS_GOLDEN_HEAD_APPLE_NAME;
	public static String ITEMS_GOLDEN_HEAD_APPLE_HELP;
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
	public static String ITEMS_POTION_BANNED;

	public static String PVP_ENABLED;
	public static String PVP_START_IN;

	public static String EVENT_TIME_REWARD;
	public static String EVENT_KILL_REWARD;
	public static String EVENT_WIN_REWARD;

	public static String SCENARIO_GLOBAL_INVENTORY;
	public static String SCENARIO_GLOBAL_INVENTORY_EDIT;
	public static String SCENARIO_GLOBAL_INVENTORY_VOTE;
	public static String SCENARIO_GLOBAL_ITEM_EDIT;
	public static String SCENARIO_GLOBAL_ITEM_BACK;
	public static String SCENARIO_GLOBAL_ITEM_HOTBAR;
	public static String SCENARIO_GLOBAL_VOTE_MAX;

	public static String SCENARIO_BESTPVE_ADDED;
	public static String SCENARIO_BESTPVE_REMOVED;
	public static String SCENARIO_BESTPVE_BACK;
	public static String SCENARIO_BOWLESS_ERROR;
	public static String SCENARIO_HORSELESS_ERROR;
	public static String SCENARIO_NOCLEAN_INVULNERABLE;
	public static String SCENARIO_NOCLEAN_VULNERABLE;
	public static String SCENARIO_NOCLEAN_ERROR;
	public static String SCENARIO_RODLESS_ERROR;
	public static String SCENARIO_SWITCHEROO_SWITCH;
	public static String SCENARIO_LOVEATFIRSTSIGHT_JOIN_ERROR;

	public Lang(){
		loadLangConfig();
	}

	private void loadLangConfig() {
		File langFile = new File("plugins/UhcCore/lang.yml");
		if(!langFile.exists()) {
			try {
				langFile.createNewFile();
			}catch (IOException ex){
				Bukkit.getLogger().severe("[UhcCore] Failed to create " + langFile.toString());
				ex.printStackTrace();
				return;
			}
		}

		YamlFile lang = FileUtils.saveResourceIfNotAvailable("lang.yml");

		// Game
		GAME_ENOUGH_TEAMS_READY = getString(lang, "game.enough-teams-ready", "Ok, enough teams are ready.");
		GAME_STARTING = getString(lang, "game.starting", "Starting the game now!");
		GAME_STARTING_IN = getString(lang, "game.starting-in", "Starting in %time% seconds.");
		GAME_STARTING_CANCELLED = getString(lang, "game.starting-cancelled", "Game starting was cancelled because not enough teams are ready");
		GAME_FINISHED = getString(lang, "game.finished", "The game has finished!");
		GAME_END_STOPPED = getString(lang, "game.end-stopped", "Game ending stopped");
		GAME_SHUTDOWN = getString(lang, "game.shutdown", "Server will shutdown in %time% seconds.");
		GAME_PLEASE_WAIT_TELEPORTING = getString(lang, "game.please-wait-teleporting", "Please wait while all players are being teleported.");
		GAME_START_DEATHMATCH = getString(lang, "game.start-deathmatch", "Starting the deathmatch! Prepare yourself until PVP is enabled!");
		GAME_BORDER_START_SHRINKING = getString(lang, "game.border-start-shrinking", "The border will now begin to shrink");

		// Players
        PLAYERS_NETHER_OFF = getString(lang, "players.nether-off", "The nether has been deactivated for this game.");
        PLAYERS_BUILD_HEIGHT = getString(lang, "players.build-height", "&cMax build height reached!");
		PLAYERS_WELCOME_NEW = getString(lang, "players.welcome-new", "Welcome to UHC, please select your team");
		PLAYERS_WELCOME_BACK_IN_GAME = getString(lang, "players.welcome-back-in-game", "You logged back in the game");
		PLAYERS_WELCOME_BACK_SPECTATING = getString(lang, "players.welcome-back-spectating", "You are dead and are now spectating.");
		PLAYERS_ELIMINATED = getString(lang, "players.eliminated", "%player% has been eliminated!");
		PLAYERS_WON = getString(lang, "players.won", "%player% won the game!");
		PLAYERS_ALL_HAVE_LEFT = getString(lang, "players.all-have-left", "All players have left, game will end in");
		PLAYERS_FF_OFF = getString(lang, "players.ff-off", "Friendly-Fire is disabled");
		PLAYERS_SEND_BUNGEE = getString(lang, "players.send-bungee", "Sending you to the hub in %time%");
		PLAYERS_SEND_BUNGEE_NOW = getString(lang, "players.send-bungee-now", "&eConnecting to Hub ...");
		PLAYERS_SEND_BUNGEE_DISABLED = getString(lang, "players.send-bungee-disabled", "&cThis command is disabled");

		// Display
		DISPLAY_MESSAGE_PREFIX = getString(lang, "display.message-prefix", "[UhcCore]");
		DISPLAY_EPISODE_MARK = getString(lang, "display.episode-mark", "End of episode %episode%!");
		DISPLAY_MOTD_LOADING  = getString(lang, "display.motd-loading", "Loading ...");
		DISPLAY_MOTD_WAITING  = getString(lang, "display.motd-waiting", "Waiting ...");
		DISPLAY_MOTD_STARTING  = getString(lang, "display.motd-starting", "Starting");
		DISPLAY_MOTD_PLAYING  = getString(lang, "display.motd-playing", "Playing");
		DISPLAY_MOTD_ENDED  = getString(lang, "display.motd-ended", "Ended");

		// Kick
		KICK_LOADING = getString(lang, "kick.loading", "Loading. Please retry in a few minutes.");
		KICK_STARTING = getString(lang, "kick.starting", "Starting ... Too late to join.");
		KICK_PLAYING = getString(lang, "kick.playing", "Playing ... You can't join.");
		KICK_ENDED = getString(lang, "kick.ended", "Ended ... Please retry in a few minutes.");
		KICK_DEAD = getString(lang, "kick.dead", "You are dead!");

		// Command
		COMMAND_CHAT_GLOBAL = getString(lang, "command.chat-global", "You are now talking to everyone");
		COMMAND_CHAT_TEAM = getString(lang, "command.chat-team", "You are now talking to your team");
		COMMAND_CHAT_HELP = getString(lang, "command.chat-help", "Type '/chat' or '/c' to toggle global chat");
		COMMAND_CHAT_ERROR = getString(lang, "command.chat-error", "You can only use that command while playing");
		COMMAND_SPECTATING_HELP = getString(lang, "command.spectating-help", "Use '/teleport <player>' to teleport to a playing player");
		COMMAND_SPECTATING_TELEPORT = getString(lang, "command.spectating-teleport", "Teleporting to %player%");
		COMMAND_SPECTATING_TELEPORT_ERROR = getString(lang, "command.spectating-teleport-error", "You can't teleport to that player");

		// Team
		TEAM_PLAYER_NOT_ONLINE = getString(lang, "team.player-not-online", "%player% isn't online.");
		TEAM_PLAYER_JOIN_NOT_ONLINE = getString(lang, "team.player-join-not-online", "That player isn't online, he can't join your team");
		TEAM_LEADER_JOIN_NOT_ONLINE = getString(lang, "team.leader-join-not-online", "The team leader isn't online, you can't join his team");
		TEAM_INVENTORY  = getString(lang, "team.inventory", "Team selection");
		TEAM_CANNOT_JOIN_OWN_TEAM = getString(lang, "team.cannot-join-own-team", "You can't join your own team");
		TEAM_READY_TOGGLE = getString(lang, "team.ready-toggle", "Click to change");
		TEAM_READY_TOGGLE_ERROR = getString(lang, "team.ready-toggle-error", "The game is starting, you can't change that now!");
		TEAM_NOW_READY = getString(lang, "team.now-ready", "Your team is now ready!");
		TEAM_NOW_NOT_READY = getString(lang, "team.now-not-ready", "Your team is now NOT ready!");
		TEAM_READY = getString(lang, "team.ready", "Ready");
		TEAM_NOT_READY = getString(lang, "team.not-ready", "Not ready");
		TEAM_NOT_LEADER = getString(lang, "team.not-leader", "You are not the leader of that team");
		TEAM_JOIN_AS_PLAYER = getString(lang, "team.join-as-player", "You have join %leader%'s team");
		TEAM_PLAYER_JOINS = getString(lang, "team.player-joins", "%player% has joined the team");
		TEAM_LEAVE_AS_LEADER = getString(lang, "team.leave-as-leader", "You have left your team, %newleader% will be the new leader");
		TEAM_LEAVE_AS_PLAYER = getString(lang, "team.leave-as-player", "You have left the team");
		TEAM_PLAYER_LEAVES = getString(lang, "team.player-leaves", "%player% has left the team");
		TEAM_LEADER_LEAVES = getString(lang, "team.leader-leaves", "Team leader %leader% has left the team, %newleader% is the new leader");
		TEAM_CANT_LEAVE = getString(lang, "team.cant-leave", "You can't leave your team, you are alone.");
		TEAM_DENY_REQUEST = getString(lang, "team.deny-request", "You denied %player% to join your team.");
		TEAM_DENIED_REQUEST = getString(lang, "team.denied-request", "The team leader %leader% denied your team request.");
		TEAM_NO_LONGER_EXISTS = getString(lang, "team.no-longer-exists", "That team no longer exists.");
		TEAM_REQUEST_HEAD = getString(lang, "team.request-head", "Team request");
		TEAM_REQUEST_SENT = getString(lang, "team.request-sent", "Request sent to %leader%");
		TEAM_REQUEST_RECEIVED = getString(lang, "team.request-received", "%player% has sent you a team request, Right click to accept, Throw it to deny");
		TEAM_REQUEST_ALREADY_SENT = getString(lang, "team.request-already-sent", "You have already sent a request to that team");
		TEAM_ALREADY_IN_TEAM = getString(lang, "team.already-in-team", "You are already in a team");
		TEAM_PLAYER_ALREADY_IN_TEAM = getString(lang, "team.player-already-in-team", "%player% is already in a team");
		TEAM_FULL = getString(lang, "team.full", "%player% cannot join %leader%'s team because the team is full (%limit% players)");

		// Items
		ITEMS_SWORD = getString(lang, "items.sword", "Right click to choose your team");
		ITEMS_BUNGEE = getString(lang, "items.bungee", "&6Right click to go back to the Hub");
		ITEMS_BARRIER = getString(lang, "items.barrier", "Leave your team");
		ITEMS_REGEN_HEAD = getString(lang, "items.regen-head", "Right click to regen your team for 5 seconds");
		ITEMS_REGEN_HEAD_ACTION = getString(lang, "items.regen-head-action", "You get a 5 seconds regen effect for eating a player head");
		ITEMS_GOLDEN_HEAD_SKULL_NAME = getString(lang, "items.golden-head.skull-name", "&6%player%'s head");
		ITEMS_GOLDEN_HEAD_SKULL_HELP = getString(lang, "items.golden-head.skull-help", "&eCombine the head with gold to get a golden head.");
		ITEMS_GOLDEN_HEAD_APPLE_NAME = getString(lang, "items.golden-head.apple-name", "&6Golden Head");
		ITEMS_GOLDEN_HEAD_APPLE_HELP = getString(lang, "items.golden-head.apple-help", "&eEat this to heal 4 hearts and get 2 absorption hearts.");
		ITEMS_COMPASS_PLAYING = getString(lang, "items.compass-playing", "Right click to point to a teammate");
		ITEMS_COMPASS_PLAYING_ERROR = getString(lang, "items.compass-playing-error", "There is no playing teammate to point to.");
		ITEMS_COMPASS_PLAYING_POINTING = getString(lang, "items.compass-playing-pointing", "Pointing towards %player%'s last location");
		ITEMS_KIT_SELECTION = getString(lang, "items.kit-selection", "Right click to choose a kit");
		ITEMS_KIT_INVENTORY = getString(lang, "items.kit-inventory", "Kit selection");
		ITEMS_KIT_SELECTED =  getString(lang, "items.kit-selected", "You selected the kit %kit%");
		ITEMS_KIT_NO_PERMISSION = getString(lang, "items.kit-no-permission", "You don't have the permission to use that kit");
		ITEMS_CRAFT_NO_PERMISSION = getString(lang, "items.craft-no-permission", "You don't have the permission to craft %craft%");
		ITEMS_CRAFT_CRAFTED = getString(lang, "items.craft-crafted", "You have crafted a %craft%");
		ITEMS_CRAFT_LEFT_CLICK = getString(lang, "items.craft-left-click", "You can only craft one %craft% at a time (left click).");
		ITEMS_CRAFT_LIMIT = getString(lang, "items.craft-limit", "You have used all of your %limit% %craft% crafts.");
		ITEMS_CRAFT_BOOK = getString(lang, "items.craft-book", "Right click to see the custom crafts");
		ITEMS_CRAFT_BOOK_INVENTORY = getString(lang, "items.craft-book-inventory", "Custom crafts");
		ITEMS_CRAFT_BOOK_BACK = getString(lang, "items.craft-book-back", "Back to crafts list");
		ITEMS_POTION_BANNED = getString(lang, "items.potion-banned", "Sorry, level 2 potions are banned.");

		// PVP
		PVP_ENABLED = getString(lang, "pvp.enabled", "PVP enabled!");
		PVP_START_IN = getString(lang, "pvp.start-in", "PVP will start in");

		// Event
		EVENT_TIME_REWARD = getString(lang, "event.time-reward", "&eYou have received %money% in your account for playing %time% , total playing time %totaltime%");
		EVENT_KILL_REWARD = getString(lang, "event.kill-reward", "&eYou have received %money% in your account for killing a player");
		EVENT_WIN_REWARD = getString(lang, "event.win-reward", "&eYou have received %money% in your account for winning the game");

		// Scenarios
		SCENARIO_GLOBAL_INVENTORY = getString(lang, "scenarios.global.inventory", "&6&lScenarios &7(Click for info)");
		SCENARIO_GLOBAL_INVENTORY_EDIT = getString(lang, "scenarios.global.inventory-edit", "&6&lScenarios &7(Edit)");
		SCENARIO_GLOBAL_INVENTORY_VOTE = getString(lang, "scenarios.global.inventory-vote", "&6&lVote &7(Toggle votes)");
		SCENARIO_GLOBAL_ITEM_EDIT = getString(lang, "scenarios.global.item-edit", "&6Edit");
		SCENARIO_GLOBAL_ITEM_BACK = getString(lang, "scenarios.global.item-back", "&6Back");
		SCENARIO_GLOBAL_ITEM_HOTBAR = getString(lang, "scenarios.global.item-hotbar", "&6Right click to view active scenarios");
		SCENARIO_GLOBAL_VOTE_MAX = getString(lang, "scenarios.global.vote-max", "&cMax votes reached (%max%)");

		// load scenario info
		for (Scenario scenario : Scenario.values()){
			List<String> info = lang.getStringList("scenarios." + scenario.getLowerCase() + ".info");

			if (info.isEmpty()){
				lang.set("scenarios." + scenario.getLowerCase() + ".info", scenario.getInfo());
				info = Arrays.asList(scenario.getInfo());
			}

			List<String> translatedInfo = new ArrayList<>();

			for (String message : info){
				translatedInfo.add(ChatColor.translateAlternateColorCodes('&', message));
			}

			scenario.setInfo(translatedInfo.toArray(new String[]{}));
		}

		SCENARIO_BESTPVE_ADDED = getString(lang, "scenarios.bestpve.added", "&aYou are added to the PvE list.");
		SCENARIO_BESTPVE_REMOVED = getString(lang, "scenarios.bestpve.removed", "&cYou are now removed from the PvE list. Getting a kill will add you back to the list.");
		SCENARIO_BESTPVE_BACK = getString(lang, "scenarios.bestpve.back", "&aYou are added back to the PvE list.");
		SCENARIO_BOWLESS_ERROR = getString(lang, "scenarios.bowless.error", "&cBowless is turned on.");
		SCENARIO_HORSELESS_ERROR = getString(lang, "scenarios.horseless.error", "&cHorseless is turned on.");
		SCENARIO_NOCLEAN_INVULNERABLE = getString(lang, "scenarios.noclean.invulnerable", "&aYou are now invulnerable for 30 seconds!");
		SCENARIO_NOCLEAN_VULNERABLE = getString(lang, "scenarios.noclean.vulnerable", "&cYou can now take damage again!");
		SCENARIO_NOCLEAN_ERROR = getString(lang, "scenarios.noclean.error", "&a[NoClean] &cYou can't damage this player!");
		SCENARIO_RODLESS_ERROR = getString(lang, "scenarios.rodless.error", "&cRodless is turned on.");
		SCENARIO_SWITCHEROO_SWITCH = getString(lang, "scenarios.switcheroo.switch", "&6You have switched positions with &3%player%");
		SCENARIO_LOVEATFIRSTSIGHT_JOIN_ERROR = getString(lang, "scenarios.loveatfirstsight", "&cCan't join teams, Love at first sight is enabled!");

		if (lang.addedDefaultValues()) {
			try {
				lang.save(langFile);
			} catch (IOException ex) {
				Bukkit.getLogger().severe("[UhcCore] Failed to edit " + langFile.toString());
				ex.printStackTrace();
			}
		}
	}

	private String getString(FileConfiguration lang, String path, String def){
		return ChatColor.translateAlternateColorCodes('&', lang.getString(path, def));
	}

}