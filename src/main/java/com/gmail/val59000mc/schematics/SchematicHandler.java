package com.gmail.val59000mc.schematics;

import com.gmail.val59000mc.UhcCore;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;

public class SchematicHandler {

    public static File getSchematicFile(String name){
        File schematic = new File("plugins/UhcCore/"+name+".schematic");
        if (schematic.exists()) return schematic;
        schematic = new File("plugins/UhcCore/"+name+".schem");
        return schematic;
    }

    public static ArrayList<Integer> pasteSchematic(Location loc, File schematicFile, int loadingArea) throws Exception{
        if (loadingArea > 0){
            for (int x = (loc.getBlockX()/16)-loadingArea; x < (loc.getBlockX()/16)+loadingArea; x++) {
                for (int z = (loc.getBlockZ()/16)-loadingArea; z < (loc.getBlockZ()/16)+loadingArea; z++) {
                    Chunk chunk = loc.getWorld().getChunkAt(x,z);
                    chunk.load(true);
                    chunk.unload(true);
                }
            }
        }

        if (UhcCore.getVersion() < 13){
            return SchematicHandler8.pasteSchematic(loc, schematicFile.getPath());
        }else {
            return SchematicHandler13.pasteSchematic(loc, schematicFile.getPath());
        }
    }

}