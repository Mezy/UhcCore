package com.gmail.val59000mc.players;

import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.customitems.UhcItems;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.exceptions.UhcTeamException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scoreboard.ScoreboardManager;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class UhcTeam {

	private List<UhcPlayer> members;
	private boolean readyToStart;
	private Location startingLocation;
	private int teamNumber;
	private String prefix;
	private Inventory teamInventory;

	public UhcTeam(UhcPlayer uhcPlayer) {
		members = new ArrayList<>();
		readyToStart = GameManager.getGameManager().getConfiguration().getTeamAlwaysReady();
		teamNumber = GameManager.getGameManager().getTeamManager().getNewTeamNumber();
		prefix = GameManager.getGameManager().getTeamManager().getTeamPrefix();
		members.add(uhcPlayer);
		teamInventory = Bukkit.createInventory(null, 9*3, ChatColor.GOLD + "Team Inventory");
	}

	public int getTeamNumber() {
		return teamNumber;
	}

	public String getPrefix() {
		return prefix + "\u25A0 ";
	}

	public Inventory getTeamInventory() {
		return teamInventory;
	}

	public void sendChatMessageToTeamMembers(String message){
		sendMessage(ChatColor.GREEN+"[Team] "+message);
	}

	public void sendMessage(String message){
		for(UhcPlayer member: members){
			try {
				member.getPlayer().sendMessage(message);
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

	public int getMemberCount(){
		return members.size();
	}

	public boolean isSolo(){
		return getMemberCount() == 1;
	}

	public List<UhcPlayer> getPlayingMembers(){
		List<UhcPlayer> playingMembers = new ArrayList<>();
		for(UhcPlayer uhcPlayer : getMembers()){
			if(uhcPlayer.getState().equals(PlayerState.PLAYING)){
				playingMembers.add(uhcPlayer);
			}
		}
		return playingMembers;
	}

	public List<UhcPlayer> getOnlinePlayingMembers(){
		List<UhcPlayer> playingMembers = new ArrayList<>();
		for(UhcPlayer uhcPlayer : getMembers()){
			if(uhcPlayer.getState().equals(PlayerState.PLAYING) && uhcPlayer.isOnline()){
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

	public void join(UhcPlayer player) throws UhcPlayerNotOnlineException, UhcTeamException {
		if(player.canJoinATeam()){
			if(isFull()){
				player.sendMessage(ChatColor.RED+ Lang.TEAM_FULL.replace("%player%", player.getName()).replace("%leader%", getLeader().getName()).replace("%limit%", ""+ GameManager.getGameManager().getConfiguration().getMaxPlayersPerTeam()));
				throw new UhcTeamException(ChatColor.RED+ Lang.TEAM_FULL.replace("%player%", player.getName()).replace("%leader%", getLeader().getName()).replace("%limit%", ""+ GameManager.getGameManager().getConfiguration().getMaxPlayersPerTeam()));
			}else{
				player.getPlayer().sendMessage(ChatColor.GREEN+ Lang.TEAM_JOIN_AS_PLAYER.replace("%leader%", getLeader().getName()));
				for(UhcPlayer teamMember : getMembers()){
					teamMember.getPlayer().sendMessage(ChatColor.GREEN+ Lang.TEAM_PLAYER_JOINS.replace("%player%",player.getName()));
				}
				getMembers().add(player);
				player.setTeam(this);

				// Update player tab
				ScoreboardManager scoreboardManager = GameManager.getGameManager().getScoreboardManager();
				scoreboardManager.updatePlayerTab(player);
			}
		}else{
			throw new UhcTeamException(ChatColor.RED+ Lang.TEAM_PLAYER_ALREADY_IN_TEAM.replace("%player%", player.getName()));
		}
	}

	public boolean isFull() {
		MainConfiguration cfg = GameManager.getGameManager().getConfiguration();
		return (cfg.getMaxPlayersPerTeam() == getMembers().size());
	}

	public void askJoin(UhcPlayer player, UhcPlayer teamLeader) throws UhcTeamException{
		if (GameManager.getGameManager().getScenarioManager().isActivated(Scenario.LOVEATFIRSTSIGHT)){
			player.sendMessage(Lang.SCENARIO_LOVEATFIRSTSIGHT_JOIN_ERROR);
			return;
		}

		if(!player.canJoinATeam())
			throw new UhcTeamException(ChatColor.RED+ Lang.TEAM_ALREADY_IN_TEAM);

		boolean alreadyRequested;
		try{
			alreadyRequested= UhcItems.doesInventoryContainsLobbyTeamItem(teamLeader.getPlayer().getInventory(), player.getName());
		}catch(UhcPlayerNotOnlineException e){
			alreadyRequested = false;
		}

		if(alreadyRequested)
			throw new UhcTeamException(ChatColor.RED+ Lang.TEAM_REQUEST_ALREADY_SENT);

		if(teamLeader.isTeamLeader()){
			ItemStack head = VersionUtils.getVersionUtils().createPlayerSkull(player.getName(), player.getUuid());
			ItemMeta im = head.getItemMeta();
			im.setDisplayName(player.getName());

			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.DARK_PURPLE+ Lang.TEAM_REQUEST_HEAD);
			im.setLore(lore);

			head.setItemMeta(im);
			try {
				teamLeader.getPlayer().getInventory().addItem(head);
				teamLeader.sendMessage(ChatColor.GREEN+ Lang.TEAM_REQUEST_RECEIVED.replace("%player%", player.getName()));
				player.sendMessage(ChatColor.GREEN+ Lang.TEAM_REQUEST_SENT.replace("%leader%", teamLeader.getName()));
			}catch (UhcPlayerNotOnlineException e) {
				throw new UhcTeamException(ChatColor.RED+ Lang.TEAM_PLAYER_NOT_ONLINE.replace("%player%", teamLeader.getName()));
			}
		}else{
			throw new UhcTeamException(ChatColor.RED+ Lang.TEAM_NO_LONGER_EXISTS);
		}
	}

	public void denyJoin(UhcPlayer player){
		getLeader().sendMessage(ChatColor.RED+ Lang.TEAM_DENY_REQUEST.replace("%player%", player.getName()));
		player.sendMessage(ChatColor.RED+ Lang.TEAM_DENIED_REQUEST.replace("%leader%", getLeader().getName()));
	}


	public void leave(UhcPlayer player) throws UhcTeamException {
		if(player.canLeaveTeam()){

			getMembers().remove(player);
			player.setTeam(new UhcTeam(player));

			// Update player tab
			GameManager.getGameManager().getScoreboardManager().updatePlayerTab(player);
			UhcPlayer newLeader = getMembers().get(0);

			if(player.isTeamLeader()){

				player.sendMessage(ChatColor.GOLD+ Lang.TEAM_LEAVE_AS_LEADER.replace("%newleader%", newLeader.getName()));
				for(UhcPlayer uhcPlayer : getMembers()){
					uhcPlayer.sendMessage(ChatColor.GOLD+ Lang.TEAM_LEADER_LEAVES.replace("%leader%", player.getName()).replace("%newleader%", newLeader.getName()));
				}
			}else{
				player.sendMessage(ChatColor.GOLD+ Lang.TEAM_LEAVE_AS_PLAYER);
				for(UhcPlayer teamMember : getMembers()){
					teamMember.sendMessage(ChatColor.GOLD+ Lang.TEAM_PLAYER_LEAVES.replace("%player%", player.getName()));
				}
			}
		}else{
			throw new UhcTeamException(ChatColor.RED+ Lang.TEAM_CANT_LEAVE);
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
			throw new UhcTeamException(ChatColor.RED+ Lang.TEAM_READY_TOGGLE_ERROR);

		if(uhcPlayer.isTeamLeader()){
			setReady(!isReadyToStart());
			for(UhcPlayer teamMember : getMembers()){
				if(isReadyToStart())
					teamMember.sendMessage(ChatColor.GOLD+ Lang.TEAM_NOW_READY);
				else
					teamMember.sendMessage(ChatColor.GOLD+ Lang.TEAM_NOW_NOT_READY);
			}
		}else{
			throw new UhcTeamException(ChatColor.RED+ Lang.TEAM_NOT_LEADER);
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
			uhcPlayer.sendMessage(ChatColor.GREEN+ Lang.ITEMS_REGEN_HEAD_ACTION);
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