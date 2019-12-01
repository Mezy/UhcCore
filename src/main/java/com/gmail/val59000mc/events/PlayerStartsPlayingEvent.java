package com.gmail.val59000mc.events;

import com.gmail.val59000mc.players.UhcPlayer;

public class PlayerStartsPlayingEvent extends UhcEvent{

    private UhcPlayer uhcPlayer;

    public PlayerStartsPlayingEvent(UhcPlayer uhcPlayer){
        this.uhcPlayer = uhcPlayer;
    }

    public UhcPlayer getUhcPlayer() {
        return uhcPlayer;
    }

}