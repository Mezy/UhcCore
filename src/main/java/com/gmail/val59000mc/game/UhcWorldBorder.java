package com.gmail.val59000mc.game;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.maploader.MapLoader;
import com.gmail.val59000mc.threads.WorldBorderThread;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

public class UhcWorldBorder {

	private boolean moving;
	private int startSize;
	private int endSize;
	private long timeToShrink;
	private long timeBeforeShrink;

	public int getStartSize() {
		return startSize;
	}

	public void loadSettings(MainConfiguration configuration){
		moving = configuration.getBorderIsMoving();
		startSize = configuration.getBorderStartSize();
		endSize = configuration.getBorderEndSize();
		timeToShrink = configuration.getBorderTimeToShrink();
		timeBeforeShrink = configuration.getBorderTimeBeforeShrink();

		Bukkit.getLogger().info("[UhcCore] Border start size is "+startSize);
		Bukkit.getLogger().info("[UhcCore] Border end size is "+startSize);
		Bukkit.getLogger().info("[UhcCore] Border moves : "+moving);
		Bukkit.getLogger().info("[UhcCore] Border timeBeforeEnd : "+timeToShrink);
	}

	public void setUpBukkitBorder(MapLoader mapLoader){
		Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), () -> {
			World overworld = mapLoader.getUhcWorld(World.Environment.NORMAL);
			setBukkitWorldBorderSize(overworld,0,0,2*startSize);

			World nether = mapLoader.getUhcWorld(World.Environment.NETHER);
			if (nether != null) {
				setBukkitWorldBorderSize(nether, 0, 0, startSize);
			}

			World end = mapLoader.getUhcWorld(World.Environment.THE_END);
			if (end != null) {
				setBukkitWorldBorderSize(end, 0, 0, 2*startSize);
			}
		}, 200);
	}
	
	public void setBukkitWorldBorderSize(World world, int centerX, int centerZ, double edgeSize){
		Validate.notNull(world);

		WorldBorder worldborder = world.getWorldBorder();
		worldborder.setCenter(centerX,centerZ);
		worldborder.setSize(edgeSize);
	}

	public double getCurrentSize(){
		World overworld = GameManager.getGameManager().getMapLoader().getUhcWorld(World.Environment.NORMAL);
		return overworld.getWorldBorder().getSize()/2;
	}

	public void startBorderThread() {
		if(moving){
			Bukkit.getScheduler().runTaskAsynchronously(UhcCore.getPlugin(), new WorldBorderThread(timeBeforeShrink, endSize, timeToShrink));
		}
	}

	public boolean isWithinBorder(Location loc){
		int x = loc.getBlockX();
		int z = loc.getBlockZ();

		if (x < 0) x = -x;
		if (z < 0) z = -z;

		double border = getCurrentSize();

		return x < border && z < border;
	}

}
