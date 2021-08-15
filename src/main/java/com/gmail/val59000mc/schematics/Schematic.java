package com.gmail.val59000mc.schematics;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.Dependencies;
import com.gmail.val59000mc.game.GameManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;

public class Schematic {

    private final String schematicName;
    private final Location location;
    private final int loadingArea;

    private boolean build;
    private int height, length, width;
    private File schematicFile;

    /**
     * @param schematicName Name of the schematic that gets loaded for the UhcCore folder. (Don't include a file path or extension)
     * @param buildLocation Location to build the schematic.
     * @param loadingArea Radius around the location in chunks that should be generated before pasting the schematic.
     */
    public Schematic(String schematicName, Location buildLocation, int loadingArea){
        this.schematicName = schematicName;
        this.location = buildLocation;
        this.loadingArea = loadingArea;
        build = false;
        height = -1;
        length = -1;
        width = -1;
        schematicFile = null;
    }

    /**
     * @param schematicName Name of the schematic that gets loaded for the UhcCore folder. (Don't include a file path or extension)
     * @param buildLocation Location to build the schematic.
     */
    public Schematic(String schematicName, Location buildLocation){
        this(schematicName, buildLocation, 0);
    }

    /**
     * @param schematicName Name of the schematic that gets loaded for the UhcCore folder. (Don't include a file path or extension)
     */
    public Schematic(String schematicName){
        this(schematicName, null, 0);
    }

    /**
     * Checks if the schematic can be pasted by checking if the schematic file exists and Worldedit is installed.
     * @return Returns true if the schematic can be pasted.
     */
    public boolean canBePasted() {
        boolean worldeditLoaded = Dependencies.getWorldEditLoaded();
        schematicFile = getSchematicFile();
        return worldeditLoaded && schematicFile.exists();
    }

    /**
     * Builds the loaded schematic in the location specified in the constructor.
     */
    public void build(){
        Validate.notNull(location, "Missing location from constructor.");
        build(location);
    }

    /**
     * Builds the loaded schematic in the specified location.
     * @param location Used to override the schematic location.
     */
    public void build(Location location){
        if (loadingArea > 0){
            for (int x = (location.getBlockX()/16)-loadingArea; x < (location.getBlockX()/16)+loadingArea; x++) {
                for (int z = (location.getBlockZ()/16)-loadingArea; z < (location.getBlockZ()/16)+loadingArea; z++) {
                    Chunk chunk = location.getWorld().getChunkAt(x,z);
                    chunk.load(true);
                    chunk.unload(true);
                }
            }
        }

        ArrayList<Integer> dimensions;
        try {
            if (UhcCore.getVersion() < 13){
                dimensions = SchematicHandler8.pasteSchematic(location, schematicFile.getPath());
            }else {
                dimensions = SchematicHandler13.pasteSchematic(location, schematicFile.getPath());
            }

            build = true;
        } catch (Exception e) {
            build = false;
            Bukkit.getLogger().severe("[UhcCore] An error occurred while pasting the schematic " + schematicFile.getPath());
            e.printStackTrace();
            return;
        }


        height = dimensions.get(0);
        length = dimensions.get(1);
        width = dimensions.get(2);
    }

    public boolean isBuild() {
        return build;
    }

    public Location getLocation() {
        return location;
    }

    public int getHeight() {
        Validate.isTrue(height != -1, "Can't be obtained before pasting schematic");
        return height;
    }

    public int getLength() {
        Validate.isTrue(length != -1, "Can't be obtained before pasting schematic");
        return length;
    }

    public int getWidth() {
        Validate.isTrue(width != -1, "Can't be obtained before pasting schematic");
        return width;
    }

    public File getSchematicFile() {
        if (schematicFile == null){
            schematicFile = getSchematicFile(schematicName);
        }

        return schematicFile;
    }

    /**
     * Used to obtain a schematic file from the UhcCore folder.
     * @param name Name of the schematic without file extension.
     * @return The schematic file matching the specified name.
     */
    public static File getSchematicFile(String name){
        File schematic = new File(UhcCore.getPlugin().getDataFolder(),name+".schematic");
        if (schematic.exists()) return schematic;
        schematic = new File(UhcCore.getPlugin().getDataFolder(),name+".schem");
        return schematic;
    }

}
