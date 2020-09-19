package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class EnablePermanentDayThread implements Runnable{

    private final MainConfiguration configuration;

    public EnablePermanentDayThread(MainConfiguration configuration){
        this.configuration = configuration;
    }

    @Override
    public void run() {
        World overWorld = Bukkit.getWorld(configuration.getOverworldUuid());
        VersionUtils.getVersionUtils().setGameRuleValue(overWorld, "doDaylightCycle", "false");
        overWorld.setTime(6000);
    }

}