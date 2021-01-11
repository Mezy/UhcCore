package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.events.UhcStartedEvent;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.UniversalMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class InfiniteEnchantsListener extends ScenarioListener{

    @EventHandler
    public void onGameStarted(UhcStartedEvent e){
        ItemStack enchantingTables = UniversalMaterial.ENCHANTING_TABLE.getStack(64);
        ItemStack anvils = new ItemStack(Material.ANVIL, 64);
        ItemStack lapisBlocks = new ItemStack(Material.LAPIS_BLOCK, 64);

        for (UhcPlayer uhcPlayer : e.getPlayerManager().getOnlinePlayingPlayers()){
            try {
                Player player = uhcPlayer.getPlayer();
                player.getInventory().addItem(enchantingTables, anvils, lapisBlocks);
                player.setLevel(Integer.MAX_VALUE);
            }catch (UhcPlayerNotOnlineException ex){
                // No rod for offline players
            }
        }
    }

}