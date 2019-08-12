package com.gmail.val59000mc.schematics;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SchematicHandler13 {
	
	public static ArrayList<Integer> pasteSchematic(Location loc, String path) throws WorldEditException, IOException{
		Bukkit.getLogger().info("[UhcCore] Pasting "+path);
		File schematic = new File(path);
        World world = BukkitAdapter.adapt(loc.getWorld());

        ClipboardFormat format = ClipboardFormats.findByFile(schematic);
        ClipboardReader reader = format.getReader(new FileInputStream(schematic));
        Clipboard clipboard = reader.read();

        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
        Operation operation = new ClipboardHolder(clipboard)
                .createPaste(editSession)
                .to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()))
                .ignoreAirBlocks(false)
                .build();

        Operations.complete(operation);
        editSession.flushSession();

		ArrayList<Integer> dimensions = new ArrayList<Integer>();
		dimensions.add(clipboard.getDimensions().getY());
		dimensions.add(clipboard.getDimensions().getX());
		dimensions.add(clipboard.getDimensions().getZ());
		
		Bukkit.getLogger().info("[UhcCore] Successfully pasted '"+path+"' at "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ());
		return dimensions;
	}

}