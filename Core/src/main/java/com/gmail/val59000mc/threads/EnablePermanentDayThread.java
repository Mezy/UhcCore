package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class EnablePermanentDayThread implements Runnable{

    @Override
    public void run() {
        World overWorld = Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getOverworldUuid());
        VersionUtils.getVersionUtils().setGameRuleValue(overWorld, "doDaylightCycle", "false");
        overWorld.setTime(6000);
    }

}