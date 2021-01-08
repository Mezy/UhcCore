package com.gmail.val59000mc.configuration;

import com.gmail.val59000mc.configuration.options.*;
import com.gmail.val59000mc.scenarios.Scenario;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class MainConfig extends YamlFile {
	public static final Option<Integer> MINIMAL_READY_TEAMS_PERCENTAGE_TO_START = new Option<>("minimal-ready-teams-percentage-to-start",50);
	public static final Option<Integer> MINIMAL_READY_TEAMS_TO_START = new Option<>("minimal-ready-teams-to-start",2);
	public static final Option<Integer> MIN_PLAYERS_TO_START = new Option<>("min-players-to-start",0);
	public static final Option<Integer> MAX_PLAYERS_PER_TEAM = new Option<>("max-players-per-team",2);
	public static final Option<Boolean> TEAM_COLORS = new Option<>("use-team-colors",true);
	public static final Option<Boolean> CHANGE_DISPLAY_NAMES = new Option<>("change-display-names",false);
	public static final Option<Integer> TIME_BEFORE_START_WHEN_READY = new Option<>("time-to-start-when-ready",15);
	public static final Option<Boolean> CAN_SPECTATE_AFTER_DEATH = new Option<>("can-spectate-after-death",false);
	public static final Option<Boolean> CAN_SEND_MESSAGES_AFTER_DEATH = new Option<>("can-send-messages-after-death",true);
	// Chat prefix
	public static final Option<Boolean> ENABLE_CHAT_PREFIX = new Option<>("chat-prefix.enable",false);
	public static final Option<String> TEAM_CHAT_PREFIX = new Option<>("chat-prefix.team-prefix","@");
	public static final Option<String> GLOBAL_CHAT_PREFIX = new Option<>("chat-prefix.global-prefix","!");

	public static final Option<Boolean> DISABLE_MOTD = new Option<>("disable-motd", false);
	public static final Option<Boolean> ANNOUNCE_ADVANCEMENTS = new Option<>("announce-advancements", true);
	public static final Option<Boolean> ENABLE_HEALTH_REGEN = new Option<>("enable-health-regen", false);
	public static final Option<Integer> TIME_BEFORE_PVP = new Option<>("time-before-pvp",600);
	public static final Option<Boolean> ENABLE_FRIENDLY_FIRE = new Option<>("enable-friendly-fire",false);
	public static final Option<Boolean> DISABLE_ENEMY_NAMETAGS = new Option<>("disable-enemy-nametags",false);
	public static final Option<Boolean> PICK_RANDOM_SEED_FROM_LIST = new Option<>("world-seeds.pick-random-seed-from-list",false);
	public static final Option<Boolean> PICK_RANDOM_WORLD_FROM_LIST = new Option<>("world-list.pick-random-world-from-list",false);

	// Compass
	public static final Option<Boolean> ENABLE_PLAYING_COMPASS = new Option<>("playing-compass.enable",true);
	public static final Option<Integer> PLAYING_COMPASS_MODE = new Option<>("playing-compass.mode",1);
	public static final Option<Integer> PLAYING_COMPASS_COOLDOWN = new Option<>("playing-compass.cooldown",-1);

	public static final Option<Boolean> HEARTS_ON_TAB = new Option<>("hearts-on-tab", true);
	public static final Option<Boolean> HEARTS_BELOW_NAME = new Option<>("hearts-below-name", false);
	public static final Option<Boolean> SPECTATING_TELEPORT = new Option<>("spectating-teleport",false);
	public static final Option<Boolean> ENABLE_KITS_PERMISSIONS = new Option<>("enable-kits-permissions",false);

	// Customize game behavior
	public static final Option<Boolean> ENABLE_CRAFTS_PERMISSIONS = new Option<>("customize-game-behavior.enable-crafts-permissions",false);
	public static final Option<Boolean> ENABLE_EXTRA_HALF_HEARTS = new Option<>("customize-game-behavior.add-player-extra-half-hearts.enable",false);
	public static final Option<Integer> EXTRA_HALF_HEARTS = new Option<>("customize-game-behavior.add-player-extra-half-hearts.extra-half-hearts",0);
	public static final Option<Boolean> ENABLE_GOLD_DROPS = new Option<>("customize-game-behavior.add-gold-drops.enable",false);
	public static final Option<Integer> MIN_GOLD_DROPS = new Option<>("customize-game-behavior.add-gold-drops.min",0);
	public static final Option<Integer> MAX_GOLD_DROPS = new Option<>("customize-game-behavior.add-gold-drops.max",0);
	public static final Option<Integer> GOLD_DROP_PERCENTAGE = new Option<>("customize-game-behavior.add-gold-drops.drop-chance-percentage",0);
	public static final Option<Boolean> ENABLE_SCENARIO_VOTING = new Option<>("customize-game-behavior.scenarios.voting.enable", false);
	public static final Option<Integer> MAX_SCENARIO_VOTES = new Option<>("customize-game-behavior.scenarios.voting.max-votes", 3);
	public static final Option<Integer> ELECTED_SCENARIO_COUNT = new Option<>("customize-game-behavior.scenarios.voting.elected-scenarios", 3);
	public static final Option<Boolean> ENABLE_EXP_DROP_ON_DEATH = new Option<>("customize-game-behavior.add-xp-drops-on-player-death.enable",false);
	public static final Option<Integer> EXP_DROP_ON_DEATH = new Option<>("customize-game-behavior.add-xp-drops-on-player-death.quantity",0);
	public static final Option<Boolean> REGEN_HEAD_DROP_ON_PLAYER_DEATH = new Option<>("customize-game-behavior.add-regen-head-drop-on-player-death",true);
	public static final Option<Boolean> DOUBLE_REGEN_HEAD = new Option<>("customize-game-behavior.double-regen-head",false);
	public static final Option<Boolean> ENABLE_GOLDEN_HEADS = new Option<>("customize-game-behavior.enable-golden-heads",false);
	public static final Option<Boolean> PLACE_HEAD_ON_FENCE = new Option<>("customize-game-behavior.place-head-on-fence",false);
	public static final Option<Boolean> ALLOW_GHAST_TEARS_DROPS = new Option<>("customize-game-behavior.allow-ghast-tears-drops",true);
	public static final Option<Integer> MAX_BUILDING_HEIGHT = new Option<>("customize-game-behavior.max-building-height", -1);
	public static final Option<Boolean> ENABLE_NETHER = new Option<>("customize-game-behavior.enable-nether",false);
	public static final Option<Boolean> ENABLE_THE_END = new Option<>("customize-game-behavior.enable-the-end",false);
	public static final Option<Boolean> BAN_LEVEL_TWO_POTIONS = new Option<>("customize-game-behavior.ban-level-2-potions",false);
	public static final Option<Boolean> ENABLE_DAY_NIGHT_CYCLE = new Option<>("customize-game-behavior.day-night-cycle.enable",false);
	public static final Option<Integer> TIME_BEFORE_PERMANENT_DAY = new Option<>("customize-game-behavior.day-night-cycle.time-before-permanent-day",1200);
	public static final Option<Boolean> ENABLE_DEFAULT_SCENARIOS = new Option<>("customize-game-behavior.enable-default-scenarios", false);
	public static final EnumListOption<Scenario> DEFAULT_SCENARIOS = new EnumListOption<>("customize-game-behavior.active-scenarios", Scenario.class);
	public static final EnumListOption<Scenario> SCENARIO_VOTING_BLACKLIST = new EnumListOption<>("customize-game-behavior.scenarios.voting.black-list", Scenario.class);
	public static final EnumOption<Sound> SOUND_ON_PLAYER_DEATH = new EnumOption<>("customize-game-behavior.sound-on-player-death", Sound.class, "ENTITY_WITHER_SPAWN");
	public static final EnumListOption<EntityType> AFFECTED_GOLD_DROP_MOBS = new EnumListOption<>("customize-game-behavior.add-gold-drops.affected-mobs", EntityType.class);

	public static final Option<Boolean> ENABLE_EPISODE_MARKERS = new Option<>("episode-markers.enable",false);
	public static final Option<Integer> EPISODE_MARKERS_DELAY = new Option<>("episode-markers.delay",900);
	public static final Option<Boolean> ENABLE_KILL_DISCONNECTED_PLAYERS = new Option<>("kill-disconnected-players-after-delay.enable",false);
	public static final Option<Integer> MAX_DISCONNECT_PLAYERS_TIME = new Option<>("kill-disconnected-players-after-delay.delay",60);
	public static final Option<Boolean> SPAWN_OFFLINE_PLAYERS = new Option<>("spawn-offline-players",false);

	// Bungee
	public static final Option<Boolean> ENABLE_BUNGEE_SUPPORT = new Option<>("bungee-support.enable",false);
	public static final Option<Boolean> ENABLE_BUNGEE_LOBBY_ITEM = new Option<>("bungee-support.use-lobby-item",true);
	public static final Option<String> SERVER_BUNGEE = new Option<>("bungee-support.send-players-to-server-after-end","lobby");
	public static final Option<Integer> TIME_BEFORE_SEND_BUNGEE_AFTER_DEATH = new Option<>("bungee-support.time-before-send-after-death",-1);
	public static final Option<Integer> TIME_BEFORE_SEND_BUNGEE_AFTER_END = new Option<>("bungee-support.time-before-send-after-end",-1);

	public static final Option<Boolean> LOBBY_IN_DEFAULT_WORLD = new Option<>("lobby-in-default-world", false);

	// Border
	public static final Option<Integer> BORDER_TIME_TO_SHRINK = new Option<>("border.time-to-shrink",3600);
	public static final Option<Boolean> BORDER_IS_MOVING = new Option<>("border.moving",false);
	public static final Option<Integer> BORDER_START_SIZE = new Option<>("border.start-size",1000);
	public static final Option<Integer> BORDER_END_SIZE = new Option<>("border.end-size",0);
	public static final Option<Integer> BORDER_TIME_BEFORE_SHRINK = new Option<>("border.time-before-shrink",0);

	// Deathmatch
	public static final Option<Boolean> ENABLE_TIME_LIMIT = new Option<>("deathmatch.enable",false);
	public static final Option<Integer> TIME_LIMIT = new Option<>("deathmatch.delay", BORDER_TIME_TO_SHRINK);
	public static final Option<Boolean> DEATHMATCH_ADVENTURE_MODE = new Option<>("deathmatch.deathmatch-adventure-mode",true);
	public static final Option<Boolean> ENABLE_DEATHMATCH_FORCE_END = new Option<>("deathmatch.force-end.enable",false);
	public static final Option<Integer> DEATHMATCH_FORCE_END_DELAY = new Option<>("deathmatch.force-end.delay",120);
	public static final Option<Integer> ARENA_PASTE_AT_Y = new Option<>("deathmatch.arena-deathmatch.paste-at-y",100);
	public static final Option<Integer> DEATHMATCH_START_SIZE = new Option<>("deathmatch.center-deathmatch.start-size",125);
	public static final Option<Integer> DEATHMATCH_END_SIZE = new Option<>("deathmatch.center-deathmatch.end-size",50);
	public static final Option<Integer> DEATHMATCH_TIME_TO_SHRINK = new Option<>("deathmatch.center-deathmatch.time-to-shrink",600);

	public static final Option<Boolean> AUTO_ASSIGN_PLAYER_TO_TEAM = new Option<>("auto-assign-new-player-team",false);
	public static final Option<Boolean> FORCE_ASSIGN_SOLO_PLAYER_TO_TEAM_WHEN_STARTING = new Option<>("force-assign-solo-player-to-team-when-starting",false);
	public static final Option<Boolean> PREVENT_PLAYER_FROM_LEAVING_TEAM = new Option<>("prevent-player-from-leaving-team",false);
	public static final Option<Boolean> TEAM_ALWAYS_READY = new Option<>("team-always-ready",false);
	public static final Option<Boolean> ENABLE_TEAM_NAMES = new Option<>("enable-team-names",true);
	public static final Option<Integer> TIME_BEFORE_RESTART_AFTER_END = new Option<>("time-before-restart-after-end",30);
	public static final Option<Boolean> CAN_JOIN_AS_SPECTATOR = new Option<>("can-join-as-spectator",false);
	public static final Option<Boolean> END_GAME_WHEN_ALL_PLAYERS_HAVE_LEFT = new Option<>("countdown-ending-game-when-all-players-have-left",true);
	public static final Option<Boolean> DEBUG = new Option<>("debug",false);
	public static final Option<Boolean> ONE_PLAYER_MODE = new Option<>("one-player-mode",false);
	public static final Option<Boolean> AUTO_UPDATE = new Option<>("auto-update",true);

	// Pre-generate world
	public static final Option<Boolean> ENABLE_PRE_GENERATE_WORLD = new Option<>("pre-generate-world.enable",true);
	public static final Option<Integer> REST_EVERY_NUM_OF_CHUNKS = new Option<>("pre-generate-world.rest-every-num-of-chunks-ticks",200);
	public static final Option<Integer> REST_DURATION = new Option<>("pre-generate-world.rest-duration",20);

	public static final EnumOption<Difficulty> GAME_DIFFICULTY = new EnumOption<>("game-difficulty", Difficulty.HARD);
	public static final EnumOption<Material> ARENA_TELEPORT_SPOT_BLOCK = new EnumOption<>("deathmatch.arena-deathmatch.teleport-spots-block", Material.BEDROCK);
	public static final PotionEffectListOption POTION_EFFECT_ON_START = new PotionEffectListOption("potion-effect-on-start");
	public static final Option<List<Long>> SEEDS = new Option<>("world-seeds.list");
	public static final Option<List<String>> WORLDS = new Option<>("world-list.list");

	// Fast Mode
	public static final Option<Boolean> ENABLE_FINAL_HEAL = new Option<>("fast-mode.final-heal.enable", false);
	public static final Option<Integer> FINAL_HEAL_DELAY = new Option<>("fast-mode.final-heal.delay", 1200);
	public static final Option<Boolean> ENABLE_UNDERGROUND_NETHER = new Option<>("fast-mode.underground-nether.enable",false);
	public static final Option<Integer> NETHER_PASTE_AT_Y =  new Option<>("fast-mode.underground-nether.paste-nether-at-y",20);
	public static final Option<Integer> MIN_OCCURRENCES_UNDERGROUND_NETHER = new Option<>("fast-mode.underground-nether.min-ocurrences",5);
	public static final Option<Integer> MAX_OCCURRENCES_UNDERGROUND_NETHER = new Option<>("fast-mode.underground-nether.min-ocurrences",10);
	public static final Option<Boolean> ENABLE_GENERATE_SUGARCANE = new Option<>("fast-mode.generate-sugar-cane.enable",false);
	public static final Option<Integer> GENERATE_SUGARCANE_PERCENTAGE = new Option<>("fast-mode.generate-sugar-cane.percentage",10);
	public static final Option<Double> APPLE_DROP_PERCENTAGE = new Option<>("fast-mode.apple-drops.percentage", 0.5);
	public static final Option<Boolean> APPLE_DROPS_FROM_ALL_TREES = new Option<>("fast-mode.apple-drops.all-trees", false);
	public static final Option<Boolean> APPLE_DROPS_FROM_SHEARING = new Option<>("fast-mode.apple-drops.allow-shears", false);
	public static final Option<Boolean> REPLACE_OCEAN_BIOMES = new Option<>("fast-mode.replace-ocean-biomes", false);
	public static final Option<Boolean> CAVE_ORES_ONLY = new Option<>("fast-mode.cave-ores-only", false);
	// Loot configs
	public static final Option<Boolean> ENABLE_BLOCK_LOOT = new Option<>("fast-mode.block-loot.enable",false);
	public static final LootConfigOption<Material> BLOCK_LOOT = new LootConfigOption<>("fast-mode.block-loot.loots", Material.class);
	public static final Option<Boolean> ENABLE_MOB_LOOT = new Option<>("fast-mode.mob-loot.enable",false);
	public static final LootConfigOption<EntityType> MOB_LOOT = new LootConfigOption<>("fast-mode.mob-loot.loots", EntityType.class);
	// Generate veins
	public static final Option<Boolean> ENABLE_GENERATE_VEINS = new Option<>("fast-mode.generate-vein.enable",false);
	public static final VeinConfigOption GENERATE_VEINS = new VeinConfigOption("fast-mode.generate-vein.veins");

	// Custom events
	public static final Option<Boolean> ENABLE_TIME_EVENT = new Option<>("custom-events.time.enable",false);
	public static final Option<Double> REWARD_TIME_EVENT = new Option<>("custom-events.time.reward",0D);
	public static final Option<List<String>> TIME_COMMANDS = new Option<>("custom-events.time.commands");
	public static final Option<Integer> INTERVAL_TIME_EVENTS = new Option<>("custom-events.time.interval",600);
	public static final Option<Boolean> ENABLE_KILL_EVENT = new Option<>("custom-events.kill.enable",false);
	public static final Option<Double> REWARD_KILL_EVENT = new Option<>("custom-events.kill.reward", 0D);
	public static final Option<List<String>> KILL_COMMANDS = new Option<>("custom-events.kill.commands");
	public static final Option<Boolean> ENABLE_WIN_EVENT = new Option<>("custom-events.win.enable",false);
	public static final Option<Double> REWARD_WIN_EVENT = new Option<>("custom-events.win.reward",0D);
	public static final Option<List<String>> WIN_COMMANDS = new Option<>("custom-events.win.commands");

	public void preLoad() {
		// Pre-loads all options to add the default value for missing once
		for (Option<?> option : getOptions()){
			option.getValue(this);
		}

		// Updating old configs to the new syntax
		boolean changes = false;
		if (contains("time-limit")){
			set("deathmatch", getConfigurationSection("time-limit"));
			remove("time-limit");
			changes = true;
		}
		if (contains("worlds.permanent-world-names")){
			set("permanent-world-names", getBoolean("worlds.permanent-world-names"));
			remove("worlds.permanent-world-names");
			remove("worlds");
			changes = true;
		}
		if (contains("customize-game-behavior.ban-nether")){
			set("customize-game-behavior.enable-nether", !getBoolean("customize-game-behavior.ban-nether"));
			remove("customize-game-behavior.ban-nether");
			changes = true;
		}
		if (contains("deathmatch.limit")){
			set("deathmatch.delay", getLong("deathmatch.limit"));
			remove("deathmatch.limit");
			changes = true;
		}
		if (contains("pre-generate-world.rest-every-ticks")){
			remove("pre-generate-world.rest-every-ticks");
			changes = true;
		}
		if (contains("pre-generate-world.chunks-per-tick")){
			remove("pre-generate-world.chunks-per-tick");
			changes = true;
		}

		if (changes || addedDefaultValues()) {
			try {
				saveWithComments();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private List<Option<?>> getOptions() {
		List<Option<?>> options = new ArrayList<>();

		for (Field field : getClass().getFields()){
			try {
				Object obj = field.get(null);
				if (obj instanceof Option){
					options.add((Option<?>) obj);
				}
			}catch (ReflectiveOperationException ex){
				ex.printStackTrace();
			}
		}

		return options;
	}

}
