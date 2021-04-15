package com.gmail.val59000mc.configuration;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.discord.DiscordListener;
import com.gmail.val59000mc.utils.ProtocolUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Dependencies {

    // dependencies
    private static boolean worldEditLoaded;
    private static boolean vaultLoaded;
    private static boolean protocolLibLoaded;
    private static boolean discordSRVLoaded;

    private static DiscordListener discordListener;

    public static void loadWorldEdit() {
        Plugin wePlugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (wePlugin == null || !wePlugin.getClass().getName().equals("com.sk89q.worldedit.bukkit.WorldEditPlugin")) {
            Bukkit.getLogger().warning("[UhcCore] WorldEdit plugin not found, there will be no support of schematics.");
            worldEditLoaded = false;
        } else {
            Bukkit.getLogger().info("[UhcCore] Hooked with WorldEdit plugin.");
            worldEditLoaded = true;
        }
    }

    public static void loadVault(){
        Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
        if(vault == null || !vault.getClass().getName().equals("net.milkbowl.vault.Vault")) {
            Bukkit.getLogger().warning("[UhcCore] Vault plugin not found, there will be no support of economy rewards.");
            vaultLoaded = false;
            return;
        }

        Bukkit.getLogger().info("[UhcCore] Hooked with Vault plugin.");
        vaultLoaded = true;

        VaultManager.setupEconomy();
    }

    public static void loadProtocolLib(){
        Plugin protocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib");
        if(protocolLib == null || !protocolLib.getClass().getName().equals("com.comphenix.protocol.ProtocolLib")) {
            Bukkit.getLogger().warning("[UhcCore] ProtocolLib plugin not found.");
            protocolLibLoaded = false;
            return;
        }

        Bukkit.getLogger().info("[UhcCore] Hooked with ProtocolLib plugin.");
        protocolLibLoaded = true;

        try {
            ProtocolUtils.register();
        } catch (Exception ex) {
            protocolLibLoaded = false;
            Bukkit.getLogger().severe("[UhcCore] Failed to load ProtocolLib, are you using the right version?");
            ex.printStackTrace();
        }
    }

    public static void loadDiscordSRV() {
        Plugin discordSRV = Bukkit.getPluginManager().getPlugin("DiscordSRV");
        if (discordSRV == null) {
            Bukkit.getLogger().warning("[UhcCore] DiscordSRV plugin not found.");
            discordSRVLoaded = false;
            return;
        }

        Bukkit.getLogger().info("[UhcCore] Hooked with DiscordSRV plugin.");
        discordSRVLoaded = true;

        try {
            discordListener = new DiscordListener();
            Bukkit.getPluginManager().registerEvents(discordListener, UhcCore.getPlugin());
        } catch (Exception ex) {
            discordSRVLoaded = false;
            Bukkit.getLogger().severe("[UhcCore] Failed to load DiscordSRV.");
            ex.printStackTrace();
        }
    }

    public static boolean getWorldEditLoaded() {
        return worldEditLoaded;
    }

    public static boolean getVaultLoaded() {
        return vaultLoaded;
    }

    public static boolean getProtocolLibLoaded() {
        return protocolLibLoaded;
    }

    public static boolean getDiscordSRVLoaded() {
        return discordSRVLoaded;
    }

    public static DiscordListener getDiscordListener() {
        return discordListener;
    }
}
