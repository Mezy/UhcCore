package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PlayerMovementListener implements Listener{

    private final PlayersManager playersManager;
    private ItemMeta meta;

    public PlayerMovementListener(PlayersManager playersManager){
        this.playersManager = playersManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        handleFrozenPlayers(event);
        handleAnduril(event);
    }

    private void handleFrozenPlayers(PlayerMoveEvent e){
        UhcPlayer uhcPlayer = playersManager.getUhcPlayer(e.getPlayer());
        if (uhcPlayer.isFrozen()){
            Location freezeLoc = uhcPlayer.getFreezeLocation();
            Location toLoc = e.getTo();

            if (toLoc.getBlockX() != freezeLoc.getBlockX() || toLoc.getBlockZ() != freezeLoc.getBlockZ()){
                Location newLoc = toLoc.clone();
                newLoc.setX(freezeLoc.getBlockX() + .5);
                newLoc.setZ(freezeLoc.getBlockZ() + .5);

                e.getPlayer().teleport(newLoc);
            }
        }
    }

    public void handleAnduril(PlayerMoveEvent e) {
        if (e.getPlayer() == null) return;
        int mainHandStack = e.getPlayer().getInventory().getHeldItemSlot();
        ItemStack held = e.getPlayer().getInventory().getItem(mainHandStack);
        if (held != null) {
            ItemMeta meta = held.getItemMeta();
            if(meta != null) {
                List<String> lore = meta.getLore();
                if (lore != null && lore.contains("speeed")) {
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 70, 0));
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 70, 0));
                }
            }
        }
    }

}