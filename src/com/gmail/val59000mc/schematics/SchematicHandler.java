package com.gmail.val59000mc.schematics;

import com.gmail.val59000mc.UhcCore;
import org.bukkit.Location;

import java.util.ArrayList;

public class SchematicHandler {

    public static ArrayList<Integer> pasteSchematic(Location loc, String path) throws Exception{
        if (UhcCore.getVersion() < 13){
            return SchematicHandler8.pasteSchematic(loc, path);
        }else {
            return SchematicHandler13.pasteSchematic(loc, path);
        }
    }

}