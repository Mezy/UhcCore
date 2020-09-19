package com.gmail.val59000mc.events;

import com.gmail.val59000mc.game.GameState;

public class UhcGameStateChangedEvent extends UhcEvent {

	private final GameState oldGameState, newGameState;

	public UhcGameStateChangedEvent(GameState oldGameState, GameState newGameState){
		this.oldGameState = oldGameState;
		this.newGameState = newGameState;
	}

	public GameState getOldGameState() {
		return oldGameState;
	}

	public GameState getNewGameState() {
		return newGameState;
	}

}