package com.gmail.val59000mc.schematics;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class SchematicHandler8{
	
	public static ArrayList<Integer> pasteSchematic(Location loc, String path) throws Exception{
		Bukkit.getLogger().info("[UhcCore] Pasting "+path);
		File schematic = new File(path);
		World world = new BukkitWorld(loc.getWorld());

		ClipboardFormat format = ClipboardFormat.findByFile(schematic);
		ClipboardReader reader = format.getReader(new FileInputStream(schematic));
		Clipboard clipboard = reader.read(world.getWorldData());

		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);

		Operation operation = new ClipboardHolder(clipboard, world.getWorldData())
				.createPaste(editSession, world.getWorldData())
				.to(new Vector(loc.getX(), loc.getY(), loc.getZ()))
				.ignoreAirBlocks(false)
				.build();

		Operations.complete(operation);

		ArrayList<Integer> dimensions = new ArrayList<>();
		dimensions.add(clipboard.getDimensions().getBlockY());
		dimensions.add(clipboard.getDimensions().getBlockX());
		dimensions.add(clipboard.getDimensions().getBlockZ());
		
		Bukkit.getLogger().info("[UhcCore] Successfully pasted '"+path+"' at "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ());
		return dimensions;
	}

}