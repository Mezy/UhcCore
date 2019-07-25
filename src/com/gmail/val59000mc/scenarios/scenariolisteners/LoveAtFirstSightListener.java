package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LoveAtFirstSightListener extends ScenarioListener{

    public LoveAtFirstSightListener(){
        super(Scenario.LOVEATFIRSTSIGHT);
    }

    @Override
    public void onEnable() {
        GameManager gm = GameManager.getGameManager();
        if (gm.getGameState() == GameState.WAITING || gm.getGameState() == GameState.STARTING) {
            for (UhcPlayer player : gm.getPlayersManager().getPlayersList()) {
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

        Player damaged = (Player) e.getEntity();
        Player damager = (Player) e.getDamager();
        GameManager gm = GameManager.getGameManager();
        PlayersManager pm = gm.getPlayersManager();

        if (gm.getGameState() != GameState.PLAYING){
            return;
        }

        UhcPlayer uhcDamaged, uhcDamager;

        try {
            uhcDamaged = pm.getUhcPlayer(damaged);
            uhcDamager = pm.getUhcPlayer(damager);
        }catch (UhcPlayerDoesntExistException ex){
            ex.printStackTrace();
            return;
        }

        if (uhcDamaged.getTeam().isFull() || uhcDamager.getTeam().isFull()){
            return; // One of the teams is full so no team can be made
        }

        if (!uhcDamaged.getTeam().isSolo() && !uhcDamager.getTeam().isSolo()){
            return; // Neither of the players are solo so a team can't be created
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

        team.sendMessage(ChatColor.GREEN + Lang.TEAM_PLAYER_JOINS.replace("%player%", player.getName()));
        GameManager.getGameManager().getScoreboardManager().updatePlayerTab(player);
        return true;
    }

}