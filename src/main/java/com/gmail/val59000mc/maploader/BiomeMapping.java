package com.gmail.val59000mc.maploader;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.utils.NMSUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

import java.lang.reflect.*;
import java.util.Set;

import static com.gmail.val59000mc.utils.HackyUtils.removeFinal;

/**
 * Used to change biome terrain generation on Minecraft 1.14+
 */
public class BiomeMapping{

    public enum Biome{
        OCEAN(0, "ocean", "BiomeOcean"),
        PLAINS(1, "plains", "BiomePlains"),
        DESERT(2, "desert", "BiomeDesert"),
        MOUNTAINS(3, "mountains", "BiomeBigHills"),
        FOREST(4, "forest", "BiomeForest"),
        TAIGA(5, "taiga", "BiomeTaiga"),
        SWAMP(6, "swamp", "BiomeSwamp"),
        RIVER(7, "river", "BiomeRiver"),
        NETHER(8, "nether", "BiomeHell"),
        THE_END(9, "the_end", "BiomeTheEnd"),
        FROZEN_OCEAN(10, "frozen_ocean", "BiomeFrozenOcean"),
        FROZEN_RIVER(11, "frozen_river", "BiomeFrozenRiver"),
        SNOWY_TUNDRA(12, "snowy_tundra", "BiomeIcePlains"),
        SNOWY_MOUNTAINS(13, "snowy_mountains", "BiomeIceMountains"),
        MUSHROOM_FIELDS(14, "mushroom_fields", "BiomeMushrooms"),
        MUSHROOM_FIELD_SHORE(15, "mushroom_field_shore", "BiomeMushroomIslandShore"),
        BEACH(16, "beach", "BiomeBeach"),
        DESERT_HILLS(17, "desert_hills", "BiomeDesertHills"),
        WOODED_HILLS(18, "wooded_hills", "BiomeForestHills"),
        TAIGA_HILLS(19, "taiga_hills", "BiomeTaigaHills"),
        MOUNTAIN_EDGE(20, "mountain_edge", "BiomeExtremeHillsEdge"),
        JUNGLE(21, "jungle", "BiomeJungle"),
        JUNGLE_HILLS(22, "jungle_hills", "BiomeJungleHills"),
        JUNGLE_EDGE(23, "jungle_edge", "BiomeJungleEdge"),
        DEEP_OCEAN(24, "deep_ocean", "BiomeDeepOcean"),
        STONE_SHORE(25, "stone_shore", "BiomeStoneBeach"),
        SNOWY_BEACH(26, "snowy_beach", "BiomeColdBeach"),
        BIRCH_FOREST(27, "birch_forest", "BiomeBirchForest"),
        BIRCH_FOREST_HILLS(28, "birch_forest_hills", "BiomeBirchForestHills"),
        DARK_FOREST(29, "dark_forest", "BiomeRoofedForest"),
        SNOWY_TAIGA(30, "snowy_taiga", "BiomeColdTaiga"),
        SNOWY_TAIGA_HILLS(31, "snowy_taiga_hills", "BiomeColdTaigaHills"),
        GIANT_TREE_TAIGA(32, "giant_tree_taiga", "BiomeMegaTaiga"),
        GIANT_TREE_TAIGA_HILLS(33, "giant_tree_taiga_hills", "BiomeMegaTaigaHills"),
        WOODED_MOUNTAINS(34, "wooded_mountains", "BiomeExtremeHillsWithTrees"),
        SAVANNA(35, "savanna", "BiomeSavanna"),
        SAVANNA_PLATEAU(36, "savanna_plateau", "BiomeSavannaPlateau"),
        BADLANDS(37, "badlands", "BiomeMesa"),
        WOODED_BADLANDS_PLATEAU(38, "wooded_badlands_plateau", "BiomeMesaPlataeu"),
        BADLANDS_PLATEAU(39, "badlands_plateau", "BiomeMesaPlataeuClear"),
        SMALL_END_ISLANDS(40, "small_end_islands", "BiomeTheEndFloatingIslands"),
        END_MIDLANDS(41, "end_midlands", "BiomeTheEndMediumIsland"),
        END_HIGHLANDS(42, "end_highlands", "BiomeTheEndHighIsland"),
        END_BARRENS(43, "end_barrens", "BiomeTheEndBarrenIsland"),
        WARM_OCEAN(44, "warm_ocean", "BiomeWarmOcean"),
        LUKEWARM_OCEAN(45, "lukewarm_ocean", "BiomeLukewarmOcean"),
        COLD_OCEAN(46, "cold_ocean", "BiomeColdOcean"),
        DEEP_WARM_OCEAN(47, "deep_warm_ocean", "BiomeWarmDeepOcean"),
        DEEP_LUKEWARM_OCEAN(48, "deep_lukewarm_ocean", "BiomeLukewarmDeepOcean"),
        DEEP_COLD_OCEAN(49, "deep_cold_ocean", "BiomeColdDeepOcean"),
        DEEP_FROZEN_OCEAN(50, "deep_frozen_ocean", "BiomeFrozenDeepOcean"),
        THE_VOID(127, "the_void", "BiomeVoid"),
        SUNFLOWER_PLAINS(129, "sunflower_plains", "BiomeSunflowerPlains"),
        DESERT_LAKES(129, "desert_lakes", "BiomeDesertMutated"),
        GRAVELLY_MOUNTAINS(131, "gravelly_mountains", "BiomeExtremeHillsMutated"),
        FLOWER_FOREST(132, "flower_forest", "BiomeFlowerForest"),
        TAIGA_MOUNTAINS(133, "taiga_mountains", "BiomeTaigaMutated"),
        SWAMP_HILLS(134, "swamp_hills", "BiomeSwamplandMutated"),
        ICE_SPIKES(140, "ice_spikes", "BiomeIcePlainsSpikes"),
        MODIFIED_JUNGLE(149, "modified_jungle", "BiomeJungleMutated"),
        MODIFIED_JUNGLE_EDGE(151, "modified_jungle_edge", "BiomeJungleEdgeMutated"),
        TALL_BIRCH_FOREST(155, "tall_birch_forest", "BiomeBirchForestMutated"),
        TALL_BIRCH_HILLS(156, "tall_birch_hills", "BiomeBirchForestHillsMutated"),
        DARK_FOREST_HILLS(156, "tall_birch_hills", "BiomeBirchForestHillsMutated"),
        SNOWY_TAIGA_MOUNTAINS(158, "snowy_taiga_mountains", "BiomeColdTaigaMutated"),
        GIANT_SPRUCE_TAIGA(160, "giant_spruce_taiga", "BiomeMegaSpruceTaiga"),
        GIANT_SPRUCE_TAIGA_HILLS(161, "giant_spruce_taiga_hills", "BiomeRedwoodTaigaHillsMutated"),
        MODIFIED_GRAVELLY_MOUNTAINS(162, "modified_gravelly_mountains", "BiomeExtremeHillsWithTreesMutated"),
        SHATTERED_SAVANNA(163, "shattered_savanna", "BiomeSavannaMutated"),
        SHATTERED_SAVANNA_PLATEAU(164, "shattered_savanna_plateau", "BiomeSavannaPlateauMutated"),
        ERODED_BADLANDS(165, "eroded_badlands", "BiomeMesaBryce"),
        MODIFIED_WOODED_BADLANDS_PLATEAU(166, "modified_wooded_badlands_plateau", "BiomeMesaPlateauMutated"),
        MODIFIED_BADLANDS_PLATEAU(167, "modified_badlands_plateau", "BiomeMesaPlateauClearMutated"),
        BAMBOO_JUNGLE(168, "bamboo_jungle", "BiomeBambooJungle"),
        BAMBOO_JUNGLE_HILLS(169, "bamboo_jungle_hills", "BiomeBambooJungleHills");

        private int id;
        private String name, biomeBase;

        Biome(int id, String name, String biomeBase){
            this.id = id;
            this.name = name;
            this.biomeBase = biomeBase;
        }

        public int getId(){
            return id;
        }

        public String getName(){
            return name;
        }

        public Field getField(){
            try {
                Class<?> biomesClass = NMSUtils.getNMSClass("Biomes");
                Field field = biomesClass.getField(name());
                field.setAccessible(true);

                removeFinal(field);

                return field;
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        public Object getBiomeBaseInstance(){
            try{
                Class<?> biomeBaseClass = NMSUtils.getNMSClass(biomeBase);

                Constructor<?> constructor = biomeBaseClass.getDeclaredConstructor();
                constructor.setAccessible(true);

                return constructor.newInstance();
            }catch (ReflectiveOperationException ex){
                ex.printStackTrace();
                return null;
            }
        }
    }

    private static final Biome[] HASH_SET_BIOMES = new Biome[]{Biome.OCEAN, Biome.PLAINS, Biome.DESERT, Biome.MOUNTAINS, Biome.FOREST, Biome.TAIGA, Biome.SWAMP, Biome.RIVER, Biome.FROZEN_RIVER, Biome.SNOWY_TUNDRA, Biome.SNOWY_MOUNTAINS, Biome.MUSHROOM_FIELDS, Biome.MUSHROOM_FIELD_SHORE, Biome.BEACH, Biome.DESERT_HILLS, Biome.WOODED_HILLS, Biome.TAIGA_HILLS, Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.JUNGLE_EDGE, Biome.DEEP_OCEAN, Biome.STONE_SHORE, Biome.SNOWY_BEACH, Biome.BIRCH_FOREST, Biome.BIRCH_FOREST_HILLS, Biome.DARK_FOREST, Biome.SNOWY_TAIGA, Biome.SNOWY_TAIGA_HILLS, Biome.GIANT_TREE_TAIGA, Biome.GIANT_TREE_TAIGA_HILLS, Biome.WOODED_MOUNTAINS, Biome.SAVANNA, Biome.SAVANNA_PLATEAU, Biome.BADLANDS, Biome.WOODED_BADLANDS_PLATEAU, Biome.BADLANDS_PLATEAU};

    public static void replaceBiomes(Biome oldBiome, Biome newBiome){
        Bukkit.getLogger().info("[UhcCore] Replacing biomes: " + oldBiome.name() + " --> " + newBiome.name());

        try {
            Class<?> biomeBaseClass = NMSUtils.getNMSClass("BiomeBase");
            Class<?> biomesClass = NMSUtils.getNMSClass("Biomes");
            Method registerMethod = NMSUtils.getMethod(biomesClass, "a");

            String biomeBaseFieldName = UhcCore.getVersion() < 16 ? "b" : "c";
            Field biomeBaseHashSet = biomeBaseClass.getField(biomeBaseFieldName);
            removeFinal(biomeBaseHashSet);

            // Biome field to change
            Field oldBiomeField = oldBiome.getField();
            Validate.notNull(oldBiomeField);

            // New registered BiomeBase
            Object newBiomeBase = registerMethod.invoke(null, oldBiome.getId(), oldBiome.getName(), newBiome.getBiomeBaseInstance());

            oldBiomeField.set(null, newBiomeBase);

            Set<Object> hashSet = (Set<Object>) biomeBaseHashSet.get(null);
            hashSet.clear();
            for (Biome biome : HASH_SET_BIOMES){
                hashSet.add(biome.getField().get(null));
            }

            biomeBaseHashSet.set(null, hashSet);
        } catch (ReflectiveOperationException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}