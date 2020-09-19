package com.gmail.val59000mc.players;

import com.gmail.val59000mc.configuration.MainConfiguration;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.exceptions.UhcTeamException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class UhcTeam {

	private final List<UhcPlayer> members;
	private boolean readyToStart;
	private Location startingLocation;
	private final int teamNumber;
	private String teamName;
	private String prefix;
	private final Inventory teamInventory;

	public UhcTeam(UhcPlayer uhcPlayer) {
		members = new ArrayList<>();
		readyToStart = GameManager.getGameManager().getConfiguration().getTeamAlwaysReady();
		teamNumber = GameManager.getGameManager().getTeamManager().getNewTeamNumber();
		teamName = "Team " + teamNumber;
		prefix = GameManager.getGameManager().getTeamManager().getTeamPrefix();
		members.add(uhcPlayer);
		teamInventory = Bukkit.createInventory(null, 9*3, ChatColor.GOLD + "Team Inventory");
	}

	public int getTeamNumber() {
		return teamNumber;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getPrefix() {
		return prefix + "\u25A0 ";
	}

	public String getColor(){
		return prefix;
	}

	public void setPrefix(String prefix){
		this.prefix = prefix;
	}

	public Inventory getTeamInventory() {
		return teamInventory;
	}

	public void sendChatMessageToTeamMembers(UhcPlayer sender, String message){
		sendMessage(ChatColor.GREEN+"[Team] "+ChatColor.RESET+sender.getRealName()+": "+message);
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

	public boolean isSpectating(){
		return isSolo() && getLeader().getState() == PlayerState.DEAD;
	}

	public int getKills(){
		int i = 0;
		for (UhcPlayer uhcPlayer : members){
			i += uhcPlayer.kills;
		}
		return i;
	}

	public List<UhcPlayer> getDeadMembers(){
		List<UhcPlayer> deadMembers = new ArrayList<>();
		for(UhcPlayer uhcPlayer : getMembers()){
			if(uhcPlayer.getState().equals(PlayerState.DEAD)){
				deadMembers.add(uhcPlayer);
			}
		}
		return deadMembers;
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

	public List<String> getMembersNames(){
		List<String> names = new ArrayList<>();
		for(UhcPlayer player : getMembers()){
			names.add(player.getName());
		}
		return names;
	}

	public void join(UhcPlayer player) throws UhcTeamException {
		if(player.canJoinATeam()){
			if(isFull()){
				player.sendMessage(Lang.TEAM_MESSAGE_FULL.replace("%player%", player.getName()).replace("%leader%", getLeader().getName()).replace("%limit%", ""+ GameManager.getGameManager().getConfiguration().getMaxPlayersPerTeam()));
				throw new UhcTeamException(Lang.TEAM_MESSAGE_FULL.replace("%player%", player.getName()).replace("%leader%", getLeader().getName()).replace("%limit%", ""+ GameManager.getGameManager().getConfiguration().getMaxPlayersPerTeam()));
			}else{
				player.sendMessage(Lang.TEAM_MESSAGE_JOIN_AS_PLAYER.replace("%leader%", getLeader().getName()));
				for(UhcPlayer teamMember : getMembers()){
					teamMember.sendMessage(Lang.TEAM_MESSAGE_PLAYER_JOINS.replace("%player%",player.getName()));
				}
				getMembers().add(player);
				player.setTeam(this);

				// Update player tab
				ScoreboardManager scoreboardManager = GameManager.getGameManager().getScoreboardManager();
				scoreboardManager.updatePlayerTab(player);
			}
		}else{
			throw new UhcTeamException(Lang.TEAM_MESSAGE_PLAYER_ALREADY_IN_TEAM.replace("%player%", player.getName()));
		}
	}

	public boolean isFull() {
		MainConfiguration cfg = GameManager.getGameManager().getConfiguration();
		return (cfg.getMaxPlayersPerTeam() == getMembers().size());
	}

	public void leave(UhcPlayer player) throws UhcTeamException {
		if(player.canLeaveTeam()){

			boolean isLeader = player.isTeamLeader();
			getMembers().remove(player);
			player.setTeam(new UhcTeam(player));

			// Update player tab
			GameManager.getGameManager().getScoreboardManager().updatePlayerTab(player);
			UhcPlayer newLeader = getMembers().get(0);

			if(isLeader){
				player.sendMessage(Lang.TEAM_MESSAGE_LEAVE_AS_LEADER.replace("%newleader%", newLeader.getName()));
				for(UhcPlayer uhcPlayer : getMembers()){
					uhcPlayer.sendMessage(Lang.TEAM_MESSAGE_LEADER_LEAVES.replace("%leader%", player.getName()).replace("%newleader%", newLeader.getName()));
				}
			}else{
				player.sendMessage(Lang.TEAM_MESSAGE_LEAVE_AS_PLAYER);
				for(UhcPlayer teamMember : getMembers()){
					teamMember.sendMessage(Lang.TEAM_MESSAGE_PLAYER_LEAVES.replace("%player%", player.getName()));
				}
			}
		}else{
			throw new UhcTeamException(Lang.TEAM_MESSAGE_CANT_LEAVE);
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

	public void changeReadyState(){
		setReady(!isReadyToStart());
		for(UhcPlayer teamMember : getMembers()){
			if(isReadyToStart()) {
				teamMember.sendMessage(Lang.TEAM_MESSAGE_NOW_READY);
			}else {
				teamMember.sendMessage(Lang.TEAM_MESSAGE_NOW_NOT_READY);
			}
		}
	}

	public List<UhcPlayer> getOtherMembers(UhcPlayer excludedPlayer){
		List<UhcPlayer> otherMembers = new ArrayList<>();
		for(UhcPlayer uhcPlayer : getMembers()){
			if(!uhcPlayer.equals(excludedPlayer))
				otherMembers.add(uhcPlayer);
		}
		return otherMembers;
	}

	public void regenTeam(boolean doubleRegen) {
		for(UhcPlayer uhcPlayer : getMembers()){
			uhcPlayer.sendMessage(Lang.ITEMS_REGEN_HEAD_ACTION);
			try{
				Player p = uhcPlayer.getPlayer();
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,100,doubleRegen?2:1));
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