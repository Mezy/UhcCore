package com.gmail.val59000mc.playuhc.mc1_8.players;


import com.gmail.val59000mc.playuhc.mc1_8.customitems.Craft;
import com.gmail.val59000mc.playuhc.mc1_8.customitems.CraftsManager;
import com.gmail.val59000mc.playuhc.mc1_8.customitems.Kit;
import com.gmail.val59000mc.playuhc.mc1_8.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.playuhc.mc1_8.game.GameManager;
import com.gmail.val59000mc.playuhc.mc1_8.languages.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UhcPlayer {
	String name;
	Scoreboard scoreboard;
	UhcTeam team;
	PlayerState state;
	boolean globalChat;
	Kit kit;
	Map<String,Integer> craftedItems;
	boolean hasBeenTeleportedToLocation;
	
	UhcPlayer compassPlayingCurrentPlayer;
	UhcPlayer compassSpectatingCurrentPlayer;
	
	public Player getPlayer() throws UhcPlayerNotOnlineException{
		Player player = Bukkit.getPlayer(name);
		if(player != null)
			return player;
		throw new UhcPlayerNotOnlineException(name);
	}
	
	public Boolean isOnline(){
		Player player = Bukkit.getPlayer(name);
		if(player == null)
			return false;
		return true;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	public synchronized UhcTeam getTeam() {
		return team;
	}
	public synchronized void setTeam(UhcTeam team) {
		this.team = team;
	}
	public PlayerState getState() {
		return state;
	}
	public void setState(PlayerState state) {
		this.state = state;
	}
	
	public UhcPlayer(String playerName){
		this.name = playerName;
		this.team = new UhcTeam(this);
		this.state = PlayerState.WAITING;
		this.globalChat = false;
		this.kit = null;
		this.craftedItems = new HashMap<String,Integer>();
		this.hasBeenTeleportedToLocation = false;
		
		compassPlayingCurrentPlayer = this;
		compassSpectatingCurrentPlayer = this;
	}
	
	public boolean addCraftedItem(String craftName){

		Integer quantity = 0;
		if(craftedItems.containsKey(craftName)){
			quantity = craftedItems.get(craftName);
		}
		
		Craft craft = CraftsManager.getCraftByName(craftName);
		if(craft != null && (craft.getLimit() == -1 || quantity+1 <= craft.getLimit())){
			craftedItems.put(craftName,	quantity+1);
			return true;
		}
		
		return false;
	}
	
	public void setUpScoreboard() {
		GameManager gm = GameManager.getGameManager();
		PlayersManager pm = gm.getPlayersManager();
		
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective health = scoreboard.registerNewObjective("health", "health");
		Objective information = scoreboard.registerNewObjective("informations", "dummy");
		Objective kills = scoreboard.registerNewObjective("kills", "playerKillCount");
		information.setDisplaySlot(DisplaySlot.SIDEBAR);
		information.setDisplayName(ChatColor.WHITE+Lang.DISPLAY_MESSAGE_PREFIX);
		health.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		kills.getScore(this.getName()).setScore(0);
		
		Team friends = scoreboard.registerNewTeam("friends");
		Team ennemies = scoreboard.registerNewTeam("ennemies");
		friends.setPrefix(ChatColor.GREEN+"");
		ennemies.setPrefix(ChatColor.RED+"");
		friends.setSuffix(ChatColor.RESET+"");
		ennemies.setSuffix(ChatColor.RESET+"");
		

		// Putting players in colored teams
		for(UhcPlayer uhcPlayer : pm.getPlayersList()){
				
			try {
				Player player = uhcPlayer.getPlayer();
				health.getScore(uhcPlayer.getName()).setScore(20);
				if(uhcPlayer.isInTeamWith(this))
					friends.addEntry(player.getName());
				else
					ennemies.addEntry(player.getName());
			} catch (UhcPlayerNotOnlineException e) {
				// No team for offline players
			}
		}
		
	
	
		try {
			getPlayer().setScoreboard(scoreboard);
		}catch (UhcPlayerNotOnlineException e) {
			// No scoreboard for offline players
		}
	}
	
	public void updateScoreboard(){
		
	}
	
	public boolean isInTeamWith(UhcPlayer player){
		return team.contains(player);
	}
	
	public boolean isTeamLeader(){
		return getTeam().getMembers().get(0).equals(this);
	}
	
	public boolean canJoinATeam(){
		return (getTeam().getMembers().get(0).equals(this) && getTeam().getMembers().size() == 1);
	}
	
	public boolean canLeaveTeam(){
		return (getTeam().getMembers().size() > 1);
	}
	
	public void sendMessage(String message){
		try {
			getPlayer().sendMessage(message);
		} catch (UhcPlayerNotOnlineException e) {
			// No message to send
		};
	}
	public boolean isGlobalChat() {
		return globalChat;
	}
	public void setGlobalChat(boolean globalChat) {
		this.globalChat = globalChat;
	}
	public void compassPlayingNextTeammate() {
		List<UhcPlayer> playingMembers = getTeam().getPlayingMembers();
		if((playingMembers.size() == 1 && playingMembers.get(0).equals(this)) || playingMembers.size() == 0){
			sendMessage(ChatColor.RED+Lang.ITEMS_COMPASS_PLAYING_ERROR);
		}else{
			int currentIndice = -1;
			for(int i = 0 ; i < playingMembers.size() ; i++){
				if(playingMembers.get(i).equals(compassPlayingCurrentPlayer))
					currentIndice = i;
			}
			
			// Switching to next player
			if(currentIndice == playingMembers.size()-1)
				currentIndice = 0;
			else
				currentIndice++;
			

			// Skipping player if == this
			if(playingMembers.get(currentIndice).equals(this))
				currentIndice++;
			
			// Correct indice if out of bounds
			if(currentIndice == playingMembers.size())
				currentIndice = 0;
			
			
			// Pointing compass
			UhcPlayer pointed = playingMembers.get(currentIndice);
			try {
				this.getPlayer().setCompassTarget(pointed.getPlayer().getLocation());
				sendMessage(ChatColor.GREEN+Lang.ITEMS_COMPASS_PLAYING_POINTING.replace("%player%", pointed.getName()));
			} catch (UhcPlayerNotOnlineException e) {
				sendMessage(ChatColor.RED+Lang.TEAM_PLAYER_NOT_ONLINE.replace("%player%", pointed.getName()));
			}
		}
		
	}
	public void compassSpectatingNextTeammate() {
		// TODO Auto-generated method stub
		
	}
	
	public Kit getKit() {
		return kit;
	}
	
	public void setKit(Kit kit) {
		this.kit = kit;
	}
	
	public void selectDefaultGlobalChat() {
		if(getTeam().getMembers().size() == 1){
			setGlobalChat(true);
		}		
	}
	
	public Location getStartingLocation(){
		return getTeam().getStartingLocation();
	}
	public boolean getHasBeenTeleportedToLocation() {
		return hasBeenTeleportedToLocation;
	}
	public void setHasBeenTeleportedToLocation(boolean hasBeenTeleportedToLocation) {
		this.hasBeenTeleportedToLocation = hasBeenTeleportedToLocation;
	}
}
