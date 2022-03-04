package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.UhcCore;

public class VersionUtils_1_18 extends VersionUtils_1_15 {

    @Override
    public void replaceOceanBiomes() {
        UhcCore.getPlugin().getLogger().warning(
            "The 'replace-ocean-biomes' setting is currently not supported in Minecraft 1.18+");
    }

}
