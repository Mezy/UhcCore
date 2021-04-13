package com.gmail.val59000mc;

import com.gmail.val59000mc.discord.DiscordListener;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.utils.FileUtils;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

public class UhcCore extends JavaPlugin {

	private static final int MIN_VERSION = 8;
	private static final int MAX_VERSION = 19;

	@Nullable
	private static DiscordSRV DiscordAPI;
	private static UhcCore pl;
	private static int version;
	private GameManager gameManager;
	private Updater updater;
	private boolean discordSupported = false;

	// Load the Minecraft version.
	private void loadServerVersion() {
		String versionString = Bukkit.getBukkitVersion();
		version = 0;

		for (int i = MIN_VERSION; i <= MAX_VERSION; i++) {
			if (versionString.contains("1." + i)) {
				version = i;
			}
		}

		if (version == 0) {
			version = MIN_VERSION;
			Bukkit.getLogger().warning("[UhcCore] Failed to detect server version! " + versionString + "?");
		}else {
			Bukkit.getLogger().info("[UhcCore] 1." + version + " Server detected!");
		}
	}

	public static int getVersion() {
		return version;
	}

	public static UhcCore getPlugin() {
		return pl;
	}

	@Nullable
	public static DiscordSRV getDiscordAPI() {
		return DiscordAPI;
	}

	public boolean isDiscordSupported() {
		return discordSupported;
	}

	public void setDiscordSupported(boolean discordSupported) {
		this.discordSupported = discordSupported;
	}

	@Override
	public void onEnable() {
		pl = this;
		loadServerVersion();

		gameManager = new GameManager();

		// Discord
		if (getServer().getPluginManager().getPlugin("DiscordSRV") != null) {
			DiscordAPI = DiscordSRV.getPlugin();
			setDiscordSupported(true);
			Bukkit.getPluginManager().registerEvents(new DiscordListener(), this);
		} else Bukkit.getLogger().info("[UHC-Discord] Discord linking won't work due to DiscordSRV missing.");
		Bukkit.getScheduler().runTaskLater(this, () -> gameManager.loadNewGame(), 1);

		updater = new Updater(this);

		// Delete files that are scheduled for deletion
		FileUtils.removeScheduledDeletionFiles();
	}

	public GameManager getGameManager() {
		return gameManager;
	}

	@Override
	public void onDisable() {
		gameManager.getScenarioManager().disableAllScenarios();

		updater.runAutoUpdate();
		Bukkit.getLogger().info("[UhcCore] Plugin disabled");
	}

}
