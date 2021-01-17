package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.maploader.MapLoader;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.World;

public class EnablePermanentDayThread implements Runnable {

    private final MapLoader mapLoader;

    public EnablePermanentDayThread(MapLoader mapLoader) {
        this.mapLoader = mapLoader;
    }

    @Override
    public void run() {
        World overWorld = mapLoader.getUhcWorld(World.Environment.NORMAL);
        VersionUtils.getVersionUtils().setGameRuleValue(overWorld, MapLoader.DO_DAYLIGHT_CYCLE, false);
        overWorld.setTime(6000);
    }

}
