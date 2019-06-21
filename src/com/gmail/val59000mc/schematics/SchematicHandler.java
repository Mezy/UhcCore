package com.gmail.val59000mc.schematics;

import com.gmail.val59000mc.UhcCore;
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

    public static ArrayList<Integer> pasteSchematic(Location loc, File schematicFile) throws Exception{
        if (UhcCore.getVersion() < 13){
            return SchematicHandler8.pasteSchematic(loc, schematicFile.getPath());
        }else {
            return SchematicHandler13.pasteSchematic(loc, schematicFile.getPath());
        }
    }

}