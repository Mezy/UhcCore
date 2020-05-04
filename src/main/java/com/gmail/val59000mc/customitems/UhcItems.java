package com.gmail.val59000mc.customitems;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.TeamManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import com.gmail.val59000mc.utils.CompareUtils;
import com.gmail.val59000mc.utils.UniversalMaterial;
import com.gmail.val59000mc.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class UhcItems{

	public static void giveGameItemTo(Player player, GameItem gameItem){
		if (!gameItem.meetsUsageRequirements()){
			return;
		}

		if (gameItem == GameItem.BUNGEE_ITEM){
			player.getInventory().setItem(8, gameItem.getItem());
		}else {
			player.getInventory().addItem(gameItem.getItem());
		}
	}

	public static void giveLobbyItemsTo(Player player){
		for (GameItem lobbyItem : GameItem.LOBBY_ITEMS){
			giveGameItemTo(player, lobbyItem);
		}
	}
	
	public static void openTeamInventory(Player player){
		int maxSlots = 6*9;
		Inventory inv = Bukkit.createInventory(null, maxSlots, Lang.TEAM_INVENTORY);
		int slot = 0;
		GameManager gm = GameManager.getGameManager();
		List<UhcTeam> teams = gm.getPlayersManager().listUhcTeams();
		for(UhcTeam team : teams){
			// If team leader is spectating don't add skull to list.
			if (team.isSpectating()){
				continue;
			}

			if(slot < maxSlots){
				ItemStack item = createTeamSkullItem(team);
				inv.setItem(slot, item);
				slot++;
			}
		}
		
		// Leave team item
		if(!gm.getConfiguration().getPreventPlayerFromLeavingTeam()){
			ItemStack leaveTeamItem = new ItemStack(Material.BARRIER);
			ItemMeta imLeave = leaveTeamItem.getItemMeta();
			imLeave.setDisplayName(Lang.ITEMS_BARRIER);
			leaveTeamItem.setItemMeta(imLeave);
			inv.setItem(maxSlots-1, leaveTeamItem);
		}
		
		UhcPlayer uhcPlayer = gm.getPlayersManager().getUhcPlayer(player);

		// Team ready/not ready item
		if(uhcPlayer.isTeamLeader() && !gm.getConfiguration().getTeamAlwaysReady()){

			// Red Wool
			ItemStack readyTeamItem = UniversalMaterial.RED_WOOL.getStack();

			String readyState = Lang.TEAM_NOT_READY;

			if(uhcPlayer.getTeam().isReadyToStart()){
				// Lime Wool
				readyTeamItem = UniversalMaterial.LIME_WOOL.getStack();
				readyState = Lang.TEAM_READY;
			}

			ItemMeta imReady = readyTeamItem.getItemMeta();
			imReady.setDisplayName(readyState);
			imReady.setLore(Collections.singletonList(Lang.TEAM_READY_TOGGLE));
			readyTeamItem.setItemMeta(imReady);
			inv.setItem(maxSlots-2, readyTeamItem);
		}

		player.openInventory(inv);
	}

	public static void openTeamColorInventory(Player player){
		int maxSlots = 2*9;
		Inventory inv = Bukkit.createInventory(null, maxSlots, Lang.TEAM_COLOR_INVENTORY);
		GameManager gm = GameManager.getGameManager();
		TeamManager tm = gm.getTeamManager();

		for (String prefix : tm.getFreePrefixes()){
			if (prefix.contains(ChatColor.RED.toString())){
				inv.addItem(getWoolItem(ChatColor.RED, Lang.COLORS_RED, UniversalMaterial.RED_WOOL));
			}
			else if (prefix.contains(ChatColor.BLUE.toString())){
				inv.addItem(getWoolItem(ChatColor.BLUE, Lang.COLORS_BLUE, UniversalMaterial.BLUE_WOOL));
			}
			else if (prefix.contains(ChatColor.DARK_GREEN.toString())){
				inv.addItem(getWoolItem(ChatColor.DARK_GREEN, Lang.COLORS_DARK_GREEN, UniversalMaterial.GREEN_WOOL));
			}
			else if (prefix.contains(ChatColor.DARK_AQUA.toString())){
				inv.addItem(getWoolItem(ChatColor.DARK_AQUA, Lang.COLORS_DARK_AQUA, UniversalMaterial.CYAN_WOOL));
			}
			else if (prefix.contains(ChatColor.DARK_PURPLE.toString())){
				inv.addItem(getWoolItem(ChatColor.DARK_PURPLE, Lang.COLORS_DARK_PURPLE, UniversalMaterial.PURPLE_WOOL));
			}
			else if (prefix.contains(ChatColor.YELLOW.toString())){
				inv.addItem(getWoolItem(ChatColor.YELLOW, Lang.COLORS_YELLOW, UniversalMaterial.YELLOW_WOOL));
			}
			else if (prefix.contains(ChatColor.GOLD.toString())){
				inv.addItem(getWoolItem(ChatColor.GOLD, Lang.COLORS_GOLD, UniversalMaterial.ORANGE_WOOL));
			}
			else if (prefix.contains(ChatColor.GREEN.toString())){
				inv.addItem(getWoolItem(ChatColor.GREEN, Lang.COLORS_GREEN, UniversalMaterial.LIME_WOOL));
			}
			else if (prefix.contains(ChatColor.AQUA.toString())){
				inv.addItem(getWoolItem(ChatColor.AQUA, Lang.COLORS_AQUA, UniversalMaterial.LIGHT_BLUE_WOOL));
			}
			else if (prefix.contains(ChatColor.LIGHT_PURPLE.toString())){
				inv.addItem(getWoolItem(ChatColor.LIGHT_PURPLE, Lang.COLORS_LIGHT_PURPLE, UniversalMaterial.PINK_WOOL));
			}
		}

		player.openInventory(inv);
	}

	private static ItemStack getWoolItem(ChatColor chatColor, String name, UniversalMaterial woolType){
		ItemStack wool = woolType.getStack();
		ItemMeta woolMeta = wool.getItemMeta();
		woolMeta.setDisplayName(chatColor + name);
		woolMeta.setLore(Collections.singletonList(ChatColor.RESET + chatColor.toString()));
		wool.setItemMeta(woolMeta);
		return wool;
	}
	
	private static ItemStack createTeamSkullItem(UhcTeam team){
		UhcPlayer leader = team.getLeader();
		String leaderName = leader.getName();
		ItemStack item = VersionUtils.getVersionUtils().createPlayerSkull(leaderName, leader.getUuid());
		List<String> membersNames = team.getMembersNames();
		item.setAmount(membersNames.size());
		ItemMeta im = item.getItemMeta();
		
		// Setting up lore with team members
		List<String> teamLore = new ArrayList<>();
		teamLore.add(ChatColor.GREEN+"Members");
		for(String teamMember : membersNames){
			teamLore.add(ChatColor.WHITE+teamMember);
		}
		
		// Ready State
		if(team.isReadyToStart()){
			teamLore.add(ChatColor.GREEN + "--- " + Lang.TEAM_READY + " ---");
		}else{
			teamLore.add(ChatColor.RED + "--- " + Lang.TEAM_NOT_READY + " ---");
		}

		im.setLore(teamLore);

		im.setDisplayName(leaderName);
		item.setItemMeta(im);
		return item;
	}
	
	public static boolean isLobbyTeamItem(ItemStack item){
		if(item != null && item.getType() == UniversalMaterial.PLAYER_HEAD.getType()){
			List<String> lore = item.getItemMeta().getLore();
			return CompareUtils.stringListContains(lore, ChatColor.GREEN+"Members") || CompareUtils.stringListContains(lore, Lang.TEAM_REQUEST_HEAD);
		}
		return false;
	}
	
	public static boolean isLobbyLeaveTeamItem(ItemStack item){
			return (
					item != null 
					&& item.getType() == Material.BARRIER
					&& item.hasItemMeta()
					&& item.getItemMeta().getDisplayName().equals(Lang.ITEMS_BARRIER)
			);
	}

	public static boolean isLobbyReadyTeamItem(ItemStack item) {
		return (
				item != null 
				&& (item.getType() == UniversalMaterial.RED_WOOL.getType() || item.getType() == UniversalMaterial.LIME_WOOL.getType())
				&& item.hasItemMeta()
				&& (item.getItemMeta().getDisplayName().equals(Lang.TEAM_NOT_READY)
						|| item.getItemMeta().getDisplayName().equals(Lang.TEAM_READY))
		);
	}

	public static boolean isRegenHeadItem(ItemStack item) {
		return (
				item != null 
				&& item.getType() == UniversalMaterial.PLAYER_HEAD.getType()
				&& item.hasItemMeta()
				&& item.getItemMeta().hasLore()
				&& item.getItemMeta().getLore().contains(Lang.ITEMS_REGEN_HEAD)
		);
	}
	
	public static boolean doesInventoryContainsLobbyTeamItem(Inventory inv, String name){
		for(ItemStack item : inv.getContents()){
			if(item!=null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(name) && isLobbyTeamItem(item))
				return true;
		}
		return false;
	}

	public static ItemStack createRegenHead(UhcPlayer player) {
		String name = player.getName();
		ItemStack item = VersionUtils.getVersionUtils().createPlayerSkull(name, player.getUuid());
		ItemMeta im = item.getItemMeta();

		// Setting up lore with team members
		im.setLore(Collections.singletonList(Lang.ITEMS_REGEN_HEAD));
		im.setDisplayName(name);
		item.setItemMeta(im);

		return item;
	}

	public static void spawnExtraXp(Location location, int quantity) {
		ExperienceOrb orb = (ExperienceOrb) location.getWorld().spawnEntity(location, EntityType.EXPERIENCE_ORB);
		orb.setExperience(quantity);	
	}

	public static ItemStack createGoldenHeadPlayerSkull(String name, UUID uuid){

		ItemStack itemStack = VersionUtils.getVersionUtils().createPlayerSkull(name, uuid);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Lang.ITEMS_GOLDEN_HEAD_SKULL_NAME.replace("%player%", name));

		List<String> lore = new ArrayList<>();
		lore.add(Lang.ITEMS_GOLDEN_HEAD_SKULL_HELP);
		itemMeta.setLore(lore);

		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	public static ItemStack createGoldenHead(){
		ItemStack itemStack = new ItemStack(Material.GOLDEN_APPLE);
		ItemMeta itemMeta = itemStack.getItemMeta();

		itemMeta.setDisplayName(Lang.ITEMS_GOLDEN_HEAD_APPLE_NAME);
		itemMeta.setLore(Collections.singletonList(Lang.ITEMS_GOLDEN_HEAD_APPLE_HELP));

		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

}