package com.gmail.val59000mc.schematics;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class SchematicHandler8 extends SchematicHandler{

	@Override
	public List<Integer> pasteSchematic(Location loc, File schematicFile) throws Exception{
		WorldEditPlugin we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		Bukkit.getLogger().info("[UhcCore] Pasting "+schematicFile.getPath());
		EditSession session = we.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(loc.getWorld()), -1);
		
		CuboidClipboard clipboard = MCEditSchematicFormat.getFormat(schematicFile).load(schematicFile);
		clipboard.paste(session, new Vector(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ()), false);

		ArrayList<Integer> dimensions = new ArrayList<>();
		dimensions.add(clipboard.getHeight());
		dimensions.add(clipboard.getLength());
		dimensions.add(clipboard.getWidth());
		
		Bukkit.getLogger().info("[UhcCore] Successfully pasted '"+schematicFile.getPath()+"' at "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ());
		return dimensions;
	}

}