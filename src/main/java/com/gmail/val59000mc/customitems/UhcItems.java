package com.gmail.val59000mc.customitems;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
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
import org.bukkit.event.inventory.InventoryType;
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

	public static void openTeamMainInventory(Player player, UhcPlayer uhcPlayer){
		List<ItemStack> items = new ArrayList<>();

		if (uhcPlayer.getTeam().isSolo()){
			// Invites item
			items.add(GameItem.TEAM_VIEW_INVITES.getItem());
		}

		if (uhcPlayer.isTeamLeader()){
			// Invite player item
			items.add(GameItem.TEAM_INVITE_PLAYER.getItem());
			// Team settings item
			items.add(GameItem.TEAM_SETTINGS.getItem());
		}

		items.add(GameItem.TEAM_LEAVE.getItem());

		player.openInventory(createInventory(items, Lang.TEAM_INVENTORY_MAIN));
	}

	public static void openTeamsListInventory(Player player){
		int maxSlots = 6*9;
		Inventory inv = Bukkit.createInventory(null, maxSlots, Lang.TEAM_INVENTORY_TEAMS_LIST);
		int slot = 0;
		GameManager gm = GameManager.getGameManager();
		List<UhcTeam> teams = gm.getPlayersManager().listUhcTeams();
		for(UhcTeam team : teams){
			// If team leader is spectating don't add skull to list.
			if (team.isSpectating()){
				continue;
			}

			if(slot < maxSlots){
				ItemStack item = createTeamSkullItem(team, !gm.getConfiguration().getTeamAlwaysReady());
				inv.setItem(slot, item);
				slot++;
			}
		}

		player.openInventory(inv);
	}

	public static void openTeamViewInventory(Player player, UhcTeam team){
		Inventory inv = Bukkit.createInventory(null, 9*3, Lang.TEAM_INVENTORY_TEAM_VIEW);

		for(UhcPlayer member : team.getMembers()){
			ItemStack item = VersionUtils.getVersionUtils().createPlayerSkull(member.getName(), member.getUuid());
			ItemMeta meta = item.getItemMeta();

			if (member.getState() == PlayerState.DEAD){
				meta.setDisplayName(ChatColor.RED + member.getName());
			}else {
				meta.setDisplayName(ChatColor.GREEN + member.getName());
			}

			item.setItemMeta(meta);
			inv.addItem(item);
		}

		player.openInventory(inv);
	}

	public static void openTeamInviteInventory(Player player){
		int maxSlots = 6*9;
		Inventory inv = Bukkit.createInventory(null, maxSlots, Lang.TEAM_INVENTORY_INVITE_PLAYER);
		int slot = 0;
		GameManager gm = GameManager.getGameManager();
		List<UhcTeam> teams = gm.getPlayersManager().listUhcTeams();
		for(UhcTeam team : teams){
			// If team leader is spectating don't add skull to list.
			if (team.isSpectating()){
				continue;
			}

			// Only solo players
			if (!team.isSolo()){
				continue;
			}

			// Don't show self
			if (team.getLeader().getUuid().equals(player.getUniqueId())){
				continue;
			}

			if(slot < maxSlots){
				UhcPlayer leader = team.getLeader();

				ItemStack item = VersionUtils.getVersionUtils().createPlayerSkull(leader.getName(), leader.getUuid());
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.GREEN + leader.getName());
				item.setItemMeta(meta);

				inv.setItem(slot, item);
				slot++;
			}
		}

		inv.setItem(maxSlots-1, GameItem.TEAM_INVITE_PLAYER_SEARCH.getItem());
		player.openInventory(inv);
	}

	public static void openTeamInvitesInventory(Player player, UhcPlayer uhcPlayer){
		Inventory inv = Bukkit.createInventory(null, 18, Lang.TEAM_INVENTORY_INVITES);

		uhcPlayer.getTeamInvites().forEach(team -> {
			inv.addItem(createTeamSkullItem(team, false));
		});

		player.openInventory(inv);
	}

	private static ItemStack createTeamSkullItem(UhcTeam team, boolean addReadyState){
		UhcPlayer leader = team.getLeader();
		String leaderName = leader.getName();
		ItemStack item = VersionUtils.getVersionUtils().createPlayerSkull(leaderName, leader.getUuid());
		List<String> membersNames = team.getMembersNames();
		ItemMeta im = item.getItemMeta();

		// Setting up lore with team members
		List<String> teamLore = new ArrayList<>();
		teamLore.add(ChatColor.GREEN+"Members");
		for(String teamMember : membersNames){
			teamLore.add(ChatColor.WHITE+teamMember);
		}

		if (addReadyState){
			// Ready State
			if(team.isReadyToStart()){
				teamLore.add(ChatColor.GREEN + "--- Ready ---");
			}else{
				teamLore.add(ChatColor.RED + "--- Not Ready ---");
			}
		}

		im.setLore(teamLore);

		im.setDisplayName(team.getTeamName());
		item.setItemMeta(im);
		return item;
	}

	public static boolean isTeamSkullItem(ItemStack item){
		return item.getType() == UniversalMaterial.PLAYER_HEAD.getType()
				&& item.hasItemMeta()
				&& item.getItemMeta().hasLore()
				&& item.getItemMeta().getLore().contains(ChatColor.GREEN+"Members");
	}

	public static void openTeamReplyInviteInventory(Player player, UhcTeam team){
		Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, Lang.TEAM_INVENTORY_REPLY_INVITE);

		inv.addItem(createTeamSkullItem(team, false));
		inv.addItem(GameItem.TEAM_INVITE_ACCEPT.getItem(ChatColor.DARK_GRAY + team.getTeamName()));
		inv.addItem(GameItem.TEAM_INVITE_DENY.getItem(ChatColor.DARK_GRAY + team.getTeamName()));

		player.openInventory(inv);
	}

	public static void openTeamSettingsInventory(Player player){
		List<ItemStack> items = new ArrayList<>();
		GameManager gm = GameManager.getGameManager();

		UhcPlayer uhcPlayer = gm.getPlayersManager().getUhcPlayer(player);

		// Team ready/not ready item
		if(!gm.getConfiguration().getTeamAlwaysReady()){
			if(uhcPlayer.getTeam().isReadyToStart()){
				items.add(GameItem.TEAM_READY.getItem());
			}else{
				items.add(GameItem.TEAM_NOT_READY.getItem());
			}
		}

		if (gm.getConfiguration().getUseTeamColors()){
			items.add(GameItem.TEAM_COLOR_SELECTION.getItem());
		}

		if (gm.getConfiguration().getEnableTeamNames()) {
			items.add(GameItem.TEAM_RENAME.getItem());
		}

		player.openInventory(createInventory(items, Lang.TEAM_INVENTORY_SETTINGS));
	}

	private static Inventory createInventory(List<ItemStack> items, String title){
		Inventory inv;
		int size = items.size();

		if (size < 4){
			inv = Bukkit.createInventory(null, InventoryType.HOPPER, title);
			fillInventory(inv);
		}else{
			inv = Bukkit.createInventory(null, 9, title);
		}

		if (size == 1){
			inv.setItem(2, items.get(0));
		}else if (size == 2){
			inv.setItem(1, items.get(0));
			inv.setItem(3, items.get(1));
		}else if (size == 3){
			inv.setItem(0, items.get(0));
			inv.setItem(2, items.get(1));
			inv.setItem(4, items.get(2));
		}else if (size == 4){
			inv.setItem(1, items.get(0));
			inv.setItem(3, items.get(1));
			inv.setItem(5, items.get(2));
			inv.setItem(7, items.get(3));
		}else if (size == 5){
			inv.setItem(0, items.get(0));
			inv.setItem(2, items.get(1));
			inv.setItem(4, items.get(2));
			inv.setItem(6, items.get(3));
			inv.setItem(8, items.get(4));
		}

		return inv;
	}

	private static void fillInventory(Inventory inv){
		for (int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, GameItem.TEAM_FILL_BLACK.getItem());
		}
	}

	public static void openTeamColorInventory(Player player){
		int maxSlots = 2*9;
		Inventory inv = Bukkit.createInventory(null, maxSlots, Lang.TEAM_INVENTORY_COLOR);
		GameManager gm = GameManager.getGameManager();
		TeamManager tm = gm.getTeamManager();

		for (String prefix : tm.getFreePrefixes()){
			if (prefix.contains(ChatColor.RED.toString())){
				inv.addItem(getWoolItem(ChatColor.RED, "Red", UniversalMaterial.RED_WOOL));
			}
			else if (prefix.contains(ChatColor.BLUE.toString())){
				inv.addItem(getWoolItem(ChatColor.BLUE, "Blue", UniversalMaterial.BLUE_WOOL));
			}
			else if (prefix.contains(ChatColor.DARK_GREEN.toString())){
				inv.addItem(getWoolItem(ChatColor.DARK_GREEN, "Dark Green", UniversalMaterial.GREEN_WOOL));
			}
			else if (prefix.contains(ChatColor.DARK_AQUA.toString())){
				inv.addItem(getWoolItem(ChatColor.DARK_AQUA, "Dark Aqua", UniversalMaterial.CYAN_WOOL));
			}
			else if (prefix.contains(ChatColor.DARK_PURPLE.toString())){
				inv.addItem(getWoolItem(ChatColor.DARK_PURPLE, "Dark Purple", UniversalMaterial.PURPLE_WOOL));
			}
			else if (prefix.contains(ChatColor.YELLOW.toString())){
				inv.addItem(getWoolItem(ChatColor.YELLOW, "Yellow", UniversalMaterial.YELLOW_WOOL));
			}
			else if (prefix.contains(ChatColor.GOLD.toString())){
				inv.addItem(getWoolItem(ChatColor.GOLD, "Gold", UniversalMaterial.ORANGE_WOOL));
			}
			else if (prefix.contains(ChatColor.GREEN.toString())){
				inv.addItem(getWoolItem(ChatColor.GREEN, "Green", UniversalMaterial.LIME_WOOL));
			}
			else if (prefix.contains(ChatColor.AQUA.toString())){
				inv.addItem(getWoolItem(ChatColor.AQUA, "Aqua", UniversalMaterial.LIGHT_BLUE_WOOL));
			}
			else if (prefix.contains(ChatColor.LIGHT_PURPLE.toString())){
				inv.addItem(getWoolItem(ChatColor.LIGHT_PURPLE, "Light Purple", UniversalMaterial.PINK_WOOL));
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

	public static boolean isRegenHeadItem(ItemStack item) {
		return (
				item != null 
				&& item.getType() == UniversalMaterial.PLAYER_HEAD.getType()
				&& item.hasItemMeta()
				&& item.getItemMeta().hasLore()
				&& item.getItemMeta().getLore().contains(Lang.ITEMS_REGEN_HEAD)
		);
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