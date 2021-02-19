package com.gmail.val59000mc.configuration;

import com.gmail.val59000mc.configuration.options.*;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioManager;
import com.gmail.val59000mc.utils.CompareUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class MainConfig extends YamlFile {
	public static final BasicOption<Integer> MINIMAL_READY_TEAMS_PERCENTAGE_TO_START = new BasicOption<>("minimal-ready-teams-percentage-to-start",50);
	public static final BasicOption<Integer> MINIMAL_READY_TEAMS_TO_START = new BasicOption<>("minimal-ready-teams-to-start",2);
	public static final BasicOption<Integer> MIN_PLAYERS_TO_START = new BasicOption<>("min-players-to-start",20);
	public static final BasicOption<Integer> MAX_PLAYERS_PER_TEAM = new BasicOption<>("max-players-per-team",2);
	public static final BasicOption<Boolean> TEAM_COLORS = new BasicOption<>("use-team-colors",true);
	public static final BasicOption<Boolean> CHANGE_DISPLAY_NAMES = new BasicOption<>("change-display-names",false);
	public static final BasicOption<Integer> TIME_BEFORE_START_WHEN_READY = new BasicOption<>("time-to-start-when-ready",15);
	public static final BasicOption<Boolean> CAN_SPECTATE_AFTER_DEATH = new BasicOption<>("can-spectate-after-death",false);
	public static final BasicOption<Boolean> CAN_SEND_MESSAGES_AFTER_DEATH = new BasicOption<>("can-send-messages-after-death",true);
	// Chat prefix
	public static final BasicOption<Boolean> ENABLE_CHAT_PREFIX = new BasicOption<>("chat-prefix.enable",false);
	public static final BasicOption<String> TEAM_CHAT_PREFIX = new BasicOption<>("chat-prefix.team-prefix","@");
	public static final BasicOption<String> GLOBAL_CHAT_PREFIX = new BasicOption<>("chat-prefix.global-prefix","!");

	public static final BasicOption<Boolean> DISABLE_MOTD = new BasicOption<>("disable-motd", false);
	public static final BasicOption<Boolean> ANNOUNCE_ADVANCEMENTS = new BasicOption<>("announce-advancements", true);
	public static final BasicOption<Boolean> ENABLE_HEALTH_REGEN = new BasicOption<>("enable-health-regen", false);
	public static final BasicOption<Integer> TIME_BEFORE_PVP = new BasicOption<>("time-before-pvp",600);
	public static final BasicOption<Boolean> ENABLE_FRIENDLY_FIRE = new BasicOption<>("enable-friendly-fire",false);
	public static final BasicOption<Boolean> DISABLE_ENEMY_NAMETAGS = new BasicOption<>("disable-enemy-nametags",false);
	public static final BasicOption<Boolean> PICK_RANDOM_SEED_FROM_LIST = new BasicOption<>("world-seeds.pick-random-seed-from-list",false);
	public static final BasicOption<Boolean> PICK_RANDOM_WORLD_FROM_LIST = new BasicOption<>("world-list.pick-random-world-from-list",false);

	// Compass
	public static final BasicOption<Boolean> ENABLE_PLAYING_COMPASS = new BasicOption<>("playing-compass.enable",true);
	public static final BasicOption<Integer> PLAYING_COMPASS_MODE = new BasicOption<>("playing-compass.mode",1);
	public static final BasicOption<Integer> PLAYING_COMPASS_COOLDOWN = new BasicOption<>("playing-compass.cooldown",-1);

	public static final BasicOption<Boolean> HEARTS_ON_TAB = new BasicOption<>("hearts-on-tab", true);
	public static final BasicOption<Boolean> HEARTS_BELOW_NAME = new BasicOption<>("hearts-below-name", false);
	public static final BasicOption<Boolean> SPECTATING_TELEPORT = new BasicOption<>("spectating-teleport",false);
	public static final BasicOption<Boolean> ENABLE_KITS_PERMISSIONS = new BasicOption<>("enable-kits-permissions",false);

	// Customize game behavior
	public static final BasicOption<Boolean> ENABLE_CRAFTS_PERMISSIONS = new BasicOption<>("customize-game-behavior.enable-crafts-permissions",false);
	public static final BasicOption<Boolean> ENABLE_EXTRA_HALF_HEARTS = new BasicOption<>("customize-game-behavior.add-player-extra-half-hearts.enable",false);
	public static final BasicOption<Integer> EXTRA_HALF_HEARTS = new BasicOption<>("customize-game-behavior.add-player-extra-half-hearts.extra-half-hearts",0);
	public static final BasicOption<Boolean> ENABLE_GOLD_DROPS = new BasicOption<>("customize-game-behavior.add-gold-drops.enable",false);
	public static final BasicOption<Integer> MIN_GOLD_DROPS = new BasicOption<>("customize-game-behavior.add-gold-drops.min",0);
	public static final BasicOption<Integer> MAX_GOLD_DROPS = new BasicOption<>("customize-game-behavior.add-gold-drops.max",0);
	public static final BasicOption<Integer> GOLD_DROP_PERCENTAGE = new BasicOption<>("customize-game-behavior.add-gold-drops.drop-chance-percentage",0);
	public static final BasicOption<Boolean> ENABLE_SCENARIO_VOTING = new BasicOption<>("customize-game-behavior.scenarios.voting.enable", false);
	public static final BasicOption<Integer> MAX_SCENARIO_VOTES = new BasicOption<>("customize-game-behavior.scenarios.voting.max-votes", 3);
	public static final BasicOption<Integer> ELECTED_SCENARIO_COUNT = new BasicOption<>("customize-game-behavior.scenarios.voting.elected-scenarios", 3);
	public static final BasicOption<Boolean> ENABLE_EXP_DROP_ON_DEATH = new BasicOption<>("customize-game-behavior.add-xp-drops-on-player-death.enable",false);
	public static final BasicOption<Integer> EXP_DROP_ON_DEATH = new BasicOption<>("customize-game-behavior.add-xp-drops-on-player-death.quantity",0);
	public static final BasicOption<Boolean> REGEN_HEAD_DROP_ON_PLAYER_DEATH = new BasicOption<>("customize-game-behavior.add-regen-head-drop-on-player-death",true);
	public static final BasicOption<Boolean> DOUBLE_REGEN_HEAD = new BasicOption<>("customize-game-behavior.double-regen-head",false);
	public static final BasicOption<Boolean> ENABLE_GOLDEN_HEADS = new BasicOption<>("customize-game-behavior.enable-golden-heads",false);
	public static final BasicOption<Boolean> PLACE_HEAD_ON_FENCE = new BasicOption<>("customize-game-behavior.place-head-on-fence",false);
	public static final BasicOption<Boolean> STRIKE_LIGHTNING_ON_DEATH = new BasicOption<>("customize-game-behavior.stike-lightning-on-death",true);
	public static final BasicOption<Boolean> ALLOW_GHAST_TEARS_DROPS = new BasicOption<>("customize-game-behavior.allow-ghast-tears-drops",true);
	public static final BasicOption<Integer> MAX_BUILDING_HEIGHT = new BasicOption<>("customize-game-behavior.max-building-height", -1);
	public static final BasicOption<Boolean> ENABLE_NETHER = new BasicOption<>("customize-game-behavior.enable-nether",false);
	public static final BasicOption<Boolean> ENABLE_THE_END = new BasicOption<>("customize-game-behavior.enable-the-end",false);
	public static final BasicOption<Boolean> BAN_LEVEL_TWO_POTIONS = new BasicOption<>("customize-game-behavior.ban-level-2-potions",false);
	public static final BasicOption<Boolean> ENABLE_DAY_NIGHT_CYCLE = new BasicOption<>("customize-game-behavior.day-night-cycle.enable",false);
	public static final BasicOption<Integer> TIME_BEFORE_PERMANENT_DAY = new BasicOption<>("customize-game-behavior.day-night-cycle.time-before-permanent-day",1200);
	public static final BasicOption<Boolean> ENABLE_DEFAULT_SCENARIOS = new BasicOption<>("customize-game-behavior.enable-default-scenarios", false);
	public static final ListOption<String> DEFAULT_SCENARIOS = new ListOption<>("customize-game-behavior.active-scenarios", ListOption.Type.STRING_LIST);
	public static final ListOption<String> SCENARIO_VOTING_BLACKLIST = new ListOption<>("customize-game-behavior.scenarios.voting.black-list", ListOption.Type.STRING_LIST);
	public static final EnumListOption<EntityType> AFFECTED_GOLD_DROP_MOBS = new EnumListOption<>("customize-game-behavior.add-gold-drops.affected-mobs", EntityType.class);

	public static final BasicOption<Boolean> ENABLE_EPISODE_MARKERS = new BasicOption<>("episode-markers.enable",false);
	public static final BasicOption<Integer> EPISODE_MARKERS_DELAY = new BasicOption<>("episode-markers.delay",900);
	public static final BasicOption<Boolean> ENABLE_KILL_DISCONNECTED_PLAYERS = new BasicOption<>("kill-disconnected-players-after-delay.enable",false);
	public static final BasicOption<Integer> MAX_DISCONNECT_PLAYERS_TIME = new BasicOption<>("kill-disconnected-players-after-delay.delay",60);
	public static final BasicOption<Boolean> SPAWN_OFFLINE_PLAYERS = new BasicOption<>("spawn-offline-players",false);

	// Bungee
	public static final BasicOption<Boolean> ENABLE_BUNGEE_SUPPORT = new BasicOption<>("bungee-support.enable",false);
	public static final BasicOption<Boolean> ENABLE_BUNGEE_LOBBY_ITEM = new BasicOption<>("bungee-support.use-lobby-item",true);
	public static final BasicOption<String> SERVER_BUNGEE = new BasicOption<>("bungee-support.send-players-to-server-after-end","lobby");
	public static final BasicOption<Integer> TIME_BEFORE_SEND_BUNGEE_AFTER_DEATH = new BasicOption<>("bungee-support.time-before-send-after-death",-1);
	public static final BasicOption<Integer> TIME_BEFORE_SEND_BUNGEE_AFTER_END = new BasicOption<>("bungee-support.time-before-send-after-end",-1);

	public static final BasicOption<Boolean> LOBBY_IN_DEFAULT_WORLD = new BasicOption<>("lobby-in-default-world", false);

	// Border
	public static final BasicOption<Integer> BORDER_TIME_TO_SHRINK = new BasicOption<>("border.time-to-shrink",3600);
	public static final BasicOption<Boolean> BORDER_IS_MOVING = new BasicOption<>("border.moving",false);
	public static final BasicOption<Integer> BORDER_START_SIZE = new BasicOption<>("border.start-size",1000);
	public static final BasicOption<Integer> BORDER_END_SIZE = new BasicOption<>("border.end-size",0);
	public static final BasicOption<Integer> BORDER_TIME_BEFORE_SHRINK = new BasicOption<>("border.time-before-shrink",0);

	// Deathmatch
	public static final BasicOption<Boolean> ENABLE_DEATHMATCH = new BasicOption<>("deathmatch.enable",true);
	public static final BasicOption<Integer> DEATHMATCH_DELAY = new BasicOption<>("deathmatch.delay", BORDER_TIME_TO_SHRINK);
	public static final BasicOption<Boolean> DEATHMATCH_ADVENTURE_MODE = new BasicOption<>("deathmatch.deathmatch-adventure-mode",true);
	public static final BasicOption<Boolean> ENABLE_DEATHMATCH_FORCE_END = new BasicOption<>("deathmatch.force-end.enable",false);
	public static final BasicOption<Integer> DEATHMATCH_FORCE_END_DELAY = new BasicOption<>("deathmatch.force-end.delay",120);
	public static final BasicOption<Integer> ARENA_PASTE_AT_Y = new BasicOption<>("deathmatch.arena-deathmatch.paste-at-y",100);
	public static final BasicOption<Integer> DEATHMATCH_START_SIZE = new BasicOption<>("deathmatch.center-deathmatch.start-size",125);
	public static final BasicOption<Integer> DEATHMATCH_END_SIZE = new BasicOption<>("deathmatch.center-deathmatch.end-size",50);
	public static final BasicOption<Integer> DEATHMATCH_TIME_TO_SHRINK = new BasicOption<>("deathmatch.center-deathmatch.time-to-shrink",600);

	public static final BasicOption<Boolean> AUTO_ASSIGN_PLAYER_TO_TEAM = new BasicOption<>("auto-assign-new-player-team",false);
	public static final BasicOption<Boolean> FORCE_ASSIGN_SOLO_PLAYER_TO_TEAM_WHEN_STARTING = new BasicOption<>("force-assign-solo-player-to-team-when-starting",false);
	public static final BasicOption<Boolean> PREVENT_PLAYER_FROM_LEAVING_TEAM = new BasicOption<>("prevent-player-from-leaving-team",false);
	public static final BasicOption<Boolean> TEAM_ALWAYS_READY = new BasicOption<>("team-always-ready",false);
	public static final BasicOption<Boolean> ENABLE_TEAM_NAMES = new BasicOption<>("enable-team-names",true);
	public static final BasicOption<Integer> TIME_BEFORE_RESTART_AFTER_END = new BasicOption<>("time-before-restart-after-end",30);
	public static final BasicOption<Boolean> CAN_JOIN_AS_SPECTATOR = new BasicOption<>("can-join-as-spectator",false);
	public static final BasicOption<Boolean> END_GAME_WHEN_ALL_PLAYERS_HAVE_LEFT = new BasicOption<>("countdown-ending-game-when-all-players-have-left",true);
	public static final BasicOption<Boolean> DEBUG = new BasicOption<>("debug",false);
	public static final BasicOption<Boolean> ONE_PLAYER_MODE = new BasicOption<>("one-player-mode",false);
	public static final BasicOption<Boolean> AUTO_UPDATE = new BasicOption<>("auto-update",true);

	// Pre-generate world
	public static final BasicOption<Boolean> ENABLE_PRE_GENERATE_WORLD = new BasicOption<>("pre-generate-world.enable",true);
	public static final BasicOption<Integer> REST_EVERY_NUM_OF_CHUNKS = new BasicOption<>("pre-generate-world.rest-every-num-of-chunks-ticks",200);
	public static final BasicOption<Integer> REST_DURATION = new BasicOption<>("pre-generate-world.rest-duration",20);

	public static final EnumOption<Difficulty> GAME_DIFFICULTY = new EnumOption<>("game-difficulty", Difficulty.HARD);
	public static final EnumOption<Material> ARENA_TELEPORT_SPOT_BLOCK = new EnumOption<>("deathmatch.arena-deathmatch.teleport-spots-block", Material.BEDROCK);
	public static final PotionEffectListOption POTION_EFFECT_ON_START = new PotionEffectListOption("potion-effect-on-start");
	public static final ListOption<Long> SEEDS = new ListOption<>("world-seeds.list", ListOption.Type.LONG_LIST);
	public static final ListOption<String> WORLDS = new ListOption<>("world-list.list", ListOption.Type.STRING_LIST);

	// Fast Mode
	public static final BasicOption<Boolean> ENABLE_FINAL_HEAL = new BasicOption<>("fast-mode.final-heal.enable", false);
	public static final BasicOption<Integer> FINAL_HEAL_DELAY = new BasicOption<>("fast-mode.final-heal.delay", 1200);
	public static final BasicOption<Boolean> ENABLE_UNDERGROUND_NETHER = new BasicOption<>("fast-mode.underground-nether.enable",false);
	public static final BasicOption<Integer> NETHER_PASTE_AT_Y =  new BasicOption<>("fast-mode.underground-nether.paste-nether-at-y",20);
	public static final BasicOption<Integer> MIN_OCCURRENCES_UNDERGROUND_NETHER = new BasicOption<>("fast-mode.underground-nether.min-ocurrences",5);
	public static final BasicOption<Integer> MAX_OCCURRENCES_UNDERGROUND_NETHER = new BasicOption<>("fast-mode.underground-nether.min-ocurrences",10);
	public static final BasicOption<Boolean> ENABLE_GENERATE_SUGARCANE = new BasicOption<>("fast-mode.generate-sugar-cane.enable",false);
	public static final BasicOption<Integer> GENERATE_SUGARCANE_PERCENTAGE = new BasicOption<>("fast-mode.generate-sugar-cane.percentage",10);
	public static final BasicOption<Double> APPLE_DROP_PERCENTAGE = new BasicOption<>("fast-mode.apple-drops.percentage", 0.5);
	public static final BasicOption<Boolean> APPLE_DROPS_FROM_ALL_TREES = new BasicOption<>("fast-mode.apple-drops.all-trees", false);
	public static final BasicOption<Boolean> APPLE_DROPS_FROM_SHEARING = new BasicOption<>("fast-mode.apple-drops.allow-shears", false);
	public static final BasicOption<Boolean> REPLACE_OCEAN_BIOMES = new BasicOption<>("fast-mode.replace-ocean-biomes", false);
	public static final BasicOption<Boolean> CAVE_ORES_ONLY = new BasicOption<>("fast-mode.cave-ores-only", false);
	// Loot configs
	public static final BasicOption<Boolean> ENABLE_BLOCK_LOOT = new BasicOption<>("fast-mode.block-loot.enable",false);
	public static final LootConfigOption<Material> BLOCK_LOOT = new LootConfigOption<>("fast-mode.block-loot.loots", Material.class);
	public static final BasicOption<Boolean> ENABLE_MOB_LOOT = new BasicOption<>("fast-mode.mob-loot.enable",false);
	public static final LootConfigOption<EntityType> MOB_LOOT = new LootConfigOption<>("fast-mode.mob-loot.loots", EntityType.class);
	// Generate veins
	public static final BasicOption<Boolean> ENABLE_GENERATE_VEINS = new BasicOption<>("fast-mode.generate-vein.enable",false);
	public static final VeinConfigOption GENERATE_VEINS = new VeinConfigOption("fast-mode.generate-vein.veins");

	// Custom events
	public static final BasicOption<Boolean> ENABLE_TIME_EVENT = new BasicOption<>("custom-events.time.enable",false);
	public static final BasicOption<Double> REWARD_TIME_EVENT = new BasicOption<>("custom-events.time.reward",0D);
	public static final ListOption<String> TIME_COMMANDS = new ListOption<>("custom-events.time.commands", ListOption.Type.STRING_LIST);
	public static final BasicOption<Integer> INTERVAL_TIME_EVENTS = new BasicOption<>("custom-events.time.interval",600);
	public static final BasicOption<Boolean> ENABLE_KILL_EVENT = new BasicOption<>("custom-events.kill.enable",false);
	public static final BasicOption<Double> REWARD_KILL_EVENT = new BasicOption<>("custom-events.kill.reward", 0D);
	public static final ListOption<String> KILL_COMMANDS = new ListOption<>("custom-events.kill.commands", ListOption.Type.STRING_LIST);
	public static final BasicOption<Boolean> ENABLE_WIN_EVENT = new BasicOption<>("custom-events.win.enable",false);
	public static final BasicOption<Double> REWARD_WIN_EVENT = new BasicOption<>("custom-events.win.reward",0D);
	public static final ListOption<String> WIN_COMMANDS = new ListOption<>("custom-events.win.commands", ListOption.Type.STRING_LIST);

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
		if (contains("customize-game-behavior.sound-on-player-death")) {
			remove("customize-game-behavior.sound-on-player-death");
			changes = true;
		}
		List<String> defaultScenarios = DEFAULT_SCENARIOS.getValue(this);
		List<String> scenarioBlacklist = SCENARIO_VOTING_BLACKLIST.getValue(this);

		defaultScenarios = updateScenarioKeyList(defaultScenarios);
		scenarioBlacklist = updateScenarioKeyList(scenarioBlacklist);

		if (defaultScenarios != null) {
			set("customize-game-behavior.active-scenarios", defaultScenarios);
			changes = true;
		}
		if (scenarioBlacklist != null) {
			set("customize-game-behavior.scenarios.voting.black-list", scenarioBlacklist);
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

	private List<String> updateScenarioKeyList(List<String> keys) {
		ScenarioManager scenarioManager = GameManager.getGameManager().getScenarioManager();
		List<String> newKeys = new ArrayList<>();
		boolean updatedList = false;
		for (String key : keys) {
			Optional<Scenario> scenario = scenarioManager.getScenarioByKey(key);
			if (scenario.isPresent()) {
				newKeys.add(scenario.get().getKey());
			}else {
				scenario = scenarioManager.getScenarioByOldKey(key);
				if (scenario.isPresent()) {
					updatedList = true;
					newKeys.add(scenario.get().getKey());
				}else {
					Bukkit.getLogger().warning("[UhcCore] Invalid scenario key, " + key + " removing ...");
				}
			}
		}

		if (updatedList) {
			return newKeys;
		}

		return null;
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
