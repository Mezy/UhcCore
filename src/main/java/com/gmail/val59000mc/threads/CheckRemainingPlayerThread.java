package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import org.bukkit.Bukkit;


public class CheckRemainingPlayerThread implements Runnable{

	private CheckRemainingPlayerThread task;
	
	public CheckRemainingPlayerThread(){
		task = this;
	}
	
	@Override
	public void run() {
		
		Bukkit.getScheduler().runTask(UhcCore.getPlugin(), new Runnable(){

			@Override
			public void run() {
				GameManager.getGameManager().getPlayersManager().checkIfRemainingPlayers();
				GameState state = GameManager.getGameManager().getGameState();
				if(state.equals(GameState.PLAYING) || state.equals(GameState.DEATHMATCH))
					Bukkit.getScheduler().runTaskLaterAsynchronously(UhcCore.getPlugin(),task,40);
				}
				
		});
	}

}
