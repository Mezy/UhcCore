package com.gmail.val59000mc.utils;

import com.gmail.val59000mc.game.GameManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public class LocationUtils {

    public static boolean isWithinBorder(Location loc){
        double border = loc.getWorld().getWorldBorder().getSize()/2;

        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        if (x < 0) x = -x;
        if (z < 0) z = -z;

        return x < border && z < border;
    }

    /***
     * This method will try found a safe location.
     * @param world The world you want to find a location in.
     * @param maxDistance Max distance from 0 0 you want the location to be.
     * @return Returns save ground location. (When no location can be found a random location in the sky will be returned.)
     */
    public static Location findRandomSafeLocation(World world, double maxDistance) {
        // 35 is the range findSafeLocationAround() will look for a spawn block
        maxDistance-=10;
        Location randomLoc;
        Location location = null;

        int i = 0;
        while (location == null){
            i++;
            randomLoc = RandomUtils.newRandomLocation(world, maxDistance);
            location = findSafeLocationAround(randomLoc, 10);
            if (i > 20){
                return randomLoc;
            }
        }

        return location;
    }

    /***
     * Finds a ground block that is not water or lava 35 blocks around the given location.
     * @param loc The location a ground block should be searched around.
     * @param searchRadius The radius used to find a safe location.
     * @return Returns ground location. Can be null when no safe ground location can be found!
     */
    @Nullable
    private static Location findSafeLocationAround(Location loc, int searchRadius) {
        boolean nether = loc.getWorld().getEnvironment() == World.Environment.NETHER;
        Material material;
        Location betterLocation;

        for(int i = -searchRadius ; i <= searchRadius ; i +=3){
            for(int j = -searchRadius ; j <= searchRadius ; j+=3){
                betterLocation = getGroundLocation(loc.clone().add(new Vector(i,0,j)), nether);

                // Check if location is on the nether roof.
                if (nether && betterLocation.getBlockY() > 120){
                    continue;
                }

                // Check if the block below is lava / water
                material = betterLocation.clone().add(0, -1, 0).getBlock().getType();
                if(material.equals(UniversalMaterial.STATIONARY_LAVA.getType()) || material.equals(UniversalMaterial.STATIONARY_WATER.getType())){
                    continue;
                }

                // Stop players from spawning on top of the lobby.
                if (betterLocation.getBlockY() > 160) {
                    continue;
                }

                return betterLocation;
            }
        }

        return null;
    }

    /**
     * Returns location of ground.
     * @param loc Location to look for ground.
     * @param allowCaves When set to true, the first location on the y axis is returned. This will include caves.
     * @return Ground location.
     */
    private static Location getGroundLocation(Location loc, boolean allowCaves){
        World w = loc.getWorld();

        loc.setY(0);

        if (allowCaves){
            while (loc.getBlock().getType() != Material.AIR){
                loc = loc.add(0, 1, 0);
            }
        }else {
            loc = w.getHighestBlockAt(loc).getLocation();
        }

        loc = loc.add(.5, 0, .5);
        return loc;
    }

}
