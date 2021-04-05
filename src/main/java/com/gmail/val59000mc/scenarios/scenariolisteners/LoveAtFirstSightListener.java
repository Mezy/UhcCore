package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import com.gmail.val59000mc.scenarios.Option;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LoveAtFirstSightListener extends ScenarioListener{

    @Option(key = "disable-broadcasts")
    private boolean disableBroadcasts = false;

    @Override
    public void onEnable() {
        GameManager gm = GameManager.getGameManager();
        if (gm.getGameState() == GameState.WAITING || gm.getGameState() == GameState.STARTING) {
            for (UhcPlayer player : gm.getPlayerManager().getPlayersList()) {
                if (!player.getTeam().isSolo() && !player.isTeamLeader()) {
                    player.getTeam().getMembers().remove(player);
                    player.setTeam(new UhcTeam(player));
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerDamage(EntityDamageByEntityEvent e){
        if (e.getEntityType() != EntityType.PLAYER || !(e.getDamager() instanceof Player)){
            return;
        }

        PlayerManager pm = getPlayerManager();
        UhcPlayer uhcDamaged = pm.getUhcPlayer((Player) e.getEntity());
        UhcPlayer uhcDamager = pm.getUhcPlayer((Player) e.getDamager());

        if (getGameManager().getGameState() != GameState.PLAYING){
            return;
        }

        if (uhcDamaged.getTeam().isFull() || uhcDamager.getTeam().isFull()){
            return; // One of the teams is full so no team can be made
        }

        if (!uhcDamaged.getTeam().isSolo() && !uhcDamager.getTeam().isSolo()){
            return; // Neither of the players are solo so a team can't be created
        }

        if (getTeamManager().getPlayingUhcTeams().size() <= 2){
            return; // Only 2 teams left, don't team them up but let them first.
        }

        boolean result;
        if (uhcDamaged.getTeam().isSolo()){
            // add to damager team
            result = addPlayerToTeam(uhcDamaged, uhcDamager.getTeam());
        }else{
            // add damager to damaged
            result = addPlayerToTeam(uhcDamager, uhcDamaged.getTeam());
        }

        if (result){
            e.setCancelled(true);
        }
    }

    private boolean addPlayerToTeam(UhcPlayer player, UhcTeam team){
        if (team.isFull()) return false;
        Inventory teamInventory = team.getTeamInventory();

        for (ItemStack item : player.getTeam().getTeamInventory().getContents()){
            if (item == null || item.getType() == Material.AIR){
                continue;
            }

            if (teamInventory.getContents().length < teamInventory.getSize()){
                teamInventory.addItem(item);
            }else {
                try {
                    Player bukkitPlayer = player.getPlayer();
                    bukkitPlayer.getWorld().dropItem(bukkitPlayer.getLocation(), item);
                }catch (UhcPlayerNotOnlineException ex){
                    ex.printStackTrace();
                }
            }
        }

        player.setTeam(team);
        team.getMembers().add(player);

        team.sendMessage(Lang.TEAM_MESSAGE_PLAYER_JOINS.replace("%player%", player.getName()));
        GameManager gm = GameManager.getGameManager();
        gm.getScoreboardManager().updatePlayerOnTab(player);
        if (!disableBroadcasts){
            gm.broadcastMessage(Lang.SCENARIO_LOVEATFIRSTSIGHT_JOIN_BROADCAST.replace("%player%", player.getName()).replace("%leader%", team.getLeader().getName()));
        }
        return true;
    }

}