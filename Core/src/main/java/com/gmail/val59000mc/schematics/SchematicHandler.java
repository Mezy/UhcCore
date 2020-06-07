package com.gmail.val59000mc.schematics;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.io.File;
import java.util.List;

public abstract class SchematicHandler {

    public static File getSchematicFile(String name){
        File schematic = new File(UhcCore.getPlugin().getDataFolder(),name+".schematic");
        if (schematic.exists()) return schematic;
        schematic = new File(UhcCore.getPlugin().getDataFolder(),name+".schem");
        return schematic;
    }

    public static List<Integer> pasteSchematic(Location loc, File schematicFile, int loadingArea) throws Exception{
        if (loadingArea > 0){
            for (int x = (loc.getBlockX()/16)-loadingArea; x < (loc.getBlockX()/16)+loadingArea; x++) {
                for (int z = (loc.getBlockZ()/16)-loadingArea; z < (loc.getBlockZ()/16)+loadingArea; z++) {
                    Chunk chunk = loc.getWorld().getChunkAt(x,z);
                    chunk.load(true);
                    chunk.unload(true);
                }
            }
        }

        SchematicHandler handler = VersionUtils.getVersionUtils().getSchematicHandler();
        return handler.pasteSchematic(loc, schematicFile);
    }

    public abstract List<Integer> pasteSchematic(Location location, File schematicFile) throws Exception;

}