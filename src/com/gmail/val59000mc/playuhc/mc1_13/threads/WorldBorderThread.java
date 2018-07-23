package com.gmail.val59000mc.playuhc.mc1_13.threads;

import com.gmail.val59000mc.playuhc.PlayUhc;
import com.gmail.val59000mc.playuhc.mc1_13.game.GameManager;
import com.gmail.val59000mc.playuhc.mc1_13.languages.Lang;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;

public class WorldBorderThread implements Runnable{

	private long timeBeforeShrink;
	private long timeToShrink;
	private int endSize;
	
	public WorldBorderThread(long timeBeforeShrink,int endSize, long timeToShrink){
		this.timeBeforeShrink = timeBeforeShrink;
		this.endSize = endSize;
		this.timeToShrink = timeToShrink;
	}
	
	@Override
	public void run() {
		if(timeBeforeShrink <= 0){
			startMoving();
		}else{
			timeBeforeShrink--;
			Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(), this, 20);
		}
	}
	
	private void startMoving(){
		GameManager.getGameManager().broadcastInfoMessage(Lang.GAME_BORDER_START_SHRINKING);
		
		World overworld = Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getOverworldUuid());
		WorldBorder overworldBorder = overworld.getWorldBorder();
		overworldBorder.setSize(2*endSize, timeToShrink);
		
		World nether = Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getNetherUuid());
		WorldBorder netherBorder = nether.getWorldBorder();
		netherBorder.setSize(endSize, timeToShrink);
	}

}
