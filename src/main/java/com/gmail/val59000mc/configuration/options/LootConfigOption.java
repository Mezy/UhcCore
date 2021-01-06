package com.gmail.val59000mc.configuration.options;

import com.gmail.val59000mc.configuration.LootConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

public class LootConfigOption<T extends Enum<T>> extends Option<Map<T, LootConfiguration<T>>>{

    private final Class<T> type;

    public LootConfigOption(String path, Class<T> type) {
        super(path);
        this.type = type;
    }

    @Override
    public Map<T, LootConfiguration<T>> getValue(YamlConfiguration config) {
        Map<T, LootConfiguration<T>> lootConfigs = new HashMap<>();
        ConfigurationSection allLootsSection = config.getConfigurationSection(path);

        if(allLootsSection != null){
            for(String lootSectionName : allLootsSection.getKeys(false)){
                ConfigurationSection lootSection = allLootsSection.getConfigurationSection(lootSectionName);
                LootConfiguration<T> lootConfig = new LootConfiguration<>(type);
                if(lootConfig.parseConfiguration(lootSection)){
                    lootConfigs.put(lootConfig.getType(), lootConfig);
                }
            }
        }

        return lootConfigs;
    }

}
