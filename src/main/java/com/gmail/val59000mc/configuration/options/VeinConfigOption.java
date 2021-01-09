package com.gmail.val59000mc.configuration.options;

import com.gmail.val59000mc.configuration.VeinConfiguration;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

public class VeinConfigOption implements Option<Map<Material, VeinConfiguration>> {

    private final String path;

    public VeinConfigOption(String path) {
        this.path = path;
    }

    @Override
    public Map<Material, VeinConfiguration> getValue(YamlConfiguration config) {
        Map<Material, VeinConfiguration> generateVeins = new HashMap<>();
        ConfigurationSection allVeinsSection = config.getConfigurationSection(path);

        if(allVeinsSection != null){
            for(String veinSectionName : allVeinsSection.getKeys(false)){
                ConfigurationSection veinSection = allVeinsSection.getConfigurationSection(veinSectionName);
                VeinConfiguration veinConfig = new VeinConfiguration();
                if(veinConfig.parseConfiguration(veinSection)){
                    generateVeins.put(veinConfig.getMaterial(),veinConfig);
                }
            }
        }
        return generateVeins;
    }

}
