package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.configuration.MainConfig;
import com.gmail.val59000mc.customitems.*;
import com.gmail.val59000mc.exceptions.UhcTeamException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.game.handlers.ScoreboardHandler;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.*;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioManager;
import com.gmail.val59000mc.utils.UniversalMaterial;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemsListener implements Listener {

	private final GameManager gameManager;
	private final MainConfig config;
	private final PlayerManager playerManager;
	private final TeamManager teamManager;
	private final ScenarioManager scenarioManager;
	private final ScoreboardHandler scoreboardHandler;

	public ItemsListener(
			GameManager gameManager,
			MainConfig config,
			PlayerManager playerManager,
			TeamManager teamManager,
			ScenarioManager scenarioManager,
			ScoreboardHandler scoreboardHandler) {
		this.gameManager = gameManager;
		this.config = config;
		this.playerManager = playerManager;
		this.teamManager = teamManager;
		this.scenarioManager = scenarioManager;
		this.scoreboardHandler = scoreboardHandler;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRightClickItem(PlayerInteractEvent event){
		if (
				event.getAction() != Action.RIGHT_CLICK_AIR &&
				event.getAction() != Action.RIGHT_CLICK_BLOCK
		){
			return;
		}

		Player player = event.getPlayer();
		UhcPlayer uhcPlayer = playerManager.getUhcPlayer(player);
		ItemStack hand = player.getItemInHand();

		if (GameItem.isGameItem(hand)){
			event.setCancelled(true);
			GameItem gameItem = GameItem.getGameItem(hand);
			handleGameItemInteract(gameItem, player, uhcPlayer, hand);
			return;
		}

		GameState state = gameManager.getGameState();
		if ((state == GameState.PLAYING || state == GameState.DEATHMATCH)
				&& UhcItems.isRegenHeadItem(hand)
				&& uhcPlayer.getState().equals(PlayerState.PLAYING)
				&& (event.getAction() == Action.RIGHT_CLICK_AIR
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK)
		) {
			event.setCancelled(true);
			uhcPlayer.getTeam().regenTeam(config.get(MainConfig.DOUBLE_REGEN_HEAD));
			player.getInventory().remove(hand);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClickInInventory(InventoryClickEvent event){
		handleScenarioInventory(event);

		ItemStack item = event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();
		UhcPlayer uhcPlayer = playerManager.getUhcPlayer(player);

		// Stop players from moving game items in their inventory.
		// Above item == null check as item is null on hotbar swap.
		if (gameManager.getGameState() == GameState.WAITING && event.getAction() == InventoryAction.HOTBAR_SWAP){
			event.setCancelled(true);
		}

		// Only handle clicked items.
		if (item == null){
			return;
		}

		// Listen for GameItems
		if (gameManager.getGameState() == GameState.WAITING){
			if (GameItem.isGameItem(item)){
				event.setCancelled(true);
				handleGameItemInteract(GameItem.getGameItem(item), player, uhcPlayer, item);
			}
		}

		if (event.getView().getTitle().equals(Lang.TEAM_INVENTORY_INVITE_PLAYER)){
			if (item.getType() != UniversalMaterial.PLAYER_HEAD.getType() || !item.hasItemMeta()){
				return;
			}

			event.setCancelled(true);
			player.closeInventory();

			String playerName = item.getItemMeta().getDisplayName().replace(ChatColor.GREEN.toString(), "");
			player.performCommand("team invite " + playerName);
		}

		if (event.getView().getTitle().equals(Lang.TEAM_INVENTORY_TEAM_VIEW)){
			if (item.getType() == UniversalMaterial.PLAYER_HEAD.getType() && item.hasItemMeta()){
				event.setCancelled(true);
			}
		}
		
		// Click on a player head to join a team
		if(event.getView().getTitle().equals(Lang.ITEMS_KIT_INVENTORY)){
			if(KitsManager.isKitItem(item)){
				event.setCancelled(true);
				Kit kit = KitsManager.getKitByName(item.getItemMeta().getDisplayName());
				if(kit.canBeUsedBy(player, config)){
					uhcPlayer.setKit(kit);
					uhcPlayer.sendMessage(Lang.ITEMS_KIT_SELECTED.replace("%kit%", kit.getName()));
				}else{
					uhcPlayer.sendMessage(Lang.ITEMS_KIT_NO_PERMISSION);
				}
				player.closeInventory();
			}
		}

		if (UhcItems.isTeamSkullItem(item)){
			event.setCancelled(true);

			UhcTeam team = teamManager.getTeamByName(item.getItemMeta().getDisplayName());

			// Click on a player head to reply to invite
			if(event.getView().getTitle().equals(Lang.TEAM_INVENTORY_INVITES)){
				if (team == null){
					player.sendMessage(Lang.TEAM_MESSAGE_NO_LONGER_EXISTS);
				}else{
					UhcItems.openTeamReplyInviteInventory(player, team);
				}
			}
			// Open team view inventory
			else{
				if (team == null){
					player.sendMessage(Lang.TEAM_MESSAGE_NO_LONGER_EXISTS);
				}else{
					UhcItems.openTeamViewInventory(player, team);
				}
			}
		}

		if(event.getView().getTitle().equals(Lang.TEAM_INVENTORY_COLOR)){
			event.setCancelled(true);

			if (item.hasItemMeta() && item.getItemMeta().hasLore()){
				String selectedColor = item.getItemMeta().getLore().get(0).replace(ChatColor.RESET.toString(), "");
				player.closeInventory();

				// check if already used by this team
				if (uhcPlayer.getTeam().getColor().contains(selectedColor)){
					uhcPlayer.sendMessage(Lang.TEAM_MESSAGE_COLOR_ALREADY_SELECTED);
					return;
				}

				// check if still available
				String newPrefix = teamManager.getTeamPrefix(selectedColor);
				if (newPrefix == null){
					uhcPlayer.sendMessage(Lang.TEAM_MESSAGE_COLOR_UNAVAILABLE);
					return;
				}

				// assign color and update color on tab
				uhcPlayer.getTeam().setPrefix(newPrefix);
				for (UhcPlayer teamMember : uhcPlayer.getTeam().getMembers()){
					scoreboardHandler.updatePlayerOnTab(teamMember);
				}

				uhcPlayer.sendMessage(Lang.TEAM_MESSAGE_COLOR_CHANGED);
				return;
			}
		}

		if(event.getView().getTitle().equals(Lang.ITEMS_CRAFT_BOOK_INVENTORY)){
			event.setCancelled(true);
			
			if(CraftsManager.isCraftItem(item)){
				player.closeInventory();
				Craft craft = CraftsManager.getCraftByDisplayName(item.getItemMeta().getDisplayName());
				if(!config.get(MainConfig.ENABLE_CRAFTS_PERMISSIONS) || (config.get(MainConfig.ENABLE_CRAFTS_PERMISSIONS) && player.hasPermission("uhc-core.craft."+craft.getName()))){
					CraftsManager.openCraftInventory(player,craft);
				}else{
					player.sendMessage(Lang.ITEMS_CRAFT_NO_PERMISSION.replace("%craft%", craft.getName()));
				}
			}
			
			if(CraftsManager.isCraftBookBackItem(item)){
				event.setCancelled(true);
				player.closeInventory();
				CraftsManager.openCraftBookInventory(player);
			}
			
		}
		
		// Ban level 2 potions
		if(event.getInventory().getType().equals(InventoryType.BREWING) && config.get(MainConfig.BAN_LEVEL_TWO_POTIONS)){
			final BrewerInventory inv = (BrewerInventory) event.getInventory();
			final HumanEntity human = event.getWhoClicked();
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), new CheckBrewingStandAfterClick(inv.getHolder(), human),1);
		}
	}

	private void handleGameItemInteract(GameItem gameItem, Player player, UhcPlayer uhcPlayer, ItemStack item){
		switch (gameItem){
			case TEAM_SELECTION:
				UhcItems.openTeamMainInventory(player, uhcPlayer);
				break;
			case TEAM_SETTINGS:
				UhcItems.openTeamSettingsInventory(player);
				break;
			case KIT_SELECTION:
				KitsManager.openKitSelectionInventory(player);
				break;
			case CUSTOM_CRAFT_BOOK:
				CraftsManager.openCraftBookInventory(player);
				break;
			case TEAM_COLOR_SELECTION:
				UhcItems.openTeamColorInventory(player);
				break;
			case TEAM_RENAME:
				openTeamRenameGUI(player, uhcPlayer.getTeam());
				break;
			case SCENARIO_VIEWER:
				Inventory inv;
				if (config.get(MainConfig.ENABLE_SCENARIO_VOTING)){
					inv = scenarioManager.getScenarioVoteInventory(uhcPlayer);
				}else {
					inv = scenarioManager.getScenarioMainInventory(player.hasPermission("uhc-core.scenarios.edit"));
				}
				player.openInventory(inv);
				break;
			case BUNGEE_ITEM:
				playerManager.sendPlayerToBungeeServer(player);
				break;
			case COMPASS_ITEM:
				uhcPlayer.pointCompassToNextPlayer(config.get(MainConfig.PLAYING_COMPASS_MODE), config.get(MainConfig.PLAYING_COMPASS_COOLDOWN));
				break;
			case TEAM_READY:
			case TEAM_NOT_READY:
				uhcPlayer.getTeam().changeReadyState();
				UhcItems.openTeamSettingsInventory(player);
				break;
			case TEAM_INVITE_PLAYER:
				UhcItems.openTeamInviteInventory(player);
				break;
			case TEAM_INVITE_PLAYER_SEARCH:
				openTeamInviteGUI(player);
				break;
			case TEAM_VIEW_INVITES:
				UhcItems.openTeamInvitesInventory(player, uhcPlayer);
				break;
			case TEAM_INVITE_ACCEPT:
				handleTeamInviteReply(uhcPlayer, item, true);
				player.closeInventory();
				break;
			case TEAM_INVITE_DENY:
				handleTeamInviteReply(uhcPlayer, item, false);
				player.closeInventory();
				break;
			case TEAM_LEAVE:
				try {
					uhcPlayer.getTeam().leave(uhcPlayer);

					// Update player tab
					scoreboardHandler.updatePlayerOnTab(uhcPlayer);
				}catch (UhcTeamException ex){
					uhcPlayer.sendMessage(ex.getMessage());
				}
				break;
			case TEAM_LIST:
				UhcItems.openTeamsListInventory(player);
				break;
		}
	}

	private void handleTeamInviteReply(UhcPlayer uhcPlayer, ItemStack item, boolean accepted){
		if (!item.hasItemMeta()){
			uhcPlayer.sendMessage("Something went wrong!");
			return;
		}

		ItemMeta meta = item.getItemMeta();

		if (!meta.hasLore()){
			uhcPlayer.sendMessage("Something went wrong!");
			return;
		}

		if (meta.getLore().size() != 2){
			uhcPlayer.sendMessage("Something went wrong!");
			return;
		}

		String line = meta.getLore().get(1).replace(ChatColor.DARK_GRAY.toString(), "");
		UhcTeam team = teamManager.getTeamByName(line);

		if (team == null){
			uhcPlayer.sendMessage(Lang.TEAM_MESSAGE_NO_LONGER_EXISTS);
			return;
		}

		teamManager.replyToTeamInvite(uhcPlayer, team, accepted);
	}

	private void openTeamRenameGUI(Player player, UhcTeam team){
		new AnvilGUI.Builder()
				.plugin(UhcCore.getPlugin())
				.title(Lang.TEAM_INVENTORY_RENAME)
				.text(team.getTeamName())
				.item(new ItemStack(Material.NAME_TAG))
				.onComplete(((p, s) -> {
					if (teamManager.isValidTeamName(s)){
						team.setTeamName(s);
						p.sendMessage(Lang.TEAM_MESSAGE_NAME_CHANGED);
						return AnvilGUI.Response.close();
					}else{
						p.sendMessage(Lang.TEAM_MESSAGE_NAME_CHANGED_ERROR);
						return AnvilGUI.Response.close();
					}
				}))
				.open(player);
	}

	private void openTeamInviteGUI(Player player){
		new AnvilGUI.Builder()
				.plugin(UhcCore.getPlugin())
				.title(Lang.TEAM_INVENTORY_INVITE_PLAYER)
				.text("Enter name ...")
				.item(new ItemStack(Material.NAME_TAG))
				.onComplete(((p, s) -> {
					p.performCommand("team invite " + s);
					return AnvilGUI.Response.close();
				}))
				.open(player);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHopperEvent(InventoryMoveItemEvent event) {
		Inventory inv = event.getDestination();
		if(inv.getType().equals(InventoryType.BREWING) && config.get(MainConfig.BAN_LEVEL_TWO_POTIONS) && inv.getHolder() instanceof BrewingStand){
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), new CheckBrewingStandAfterClick((BrewingStand) inv.getHolder(), null),1);
		}
		
	}
	
	private static class CheckBrewingStandAfterClick implements Runnable{
        private final BrewingStand stand;
        private final HumanEntity human;

        private CheckBrewingStandAfterClick(BrewingStand stand, HumanEntity human) {
        	this.stand = stand;
        	this.human = human;
        }

        @Override
        public void run(){
        	ItemStack ingredient = stand.getInventory().getIngredient();
			if(ingredient != null && ingredient.getType().equals(Material.GLOWSTONE_DUST)){
				if(human != null){
                    human.sendMessage(Lang.ITEMS_POTION_BANNED);
                }

				stand.getLocation().getWorld().dropItemNaturally(stand.getLocation(), ingredient.clone());
				stand.getInventory().setIngredient(new ItemStack(Material.AIR));
			}
        }
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();

		if (gameManager.getGameState() == GameState.WAITING && GameItem.isGameItem(item)){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent e){
		if (e.getItem() == null) return;

		Craft craft = CraftsManager.getCraft(e.getItem());
		if (craft != null){
			for (Craft.OnConsumeListener listener : craft.getOnConsumeListeners()) {
				if (listener.onConsume(playerManager.getUhcPlayer(e.getPlayer()))) {
					e.setCancelled(true);
					return;
				}
			}
		}

		if (e.getItem().isSimilar(UhcItems.createGoldenHead())){
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
		}
	}

	private void handleScenarioInventory(InventoryClickEvent e){
		if (!(e.getWhoClicked() instanceof Player)){
			return;
		}

		InventoryView clickedInv = e.getView();

		if (clickedInv == null || e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()){
			return;
		}

		Player player = (Player) e.getWhoClicked();
		UhcPlayer uhcPlayer = playerManager.getUhcPlayer(player);
		ItemStack item = e.getCurrentItem();
		ItemMeta meta = item.getItemMeta();

		boolean mainInventory = clickedInv.getTitle().equals(Lang.SCENARIO_GLOBAL_INVENTORY);
		boolean editInventory = clickedInv.getTitle().equals(Lang.SCENARIO_GLOBAL_INVENTORY_EDIT);
		boolean voteInventory = clickedInv.getTitle().equals(Lang.SCENARIO_GLOBAL_INVENTORY_VOTE);

		// No scenario inventory!
		if (!mainInventory && !editInventory && !voteInventory){
			return;
		}

		e.setCancelled(true);
		player.closeInventory();

		// Get scenario info when right click or when on the global inventory menu.
		if (e.getClick() == ClickType.RIGHT || mainInventory){
			// Handle edit item
			if (meta.getDisplayName().equals(Lang.SCENARIO_GLOBAL_ITEM_EDIT)) {
				uhcPlayer.setBrowsingPage(0);
				Inventory inv = scenarioManager.getScenarioEditInventory(0);
				player.openInventory(inv);
				return;
			}

			// Send scenario info
			scenarioManager.getScenarioByName(meta.getDisplayName()).ifPresent(sce -> {
				player.sendMessage(Lang.SCENARIO_GLOBAL_DESCRIPTION_HEADER.replace("%scenario%", sce.getInfo().getName()));
				sce.getInfo().getDescription().forEach(s -> player.sendMessage(Lang.SCENARIO_GLOBAL_DESCRIPTION_PREFIX + s));
			});
		}else if (editInventory){
			// Handle back item
			if (item.getItemMeta().getDisplayName().equals(Lang.SCENARIO_GLOBAL_ITEM_BACK)) {
				Inventory inv;

				int page = uhcPlayer.getBrowsingPage() - 1;
				if (page < 0) {
					inv = scenarioManager.getScenarioMainInventory(true);
				}else {
					uhcPlayer.setBrowsingPage(page);
					inv = scenarioManager.getScenarioEditInventory(page);
				}
				player.openInventory(inv);
				return;
			}
			// Handle next item
			if (item.getItemMeta().getDisplayName().equals(Lang.SCENARIO_GLOBAL_ITEM_NEXT)) {
				int page = uhcPlayer.getBrowsingPage() + 1;
				uhcPlayer.setBrowsingPage(page);
				Inventory inv = scenarioManager.getScenarioEditInventory(page);
				player.openInventory(inv);
				return;
			}

			// toggle scenario
			scenarioManager.getScenarioByName(meta.getDisplayName())
					.ifPresent(scenarioManager::toggleScenario);

			// Open edit inventory
			player.openInventory(scenarioManager.getScenarioEditInventory(uhcPlayer.getBrowsingPage()));
		}else if (voteInventory){
			// Clicked scenario
			Scenario scenario = scenarioManager.getScenarioByName(meta.getDisplayName()).orElse(null);

			// toggle scenario
			if (uhcPlayer.getScenarioVotes().contains(scenario)){
				uhcPlayer.getScenarioVotes().remove(scenario);
			}else{
				int maxVotes = config.get(MainConfig.MAX_SCENARIO_VOTES);
				if (uhcPlayer.getScenarioVotes().size() == maxVotes){
					player.sendMessage(Lang.SCENARIO_GLOBAL_VOTE_MAX.replace("%max%", String.valueOf(maxVotes)));
					return;
				}
				uhcPlayer.getScenarioVotes().add(scenario);
			}
			player.openInventory(scenarioManager.getScenarioVoteInventory(uhcPlayer));
        }
	}

}