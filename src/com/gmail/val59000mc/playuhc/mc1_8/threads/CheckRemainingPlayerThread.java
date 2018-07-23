package com.gmail.val59000mc.playuhc.mc1_8.threads;

import org.bukkit.Bukkit;

import com.gmail.val59000mc.playuhc.PlayUhc;
import com.gmail.val59000mc.playuhc.mc1_8.game.GameManager;
import com.gmail.val59000mc.playuhc.mc1_8.game.GameState;


public class CheckRemainingPlayerThread implements Runnable{

	private CheckRemainingPlayerThread task;
	
	public CheckRemainingPlayerThread(){
		task = this;
	}
	
	@Override
	public void run() {
		
		Bukkit.getScheduler().runTask(PlayUhc.getPlugin(), new Runnable(){

			@Override
			public void run() {
				GameManager.getGameManager().getPlayersManager().checkIfRemainingPlayers();
				GameState state = GameManager.getGameManager().getGameState();
				if(state.equals(GameState.PLAYING) || state.equals(GameState.DEATHMATCH))
					Bukkit.getScheduler().runTaskLaterAsynchronously(PlayUhc.getPlugin(),task,40);
				}
				
		});
	}

}
