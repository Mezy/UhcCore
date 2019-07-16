package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.RandomUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DragonRushListener extends ScenarioListener{

    private Set<Block> portalBlocks;
    private Location portalLoc;

    public DragonRushListener(){
        super(Scenario.DRAGONRUSH);
        portalBlocks = new HashSet<>();
    }

    @Override
    public void onEnable(){
        if (!GameManager.getGameManager().getConfiguration().getEnableTheEnd()){
            Bukkit.broadcastMessage(ChatColor.RED + "[UhcCore] For DragonRush the end needs to be enabled first!");
            getScenarioManager().removeScenario(Scenario.DRAGONRUSH);
            return;
        }

        portalLoc = getPortalLocation();

        portalBlocks.add(portalLoc.clone().add(2, 0, 1).getBlock());
        portalBlocks.add(portalLoc.clone().add(2, 0, 0).getBlock());
        portalBlocks.add(portalLoc.clone().add(2, 0, -1).getBlock());

        portalBlocks.add(portalLoc.clone().add(-2, 0, 1).getBlock());
        portalBlocks.add(portalLoc.clone().add(-2, 0, 0).getBlock());
        portalBlocks.add(portalLoc.clone().add(-2, 0, -1).getBlock());

        portalBlocks.add(portalLoc.clone().add(1, 0, 2).getBlock());
        portalBlocks.add(portalLoc.clone().add(0, 0, 2).getBlock());
        portalBlocks.add(portalLoc.clone().add(-1, 0, 2).getBlock());

        portalBlocks.add(portalLoc.clone().add(1, 0, -2).getBlock());
        portalBlocks.add(portalLoc.clone().add(0, 0, -2).getBlock());
        portalBlocks.add(portalLoc.clone().add(-1, 0, -2).getBlock());

        for (Block block : portalBlocks){
            block.setType(Material.END_PORTAL_FRAME);
            if (RandomUtils.randomInteger(0, 2) == 1){
                EndPortalFrame portalFrame = (EndPortalFrame) block.getBlockData();
                portalFrame.setEye(true);
                block.setBlockData(portalFrame);
            }
        }
    }

    @Override
    public void onDisable() {
        for (Block block : portalBlocks){
            block.setType(Material.AIR);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK){
            return;
        }

        if (e.getClickedBlock().getType() != Material.END_PORTAL_FRAME){
            return;
        }

        if (portalBlocks.contains(e.getClickedBlock())){
            Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    int eyes = 0;
                    for (Block block : portalBlocks){
                        EndPortalFrame portalFrame = (EndPortalFrame) block.getBlockData();
                        if (portalFrame.hasEye()){
                            eyes++;
                        }
                    }

                    if (eyes == 12){
                        // fill portal
                        for (int x = -1; x < 2; x++) {
                            for (int z = -1; z < 2; z++) {
                                portalLoc.getWorld().getBlockAt(x, portalLoc.getBlockY(), z).setType(Material.END_PORTAL);
                            }
                        }
                    }
                }
            }, 1);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        if (e.getEntityType() != EntityType.ENDER_DRAGON){
            return;
        }

        if (e.getEntity().getKiller() == null) {
            return;
        }

        GameManager gm = GameManager.getGameManager();
        Player killer = e.getEntity().getKiller();
        UhcPlayer uhcKiller;

        try {
            uhcKiller = gm.getPlayersManager().getUhcPlayer(killer);
        }catch (UhcPlayerDoesntExistException ex){
            return;
        }

        List<UhcPlayer> spectators = new ArrayList<>();

        for (UhcPlayer playingPlayer : gm.getPlayersManager().getAllPlayingPlayers()){

            if (!playingPlayer.isInTeamWith(uhcKiller)){
                spectators.add(playingPlayer);
            }
        }

        for (UhcPlayer spectator : spectators){
            spectator.setState(PlayerState.DEAD);

            try {
                Player all = spectator.getPlayer();
                all.setGameMode(GameMode.SPECTATOR);
                all.teleport(killer);
            }catch (UhcPlayerNotOnlineException exeption){
                // Nothing
            }
        }

        gm.getPlayersManager().checkIfRemainingPlayers();
    }

    private Location getPortalLocation(){
        World world = Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getOverworldUuid());
        int portalY = 0;

        for (int x = -4; x < 4; x++) {
            for (int z = -4; z < 4; z++) {
                int y = getHighestBlock(world, x, z);
                if (y > portalY){
                    portalY = y;
                }
            }
        }

        return new Location(world, 0, portalY+1, 0);
    }

    private int getHighestBlock(World world, int x, int z){
        int y = 150;
        while (world.getBlockAt(x, y, z).getType() == Material.AIR){
            y--;
        }

        return y;
    }

}