package com.gmail.val59000mc.playuhc.players;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.val59000mc.playuhc.configuration.MainConfiguration;
import com.gmail.val59000mc.playuhc.customitems.ItemFactory;
import com.gmail.val59000mc.playuhc.customitems.UhcItems;
import com.gmail.val59000mc.playuhc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.playuhc.exceptions.UhcTeamException;
import com.gmail.val59000mc.playuhc.game.GameManager;
import com.gmail.val59000mc.playuhc.game.GameState;
import com.gmail.val59000mc.playuhc.languages.Lang;

public class UhcTeam {

	List<UhcPlayer> members;
	boolean readyToStart;
	Location startingLocation;
	
	public UhcTeam(UhcPlayer uhcPlayer) {
		this.members = new ArrayList<UhcPlayer>();
		this.readyToStart = GameManager.getGameManager().getConfiguration().getTeamAlwaysReady();
		members.add(uhcPlayer);
	}
	
	public void sendChatMessageToTeamMembers(String message){
		for(UhcPlayer member: members){
			try {
				member.getPlayer().sendMessage(ChatColor.GREEN+"[Team] "+message);
			} catch (UhcPlayerNotOnlineException e) {
				// No message sent to offline players
			}
		}
	}
	
	public boolean contains(UhcPlayer player){
		return members.contains(player);
	}
	
	public synchronized List<UhcPlayer> getMembers(){
		return members;
	}
	

	public List<UhcPlayer> getPlayingMembers(){
		List<UhcPlayer> playingMembers = new ArrayList<UhcPlayer>();
		for(UhcPlayer uhcPlayer : getMembers()){
			if(uhcPlayer.getState().equals(PlayerState.PLAYING)){
				playingMembers.add(uhcPlayer);
			}
		}
		return playingMembers;
	}
	
	public synchronized List<String> getMembersNames(){
		List<String> names = new ArrayList<String>();
		for(UhcPlayer player : getMembers()){
			names.add(player.getName());
		}
		return names;
	}
	
	public void join(UhcPlayer player) throws UhcPlayerNotOnlineException, UhcTeamException{
		boolean canJoinATeam = player.canJoinATeam();
		if(canJoinATeam){
			if(this.ifFull()){
				player.sendMessage(ChatColor.RED+Lang.TEAM_FULL.replace("%player%", player.getName()).replace("%leader%", getLeader().getName()).replace("%limit%", ""+GameManager.getGameManager().getConfiguration().getMaxPlayersPerTeam()));
				throw new UhcTeamException(ChatColor.RED+Lang.TEAM_FULL.replace("%player%", player.getName()).replace("%leader%", getLeader().getName()).replace("%limit%", ""+GameManager.getGameManager().getConfiguration().getMaxPlayersPerTeam()));
			}else{
				player.getPlayer().sendMessage(ChatColor.GREEN+Lang.TEAM_JOIN_AS_PLAYER.replace("%leader%", getLeader().getName()));
				for(UhcPlayer teamMember : getMembers()){
					teamMember.getPlayer().sendMessage(ChatColor.GREEN+Lang.TEAM_PLAYER_JOINS.replace("%player%",player.getName()));
				}
				getMembers().add(player);
				player.setTeam(this);
			}
		}else{
			throw new UhcTeamException(ChatColor.RED+Lang.TEAM_PLAYER_ALREADY_IN_TEAM.replace("%player%", player.getName()));
		}
	}
	
	
	private boolean ifFull() {
		MainConfiguration cfg = GameManager.getGameManager().getConfiguration();
		return (cfg.getMaxPlayersPerTeam() == getMembers().size());
	}

	public void askJoin(UhcPlayer player, UhcPlayer teamLeader) throws UhcTeamException{
		if(!player.canJoinATeam())
			throw new UhcTeamException(ChatColor.RED+Lang.TEAM_ALREADY_IN_TEAM);
		
		boolean alreadyRequested;
		try{
			alreadyRequested= UhcItems.doesInventoryContainsLobbyTeamItem(teamLeader.getPlayer().getInventory(), player.getName());
		}catch(UhcPlayerNotOnlineException e){
			alreadyRequested = false;
		}
		
		if(alreadyRequested)
			throw new UhcTeamException(ChatColor.RED+Lang.TEAM_REQUEST_ALREADY_SENT);
		
		if(teamLeader.isTeamLeader()){
			ItemStack head = ItemFactory.createPlayerSkull(player.getName());
			ItemMeta im = head.getItemMeta();
			im.setDisplayName(player.getName());
			
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.DARK_PURPLE+Lang.TEAM_REQUEST_HEAD);
			im.setLore(lore);
			
			head.setItemMeta(im);
			try {
				teamLeader.getPlayer().getInventory().addItem(head);
				teamLeader.sendMessage(ChatColor.GREEN+Lang.TEAM_REQUEST_RECEIVED.replace("%player%", player.getName()));
				player.sendMessage(ChatColor.GREEN+Lang.TEAM_REQUEST_SENT.replace("%leader%", teamLeader.getName()));
			}catch (UhcPlayerNotOnlineException e) {
				throw new UhcTeamException(ChatColor.RED+Lang.TEAM_PLAYER_NOT_ONLINE.replace("%player%", teamLeader.getName()));
			}
		}else{
			throw new UhcTeamException(ChatColor.RED+Lang.TEAM_NO_LONGER_EXISTS);
		}
	}
	
	public void denyJoin(UhcPlayer player){
		getLeader().sendMessage(ChatColor.RED+Lang.TEAM_DENY_REQUEST.replace("%player%", player.getName()));
		player.sendMessage(ChatColor.RED+Lang.TEAM_DENIED_REQUEST.replace("%leader%", getLeader().getName()));
	}
	
	
	public void leave(UhcPlayer player) throws UhcTeamException{
		if(player.canLeaveTeam()){
			if(player.isTeamLeader()){
				getMembers().remove(player);
                player.setTeam(new UhcTeam(player));
				UhcPlayer newLeader = getMembers().get(0);
				player.sendMessage(ChatColor.GOLD+Lang.TEAM_LEAVE_AS_LEADER.replace("%newleader%", newLeader.getName()));
				for(UhcPlayer uhcPlayer : getMembers()){
                    uhcPlayer.sendMessage(ChatColor.GOLD+Lang.TEAM_LEADER_LEAVES.replace("%leader%", player.getName()).replace("%newleader%", newLeader.getName()));
				}
			}else{
				player.sendMessage(ChatColor.GOLD+Lang.TEAM_LEAVE_AS_PLAYER);
				getMembers().remove(player);
				player.setTeam(new UhcTeam(player));
				for(UhcPlayer teamMember : getMembers()){
					teamMember.sendMessage(ChatColor.GOLD+Lang.TEAM_PLAYER_LEAVES.replace("%player%", player.getName()));
				}
			}
		}else{
			throw new UhcTeamException(ChatColor.RED+Lang.TEAM_CANT_LEAVE);
		}
	}
	
	public UhcPlayer getLeader(){
		return getMembers().get(0);
	}
	
	public void setReady(boolean value){
		this.readyToStart = value;
	}
	
	
	public boolean isReadyToStart(){
		return readyToStart;
	}
	
	public boolean isOnline(){
		int membersOnline = 0;
		for(UhcPlayer uhcPlayer : getMembers()){
			try{
				Player player = uhcPlayer.getPlayer();
				if(player.isOnline())
					membersOnline++;
			}catch(UhcPlayerNotOnlineException e){
				// not adding playing to count
			}
		}
		return (membersOnline > 0);
	}

	public void changeReadyState(UhcPlayer uhcPlayer) throws UhcTeamException {
		if(GameManager.getGameManager().getGameState().equals(GameState.STARTING))
			throw new UhcTeamException(ChatColor.RED+Lang.TEAM_READY_TOGGLE_ERROR);
		
		if(uhcPlayer.isTeamLeader()){
			setReady(!isReadyToStart());
			for(UhcPlayer teamMember : getMembers()){
				if(isReadyToStart())
					teamMember.sendMessage(ChatColor.GOLD+Lang.TEAM_NOW_READY);
				else
					teamMember.sendMessage(ChatColor.GOLD+Lang.TEAM_NOW_NOT_READY);
			}
		}else{
			throw new UhcTeamException(ChatColor.RED+Lang.TEAM_NOT_LEADER);
		}
	}
	
	public List<UhcPlayer> getOtherMembers(UhcPlayer excludedPlayer){
		List<UhcPlayer> otherMembers = new ArrayList<UhcPlayer>();
		for(UhcPlayer uhcPlayer : getMembers()){
			if(!uhcPlayer.equals(excludedPlayer))
				otherMembers.add(uhcPlayer);
		}
		return otherMembers;
	}

	public void regenTeam() {
		for(UhcPlayer uhcPlayer : getMembers()){
			uhcPlayer.sendMessage(ChatColor.GREEN+Lang.ITEMS_REGEN_HEAD_ACTION);
			try{
				Player p = uhcPlayer.getPlayer();
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,100,1));
			}catch(UhcPlayerNotOnlineException e){
				// No regen for offline players
			}
		}
		
	}


	
	public void setStartingLocation(Location loc){
		this.startingLocation = loc;
	}
	
	public Location getStartingLocation(){
		return startingLocation;
	}

}
