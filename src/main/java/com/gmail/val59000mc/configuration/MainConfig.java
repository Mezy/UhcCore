package com.gmail.val59000mc.configuration;

import com.gmail.val59000mc.customitems.CraftsManager;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.scenarios.Scenario;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
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
	public static final Option<Boolean> ENABLE_PLAYING_COMPASS = new Option<>("playing-compass.enable",true);
	public static final Option<Integer> PLAYING_COMPASS_MODE = new Option<>("playing-compass.mode",1);
	public static final Option<Integer> PLAYING_COMPASS_COOLDOWN = new Option<>("playing-compass.cooldown",-1);
	public static final Option<Boolean> HEARTS_ON_TAB = new Option<>("hearts-on-tab", true);
	public static final Option<Boolean> HEARTS_BELOW_NAME = new Option<>("hearts-below-name", false);
	public static final Option<Boolean> SPECTATING_TELEPORT = new Option<>("spectating-teleport",false);
	public static final Option<Boolean> ENABLE_KITS_PERMISSIONS = new Option<>("enable-kits-permissions",false);
	public static final Option<Boolean> ENABLE_CRAFTS_PERMISSIONS = new Option<>("customize-game-behavior.enable-crafts-permissions",false);
	public static final Option<Boolean> ENABLE_EXTRA_HALF_HEARTS = new Option<>("customize-game-behavior.add-player-extra-half-hearts.enable",false);
	public static final Option<Integer> EXTRA_HALF_HEARTS = new Option<>("customize-game-behavior.add-player-extra-half-hearts.extra-half-hearts",0);
	public static final Option<Boolean> ENABLE_GOLD_DROPS = new Option<>("customize-game-behavior.add-gold-drops.enable",false);
	public static final Option<Integer> MIN_GOLD_DROPS = new Option<>("customize-game-behavior.add-gold-drops.min",0);
	public static final Option<Integer> MAX_GOLD_DROPS = new Option<>("customize-game-behavior.add-gold-drops.max",0);
	public static final Option<Integer> GOLD_DROP_PERCENTAGE = new Option<>("customize-game-behavior.add-gold-drops.drop-chance-percentage",0);
	public static final Option<Boolean> ENABLE_EPISODE_MARKERS = new Option<>("episode-markers.enable",false);
	public static final Option<Integer> EPISODE_MARKERS_DELAY = new Option<>("episode-markers.delay",900);
	public static final Option<Boolean> ENABLE_SCENARIO_VOTING = new Option<>("customize-game-behavior.scenarios.voting.enable", false);
	public static final Option<Integer> MAX_SCENARIO_VOTES = new Option<>("customize-game-behavior.scenarios.voting.max-votes", 3);
	public static final Option<Integer> ELECTED_SCENARIO_COUNT = new Option<>("customize-game-behavior.scenarios.voting.elected-scenarios", 3);
	public static final Option<Boolean> ENABLE_EXP_DROP_ON_DEATH = new Option<>("customize-game-behavior.add-xp-drops-on-player-death.enable",false);
	public static final Option<Integer> EXP_DROP_ON_DEATH = new Option<>("customize-game-behavior.add-xp-drops-on-player-death.quantity",0);
	public static final Option<Boolean> ENABLE_KILL_DISCONNECTED_PLAYERS = new Option<>("kill-disconnected-players-after-delay.enable",false);
	public static final Option<Integer> MAX_DISCONNECT_PLAYERS_TIME = new Option<>("kill-disconnected-players-after-delay.delay",60);
	public static final Option<Boolean> SPAWN_OFFLINE_PLAYERS = new Option<>("spawn-offline-players",false);
	public static final Option<Boolean> ENABLE_BUNGEE_SUPPORT = new Option<>("bungee-support.enable",false);
	public static final Option<Boolean> ENABLE_BUNGEE_LOBBY_ITEM = new Option<>("bungee-support.use-lobby-item",true);
	public static final Option<String> SERVER_BUNGEE = new Option<>("bungee-support.send-players-to-server-after-end","lobby");
	public static final Option<Integer> TIME_BEFORE_SEND_BUNGEE_AFTER_DEATH = new Option<>("bungee-support.time-before-send-after-death",-1);
	public static final Option<Integer> TIME_BEFORE_SEND_BUNGEE_AFTER_END = new Option<>("bungee-support.time-before-send-after-end",-1);
	public static final Option<Boolean> LOBBY_IN_DEFAULT_WORLD = new Option<>("lobby-in-default-world", false);
	public static final Option<Integer> BORDER_TIME_TO_SHRINK = new Option<>("border.time-to-shrink",3600);
	public static final Option<Boolean> ENABLE_TIME_LIMIT = new Option<>("deathmatch.enable",false);
	public static final Option<Integer> TIME_LIMIT = new Option<>("deathmatch.delay", BORDER_TIME_TO_SHRINK);
	public static final Option<Boolean> BORDER_IS_MOVING = new Option<>("border.moving",false);
	public static final Option<Integer> BORDER_START_SIZE = new Option<>("border.start-size",1000);
	public static final Option<Integer> BORDER_END_SIZE = new Option<>("border.end-size",0);
	public static final Option<Integer> BORDER_TIME_BEFORE_SHRINK = new Option<>("border.time-before-shrink",0);
	public static final Option<Boolean> DEATHMATCH_ADVENTURE_MODE = new Option<>("deathmatch.deathmatch-adventure-mode",true);
	public static final Option<Boolean> ENABLE_DEATHMATCH_FORCE_END = new Option<>("deathmatch.force-end.enable",false);
	public static final Option<Integer> DEATHMATCH_FORCE_END_DELAY = new Option<>("deathmatch.force-end.delay",120);
	public static final Option<Integer> ARENA_PASTE_AT_Y = new Option<>("deathmatch.arena-deathmatch.paste-at-y",100);
	public static final Option<Integer> DEATHMATCH_START_SIZE = new Option<>("deathmatch.center-deathmatch.start-size",125);
	public static final Option<Integer> DEATHMATCH_END_SIZE = new Option<>("deathmatch.center-deathmatch.end-size",50);
	public static final Option<Integer> DEATHMATCH_TIME_TO_SHRINK = new Option<>("deathmatch.center-deathmatch.time-to-shrink",600);
	public static final Option<Boolean> REGEN_HEAD_DROP_ON_PLAYER_DEATH = new Option<>("customize-game-behavior.add-regen-head-drop-on-player-death",true);
	public static final Option<Boolean> DOUBLE_REGEN_HEAD = new Option<>("customize-game-behavior.double-regen-head",false);
	public static final Option<Boolean> ENABLE_GOLDEN_HEADS = new Option<>("customize-game-behavior.enable-golden-heads",false);
	public static final Option<Boolean> PLACE_HEAD_ON_FENCE = new Option<>("customize-game-behavior.place-head-on-fence",false);
	public static final Option<Boolean> ALLOW_GHAST_TEARS_DROPS = new Option<>("customize-game-behavior.allow-ghast-tears-drops",true);
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
	public static final Option<Integer> MAX_BUILDING_HEIGHT = new Option<>("customize-game-behavior.max-building-height", -1);
	public static final Option<Boolean> ENABLE_NETHER = new Option<>("customize-game-behavior.enable-nether",false);
	public static final Option<Boolean> ENABLE_THE_END = new Option<>("customize-game-behavior.enable-the-end",false);
	public static final Option<Boolean> BAN_LEVEL_TWO_POTIONS = new Option<>("customize-game-behavior.ban-level-2-potions",false);
	public static final Option<Boolean> ENABLE_DAY_NIGHT_CYCLE = new Option<>("customize-game-behavior.day-night-cycle.enable",false);
	public static final Option<Integer> TIME_BEFORE_PERMANENT_DAY = new Option<>("customize-game-behavior.day-night-cycle.time-before-permanent-day",1200);
	public static final Option<Boolean> ENABLE_PRE_GENERATE_WORLD = new Option<>("pre-generate-world.enable",true);
	public static final Option<Integer> REST_EVERY_NUM_OF_CHUNKS = new Option<>("pre-generate-world.rest-every-num-of-chunks-ticks",200);
	public static final Option<Integer> REST_DURATION = new Option<>("pre-generate-world.rest-duration",20);

	public <T> T get(Option<T> option){
		return (T) get(option.getPath(), option.getDef());
	}

	// Config options.
	private Difficulty gameDifficulty;
	private String overworldUuid;
	private String netherUuid;
	private String theEndUuid;
	private List<Long> seeds;
	private List<String> worldsList;
	private List<EntityType> affectedGoldDropsMobs;
	private Set<Scenario> scenarioBlackList;

	// Arena deathmatch
	private Material arenaTeleportSpotBLock;

	private Sound soundOnPlayerDeath;
	private List<PotionEffect> potionEffectOnStart;

	// custom events
	private boolean enableTimeEvent;
	private double rewardTimeEvent;
	private List<String> timeCommands;
	private long intervalTimeEvent;


	private boolean enableKillEvent;
	private double rewardKillEvent;
	private List<String> killCommands;

	private boolean enableWinEvent;
	private double rewardWinEnvent;
	private List<String> winCommands;

	// fast mode
	private boolean enableFinalHeal;
	private long finalHealDelay;
	private boolean enableUndergroundNether;
	private int netherPasteAtY;
	private int minOccurrencesUndergroundNether;
	private int maxOccurrencesUndergroundNether;
	private boolean enableGenerateSugarcane;
	private int generateSugarcanePercentage;
	private double appleDropPercentage;
	private boolean appleDropsFromAllTrees;
	private boolean appleDropsFromShearing;
	private boolean replaceOceanBiomes;
	private boolean caveOresOnly;
	private boolean enableGenerateVein;
	private Map<Material,GenerateVeinConfiguration> generateVeins;
	private boolean enableBlockLoots;
	private Map<Material,BlockLootConfiguration> blockLoots;
	private boolean enableMobLoots;
	private Map<EntityType,MobLootConfiguration> mobLoots;

	// dependencies
	private boolean worldEditLoaded;
	private boolean vaultLoaded;
	private boolean protocolLibLoaded;

	public void preLoad(YamlFile cfg){
		Validate.notNull(cfg);

		boolean changes = false;
		if (cfg.contains("time-limit")){
			cfg.set("deathmatch", cfg.getConfigurationSection("time-limit"));
			cfg.remove("time-limit");
			changes = true;
		}
		if (cfg.contains("worlds.permanent-world-names")){
			cfg.set("permanent-world-names", cfg.getBoolean("worlds.permanent-world-names"));
			cfg.remove("worlds.permanent-world-names");
			cfg.remove("worlds");
			changes = true;
		}
		if (cfg.contains("customize-game-behavior.ban-nether")){
			cfg.set("customize-game-behavior.enable-nether", !cfg.getBoolean("customize-game-behavior.ban-nether"));
			cfg.remove("customize-game-behavior.ban-nether");
			changes = true;
		}
		if (cfg.contains("deathmatch.limit")){
			cfg.set("deathmatch.delay", cfg.getLong("deathmatch.limit"));
			cfg.remove("deathmatch.limit");
			changes = true;
		}
		if (cfg.contains("pre-generate-world.rest-every-ticks")){
			cfg.remove("pre-generate-world.rest-every-ticks");
			changes = true;
		}
		if (cfg.contains("pre-generate-world.chunks-per-tick")){
			cfg.remove("pre-generate-world.chunks-per-tick");
			changes = true;
		}

		if (changes) {
			try {
				cfg.saveWithComments();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void load(YamlFile cfg, @Nullable YamlFile storage){
		Validate.notNull(cfg);

		if (storage != null){
			overworldUuid = storage.getString("worlds.normal","NULL");
			netherUuid = storage.getString("worlds.nether","NULL");
			theEndUuid = storage.getString("worlds.the_end","NULL");
		}

		// loading golden heads craft if enabled
		if (cfg.getBoolean("customize-game-behavior.enable-golden-heads", false)){
			Bukkit.getLogger().info("[UhcCore] Loading custom craft for golden heads");
			CraftsManager.registerGoldenHeadCraft();
		}

		// Game difficulty
		try {
			gameDifficulty = Difficulty.valueOf(cfg.getString("game-difficulty", "HARD"));
		}catch (IllegalArgumentException e){
			Bukkit.getLogger().severe("[UhcCore] Invalid game difficulty!");
			Bukkit.getLogger().severe(e.getMessage());
			gameDifficulty = Difficulty.HARD;
		}

		// Scenarios
		if (cfg.getBoolean("customize-game-behavior.enable-default-scenarios", false)){
			List<String> scenariosStrings = cfg.getStringList("customize-game-behavior.active-scenarios");
			for (String string : scenariosStrings){
				try {
					Scenario scenario = Scenario.valueOf(string);
					Bukkit.getLogger().info("[UhcCore] Loading " + scenario.getName());
					GameManager.getGameManager().getScenarioManager().addScenario(scenario);
				}catch (Exception ex){
					Bukkit.getLogger().severe("[UhcCore] Invalid scenario: " + string);
				}
			}
		}

		// Scenario blacklist
		scenarioBlackList = new HashSet<>();
		for (String s : cfg.getStringList("customize-game-behavior.scenarios.voting.black-list")){
			try {
				scenarioBlackList.add(Scenario.valueOf(s));
			}catch (IllegalArgumentException ex){
				Bukkit.getLogger().severe("[UhcCore] Invalid scenario: " + s);
				System.out.println(ex.getMessage());
			}
		}

		// SOund on player death
		String soundDeath = cfg.getString("customize-game-behavior.sound-on-player-death","false");
		try{
			soundOnPlayerDeath = Sound.valueOf(soundDeath);
		}catch(IllegalArgumentException e){
			Bukkit.getLogger().info("[UhcCore] Invalid death sound: " + soundDeath);
			soundOnPlayerDeath = null;
		}

		// Arena spot block
		String spotBlock = cfg.getString("deathmatch.arena-deathmatch.teleport-spots-block","BEDROCK");
		try{
			arenaTeleportSpotBLock = Material.valueOf(spotBlock);
		}catch(IllegalArgumentException e){
			Bukkit.getLogger().info("[UhcCore] Invalid deathmatch arena teleport block: " + spotBlock);
			arenaTeleportSpotBLock = Material.BEDROCK;
		}

		// Set remaining time
		if(get(ENABLE_TIME_LIMIT)){
			GameManager.getGameManager().setRemainingTime(get(TIME_LIMIT));
		}

		// Potions effects on start
		List<String> potionStrList = cfg.getStringList("potion-effect-on-start");
		potionEffectOnStart = new ArrayList<>();

		for(String potionStr : potionStrList){
			try{
				String[] potionArr = potionStr.split("/");
				PotionEffectType type = PotionEffectType.getByName(potionArr[0].toUpperCase());
				int duration = Integer.parseInt(potionArr[1]);
				int amplifier = Integer.parseInt(potionArr[2]);

				Validate.notNull(type, "Invalid potion effect type: " + potionArr[0]);

				PotionEffect effect = new PotionEffect(type, duration, amplifier);
				potionEffectOnStart.add(effect);
			}catch(IllegalArgumentException e){
				Bukkit.getLogger().warning("[UhcCore] "+potionStr+" ignored, please check the syntax. It must be formated like POTION_NAME/duration/amplifier");
			}
		}

		// Mobs gold drops
		List<String> mobsGoldDrop = cfg.getStringList("customize-game-behavior.add-gold-drops.affected-mobs");
		affectedGoldDropsMobs = new ArrayList<>();

		for(String mobTypeString : mobsGoldDrop){
			try{
				EntityType mobType = EntityType.valueOf(mobTypeString);
				affectedGoldDropsMobs.add(mobType);
			}catch(IllegalArgumentException e){
				Bukkit.getLogger().warning("[UhcCore] " + mobTypeString+" is not a valid mob type");
			}
		}

		// Seed list
		seeds = cfg.getLongList("world-seeds.list");

		// World list
		worldsList = cfg.getStringList("world-list.list");

		// Fast Mode
		enableFinalHeal = cfg.getBoolean("fast-mode.final-heal.enable", false);
		finalHealDelay = cfg.getLong("fast-mode.final-heal.delay", 1200);
		enableUndergroundNether = cfg.getBoolean("fast-mode.underground-nether.enable",false);
		netherPasteAtY =  cfg.getInt("fast-mode.underground-nether.paste-nether-at-y",20);
		minOccurrencesUndergroundNether = cfg.getInt("fast-mode.underground-nether.min-ocurrences",5);
		maxOccurrencesUndergroundNether = cfg.getInt("fast-mode.underground-nether.min-ocurrences",10);
		enableGenerateSugarcane = cfg.getBoolean("fast-mode.generate-sugar-cane.enable",false);
		generateSugarcanePercentage = cfg.getInt("fast-mode.generate-sugar-cane.percentage",10);
		appleDropPercentage = cfg.getDouble("fast-mode.apple-drops.percentage", 0.5);
		appleDropsFromAllTrees = cfg.getBoolean("fast-mode.apple-drops.all-trees", false);
		appleDropsFromShearing = cfg.getBoolean("fast-mode.apple-drops.allow-shears", false);
		replaceOceanBiomes = cfg.getBoolean("fast-mode.replace-ocean-biomes", false);
		caveOresOnly = cfg.getBoolean("fast-mode.cave-ores-only", false);

		// Fast Mode, generate-vein
		enableGenerateVein = cfg.getBoolean("fast-mode.generate-vein.enable",false);
		generateVeins = new HashMap<>();
		ConfigurationSection allVeinsSection = cfg.getConfigurationSection("fast-mode.generate-vein.veins");
		if(allVeinsSection != null){
			for(String veinSectionName : allVeinsSection.getKeys(false)){
				ConfigurationSection veinSection = allVeinsSection.getConfigurationSection(veinSectionName);
				GenerateVeinConfiguration veinConfig = new GenerateVeinConfiguration();
				if(veinConfig.parseConfiguration(veinSection)){
					generateVeins.put(veinConfig.getMaterial(),veinConfig);
				}
			}
		}

		// Fast Mode, block-loot
		enableBlockLoots = cfg.getBoolean("fast-mode.block-loot.enable",false);
		blockLoots = new HashMap<>();
		ConfigurationSection allBlockLootsSection = cfg.getConfigurationSection("fast-mode.block-loot.loots");
		if(allBlockLootsSection != null){
			for(String blockLootSectionName : allBlockLootsSection.getKeys(false)){
				ConfigurationSection blockLootSection = allBlockLootsSection.getConfigurationSection(blockLootSectionName);
				BlockLootConfiguration blockLootConfig = new BlockLootConfiguration();
				if(blockLootConfig.parseConfiguration(blockLootSection)){
					blockLoots.put(blockLootConfig.getMaterial(),blockLootConfig);
				}
			}
		}

		// Fast Mode, mob-loot
		enableMobLoots = cfg.getBoolean("fast-mode.mob-loot.enable",false);
		mobLoots = new HashMap<>();
		ConfigurationSection allMobLootsSection = cfg.getConfigurationSection("fast-mode.mob-loot.loots");
		if(allMobLootsSection != null){
			for(String mobLootSectionName : allMobLootsSection.getKeys(false)){
				ConfigurationSection mobLootSection = allMobLootsSection.getConfigurationSection(mobLootSectionName);
				MobLootConfiguration mobLootConfig = new MobLootConfiguration();
				if(mobLootConfig.parseConfiguration(mobLootSection)){
					mobLoots.put(mobLootConfig.getEntityType(),mobLootConfig);
				}
			}
		}


		// custom events
		enableTimeEvent = cfg.getBoolean("custom-events.time.enable",false);
		rewardTimeEvent = cfg.getDouble("custom-events.time.reward",0);
		timeCommands = cfg.getStringList("custom-events.time.commands");
		intervalTimeEvent = cfg.getLong("custom-events.time.interval",600);
		if (!timeCommands.isEmpty()){
			for (int i = 0; i < timeCommands.size(); i++){
				String cmd = timeCommands.get(i);
				if (cmd.startsWith("/")){
					timeCommands.set(i, cmd.substring(1));
				}
			}
		}

		enableKillEvent = cfg.getBoolean("custom-events.kill.enable",false);
		rewardKillEvent = cfg.getDouble("custom-events.kill.reward");
		killCommands = cfg.getStringList("custom-events.kill.commands");
		if (!killCommands.isEmpty()){
			for (int i = 0; i < killCommands.size(); i++){
				String cmd = killCommands.get(i);
				if (cmd.startsWith("/")){
					killCommands.set(i, cmd.substring(1));
				}
			}
		}


		enableWinEvent = cfg.getBoolean("custom-events.win.enable",false);
		rewardWinEnvent = cfg.getDouble("custom-events.win.reward",0);
		winCommands = cfg.getStringList("custom-events.win.commands");
		if (!winCommands.isEmpty()){
			for (int i = 0; i < winCommands.size(); i++){
				String cmd = winCommands.get(i);
				if (cmd.startsWith("/")){
					winCommands.set(i, cmd.substring(1));
				}
			}
		}


		if (cfg.addedDefaultValues()) {
			try {
				cfg.saveWithComments();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}


	public void loadWorldEdit() {
		Plugin wePlugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
		if(wePlugin == null || !wePlugin.getClass().getName().equals("com.sk89q.worldedit.bukkit.WorldEditPlugin")) {
			Bukkit.getLogger().warning("[UhcCore] WorldEdit plugin not found, there will be no support of schematics.");
			worldEditLoaded = false;
		}else {
			Bukkit.getLogger().info("[UhcCore] Hooked with WorldEdit plugin.");
			worldEditLoaded = true;
		}
	}

	public void loadVault(){
		Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
		if(vault == null || !vault.getClass().getName().equals("net.milkbowl.vault.Vault")) {
			Bukkit.getLogger().warning("[UhcCore] Vault plugin not found, there will be no support of economy rewards.");
			vaultLoaded = false;
		}else{
			Bukkit.getLogger().info("[UhcCore] Hooked with Vault plugin.");
			vaultLoaded = true;
		}
	}

	public void loadProtocolLib(){
		Plugin protocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib");
		if(protocolLib == null || !protocolLib.getClass().getName().equals("com.comphenix.protocol.ProtocolLib")) {
			Bukkit.getLogger().warning("[UhcCore] ProtocolLib plugin not found.");
			protocolLibLoaded = false;
		}else{
			Bukkit.getLogger().info("[UhcCore] Hooked with ProtocolLib plugin.");
			protocolLibLoaded = true;
		}
	}

	public boolean getEnableTimeEvent() {
		return enableTimeEvent;
	}

	public double getRewardTimeEvent() {
		return rewardTimeEvent;
	}

	public long getIntervalTimeEvent() {
		return intervalTimeEvent;
	}


	public boolean getEnableKillEvent() {
		return enableKillEvent;
	}

	public double getRewardKillEvent() {
		return rewardKillEvent;
	}

	public boolean getEnableWinEvent() {
		return enableWinEvent;
	}

	public double getRewardWinEnvent() {
		return rewardWinEnvent;
	}

	public int getNetherPasteAtY() {
		return netherPasteAtY;
	}

	public boolean getWorldEditLoaded() {
		return worldEditLoaded;
	}

	public boolean getVaultLoaded() {
		return vaultLoaded;
	}

	public boolean getProtocolLibLoaded(){
		return protocolLibLoaded;
	}

	public void setProtocolLibLoaded(boolean b){
		protocolLibLoaded = b;
	}

	public boolean getEnableGenerateVein() {
		return enableGenerateVein;
	}

	public boolean getEnableBlockLoots() {
		return enableBlockLoots;
	}

	public boolean getEnableMobLoots() {
		return enableMobLoots;
	}

	public Map<Material, GenerateVeinConfiguration> getGenerateVeins() {
		return generateVeins;
	}

	public Map<Material, BlockLootConfiguration> getBlockLoots() {
		return blockLoots;
	}

	public Map<EntityType, MobLootConfiguration> getMobLoots() {
		return mobLoots;
	}

	public boolean getEnableFinalHeal(){
		return enableFinalHeal;
	}

	public long getFinalHealDelay(){
		return finalHealDelay;
	}

	public boolean getEnableUndergroundNether() {
		return enableUndergroundNether;
	}

	public int getMinOccurrencesUndergroundNether() {
		return minOccurrencesUndergroundNether;
	}

	public int getMaxOccurrencesUndergroundNether() {
		return maxOccurrencesUndergroundNether;
	}

	public boolean getEnableGenerateSugarcane(){
		return enableGenerateSugarcane;
	}

	public int getGenerateSugarcanePercentage(){
		return generateSugarcanePercentage;
	}

	public double getAppleDropPercentage(){
		return appleDropPercentage;
	}

	public boolean getAppleDropsFromAllTrees(){
		return appleDropsFromAllTrees;
	}

	public boolean getAppleDropsFromShearing(){
		return appleDropsFromShearing;
	}

	public boolean getReplaceOceanBiomes(){
		return replaceOceanBiomes;
	}

	public boolean getCaveOresOnly(){
		return caveOresOnly;
	}

	public Map<Material, GenerateVeinConfiguration> getMoreOres() {
		return generateVeins;
	}

	public List<PotionEffect> getPotionEffectOnStart() {
		return potionEffectOnStart;
	}

	public List<Long> getSeeds() {
		return seeds;
	}

	public String getOverworldUuid() {
		return overworldUuid;
	}

	public String getNetherUuid() {
		return netherUuid;
	}

	public String getTheEndUuid() {
		return theEndUuid;
	}

	public Difficulty getGameDifficulty() {
		return gameDifficulty;
	}

	public List<EntityType> getAffectedGoldDropsMobs() {
		return affectedGoldDropsMobs;
	}

	public Set<Scenario> getScenarioBlackList() {
		return scenarioBlackList;
	}

	public List<String> getWorldsList() {
		return worldsList;
	}

	public Material getArenaTeleportSpotBLock() {
		return arenaTeleportSpotBLock;
	}

	public Sound getSoundOnPlayerDeath() {
		return soundOnPlayerDeath;
	}

	public void setOverworldUuid(String uuid) {
		overworldUuid = uuid;
	}

	public void setNetherUuid(String uuid) {
		netherUuid = uuid;
	}

	public void setTheEndUuid(String theEndUuid) {
		this.theEndUuid = theEndUuid;
	}


	public List<String> getTimeCommands() {
		return timeCommands;
	}

	public List<String> getKillCommands() {
		return killCommands;
	}

	public List<String> getWinCommands() {
		return winCommands;
	}

}