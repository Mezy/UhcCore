package com.gmail.val59000mc.maploader;

import com.gmail.val59000mc.utils.NMSUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Used to change biome terrain generation on Minecraft 1.14+
 */
public class BiomeMapping16 {

    public enum Biome {
        PLAINS(1, "a", false),
        FOREST(1, "c", 0.1F, 0.2F),
        OCEAN(0),
        FROZEN_OCEAN(10),
        DEEP_OCEAN(24),
        WARM_OCEAN(44),
        LUKEWARM_OCEAN(45),
        COLD_OCEAN(46),
        DEEP_WARM_OCEAN(47),
        DEEP_LUKEWARM_OCEAN(48),
        DEEP_COLD_OCEAN(49),
        DEEP_FROZEN_OCEAN(50);

        private final int id;
        private String biomesSettingsMethod;
        private Object[] methodArgs;

        Biome(int id) {
            this.id = id;
        }

        Biome(int id, String biomesSettingsMethod, Object... methodArgs) {
            this.id = id;
            this.biomesSettingsMethod = biomesSettingsMethod;
            this.methodArgs = methodArgs;
        }

        public Class<?>[] getArgTypes(){
            switch (this){
                case PLAINS:
                    return new Class[]{boolean.class};
                case FOREST:
                    return new Class[]{float.class, float.class};
                default:
                    return new Class[]{};
            }
        }

        public Field getField() {
            try {
                Class<?> biomesClass = NMSUtils.getNMSClass("Biomes");
                Field field = biomesClass.getField(name());
                field.setAccessible(true);
                NMSUtils.removeFinal(field);
                return field;
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
                return null;
            }
        }

    }

    public static void replaceBiomes(Biome oldBiome, Biome newBiome){
        Bukkit.getLogger().info("[UhcCore] Replacing biomes: "+oldBiome.name()+" --> "+newBiome.name());

        try {
            // Step 1
            oldBiome.getField().set(null, newBiome.getField().get(null));

            // Step 2
            Class<?> biomeRegistryClass = NMSUtils.getNMSClass("BiomeRegistry");
            Class<?> biomesSettingsDefaultClass = NMSUtils.getNMSClass("BiomesSettingsDefault");

            Method registerMethod = NMSUtils.getMethod(biomeRegistryClass, "a", 3);

            Field resourceKeyField = oldBiome.getField();
            Object resourceKey = resourceKeyField.get(null);

            Method biomeSettingsMethod = NMSUtils.getMethod(biomesSettingsDefaultClass, newBiome.biomesSettingsMethod, newBiome.getArgTypes());
            Object biomeSettings = biomeSettingsMethod.invoke(null, newBiome.methodArgs);

            registerMethod.invoke(null, oldBiome.id, resourceKey, biomeSettings);
        } catch (IllegalArgumentException | ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

}