package com.gmail.val59000mc.customitems;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ReviveItemCraftListener implements Craft.OnCraftListener {

    private final PlayerManager playerManager;
    private final boolean reviveWithInventory;

    public ReviveItemCraftListener(PlayerManager playerManager, boolean reviveWithInventory) {
        this.playerManager = playerManager;
        this.reviveWithInventory = reviveWithInventory;
    }

    @Override
    public boolean onCraft(UhcPlayer uhcPlayer) {
        List<UhcPlayer> deadMembers = uhcPlayer.getTeam().getMembers(UhcPlayer::isDeath);

        if (deadMembers.isEmpty()){
            uhcPlayer.sendMessage(Lang.ITEMS_REVIVE_ERROR);
            return true;
        }

        UhcPlayer revivePlayer = deadMembers.get(0);
        playerManager.revivePlayer(revivePlayer, reviveWithInventory);

        uhcPlayer.sendMessage(Lang.ITEMS_REVIVE_SUCCESS.replace("%player%", revivePlayer.getName()));

        Bukkit.getScheduler().runTask(UhcCore.getPlugin(), () -> {
            try {
                Player player = uhcPlayer.getPlayer();
                player.setItemOnCursor(null);
                player.closeInventory();
            }catch (UhcPlayerNotOnlineException ex) {
                ex.printStackTrace();
            }
        });

        return false;
    }

}
