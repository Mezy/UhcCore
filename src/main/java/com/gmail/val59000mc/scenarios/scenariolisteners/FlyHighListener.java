package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.events.UhcStartedEvent;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.event.EventHandler;

public class FlyHighListener extends ScenarioListener{

    @EventHandler
    public void onGameStart(UhcStartedEvent e){
        getPlayerManager().getOnlinePlayingPlayers().forEach(uhcPlayer -> {
            try{
                uhcPlayer.getPlayer().getInventory().addItem(UniversalMaterial.ELYTRA.getStack());
            }catch (UhcPlayerNotOnlineException ex){
                // No elytra for offline players.
            }
        });
    }

}