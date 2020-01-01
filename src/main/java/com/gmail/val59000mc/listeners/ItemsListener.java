package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.customitems.*;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.exceptions.UhcTeamException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioManager;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRightClickItem(PlayerInteractEvent event){
		if (
				event.getAction() != Action.RIGHT_CLICK_AIR &&
				event.getAction() != Action.RIGHT_CLICK_BLOCK
		){
			return;
		}

		Player player = event.getPlayer();
		GameManager gm = GameManager.getGameManager();
		UhcPlayer uhcPlayer = gm.getPlayersManager().getUhcPlayer(player);
		ItemStack hand = player.getItemInHand();

		if (GameItem.isLobbyItem(hand)){
			event.setCancelled(true);
			GameItem lobbyItem = GameItem.getLobbyItem(hand);

			switch (lobbyItem){
				case TEAM_SELECTION:
					UhcItems.openTeamInventory(player);
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
				case SCENARIO_VIEWER:
					Inventory inv;
					if (gm.getConfiguration().getEnableScenarioVoting()){
						inv = gm.getScenarioManager().getScenarioVoteInventory(uhcPlayer);
					}else {
						inv = gm.getScenarioManager().getScenarioMainInventory(player.hasPermission("uhc-core.scenarios.edit"));
					}
					player.openInventory(inv);
					break;
				case BUNGEE_ITEM:
					GameManager.getGameManager().getPlayersManager().sendPlayerToBungeeServer(player);
					break;
				case COMPASS_ITEM:
					uhcPlayer.pointCompassToNextPlayer(gm.getConfiguration().getPlayingCompassMode(), gm.getConfiguration().getPlayingCompassCooldown());
					break;
			}
			return;
		}

		if (gm.getGameState().equals(GameState.WAITING)
				&& UhcItems.isLobbyTeamItem(hand)
				&& uhcPlayer.getState().equals(PlayerState.WAITING)
				&& (event.getAction() == Action.RIGHT_CLICK_AIR
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK)
		) {
			event.setCancelled(true);
			Player itemPlayer = Bukkit.getPlayer(hand.getItemMeta().getDisplayName());
			if(itemPlayer != null){
				try {
					UhcPlayer uhcPlayerRequest = gm.getPlayersManager().getUhcPlayer(itemPlayer);
					uhcPlayer.getTeam().join(uhcPlayerRequest);
				} catch (UhcPlayerNotOnlineException | UhcTeamException e) {
					player.sendMessage(ChatColor.RED+e.getMessage());
				}
			}else{
				player.sendMessage(ChatColor.RED+ Lang.TEAM_PLAYER_JOIN_NOT_ONLINE);
			}

			player.getInventory().remove(hand);
			return;
		}

		if ( (gm.getGameState().equals(GameState.PLAYING) || gm.getGameState().equals(GameState.DEATHMATCH))
				&& UhcItems.isRegenHeadItem(hand)
				&& uhcPlayer.getState().equals(PlayerState.PLAYING)
				&& (event.getAction() == Action.RIGHT_CLICK_AIR
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK)
		) {
			event.setCancelled(true);
			uhcPlayer.getTeam().regenTeam(gm.getConfiguration().getEnableDoubleRegenHead());
			player.getInventory().remove(hand);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClickInInventory(InventoryClickEvent event){
		handleScenarioInventory(event);

		ItemStack item = event.getCurrentItem();
		GameManager gm = GameManager.getGameManager();
		Player player = (Player) event.getWhoClicked();
		UhcPlayer uhcPlayer = gm.getPlayersManager().getUhcPlayer(player);

		// Only handle clicked items.
		if (item == null){
			return;
		}

		// Stop players from moving game items in their inventory.
		if (gm.getGameState() == GameState.WAITING){
			if (GameItem.isLobbyItem(item)){
				event.setCancelled(true);
			}
		}
		
		// Click on a player head to join a team
		if(event.getView().getTitle().equals(Lang.ITEMS_KIT_INVENTORY)){
			if(KitsManager.isKitItem(item)){
				event.setCancelled(true);
				Kit kit = KitsManager.getKitByName(item.getItemMeta().getDisplayName());
				if(kit.canBeUsedBy(player)){
					uhcPlayer.setKit(kit);
					uhcPlayer.sendMessage(ChatColor.GREEN+Lang.ITEMS_KIT_SELECTED.replace("%kit%", kit.getName()));
				}else{
					uhcPlayer.sendMessage(ChatColor.RED+Lang.ITEMS_KIT_NO_PERMISSION);
				}
				player.closeInventory();
			}
		}
		
		if(event.getView().getTitle().equals(Lang.TEAM_INVENTORY)){
			// Click on a player head to join a team
			if(UhcItems.isLobbyTeamItem(item)){
				event.setCancelled(true);
				
				Player itemPlayer = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
				if(itemPlayer == player){
					player.sendMessage(ChatColor.RED+Lang.TEAM_CANNOT_JOIN_OWN_TEAM);
				}else if(itemPlayer != null){
					UhcPlayer leader = gm.getPlayersManager().getUhcPlayer(itemPlayer);
					try {
						leader.getTeam().askJoin(gm.getPlayersManager().getUhcPlayer(player), leader);
					}catch (UhcTeamException e){
						player.sendMessage(ChatColor.RED+e.getMessage());
					}
					
				}else{
					player.sendMessage(ChatColor.RED+Lang.TEAM_LEADER_JOIN_NOT_ONLINE);
				}
				
				player.closeInventory();
			}
		
			// Click on the barrier to leave a team
			if(UhcItems.isLobbyLeaveTeamItem(event.getCurrentItem())){
				event.setCancelled(true);
				
				if(!gm.getConfiguration().getPreventPlayerFromLeavingTeam()){
					try {
						uhcPlayer.getTeam().leave(uhcPlayer);
					}catch (UhcTeamException e) {
						player.sendMessage(e.getMessage());
					}
					player.closeInventory();
				}
			}
			
			// Click on the item to change ready state
			if(UhcItems.isLobbyReadyTeamItem(event.getCurrentItem())){
				event.setCancelled(true);
				
				if(!gm.getConfiguration().getTeamAlwaysReady()){
					try{
						uhcPlayer.getTeam().changeReadyState(uhcPlayer);
					}catch (UhcTeamException e){
						player.sendMessage(e.getMessage());
					}
					player.closeInventory();
				}
				
			}
			
		}

		if(event.getView().getTitle().equals(Lang.TEAM_COLOR_INVENTORY)){
			event.setCancelled(true);

			if (item.hasItemMeta() && item.getItemMeta().hasLore()){
				String selectedColor = item.getItemMeta().getLore().get(0).replace(ChatColor.RESET.toString(), "");
				player.closeInventory();

				// check if player is teamleader
				if (!uhcPlayer.isTeamLeader()){
					uhcPlayer.sendMessage(Lang.TEAM_COLOR_LEADER);
					return;
				}

				// check if already used by this team
				if (uhcPlayer.getTeam().getColor().contains(selectedColor)){
					uhcPlayer.sendMessage(Lang.TEAM_COLOR_ALREADY_SELECTED);
					return;
				}

				// check if still available
				String newPrefix = gm.getTeamManager().getTeamPrefix(selectedColor);
				if (newPrefix == null){
					uhcPlayer.sendMessage(Lang.TEAM_COLOR_UNAVAILABLE);
					return;
				}

				// assign color and update color on tab
				uhcPlayer.getTeam().setPrefix(newPrefix);
				for (UhcPlayer teamMember : uhcPlayer.getTeam().getMembers()){
					gm.getScoreboardManager().updatePlayerTab(teamMember);
				}

				uhcPlayer.sendMessage(Lang.TEAM_COLOR_CHANGED);
				return;
			}
		}

		if(event.getView().getTitle().equals(Lang.ITEMS_CRAFT_BOOK_INVENTORY)){
			event.setCancelled(true);
			
			if(CraftsManager.isCraftItem(item)){
				player.closeInventory();
				Craft craft = CraftsManager.getCraftByDisplayName(item.getItemMeta().getDisplayName());
				if(!gm.getConfiguration().getEnableCraftsPermissions() || (gm.getConfiguration().getEnableCraftsPermissions() && player.hasPermission("uhc-core.craft."+craft.getName()))){
					CraftsManager.openCraftInventory(player,craft);
				}else{
					player.sendMessage(ChatColor.RED+Lang.ITEMS_CRAFT_NO_PERMISSION.replace("%craft%",craft.getName()));
				}
			}
			
			if(CraftsManager.isCraftBookBackItem(item)){
				event.setCancelled(true);
				player.closeInventory();
				CraftsManager.openCraftBookInventory(player);
			}
			
		}
		
		// Ban level 2 potions
		if(event.getInventory().getType().equals(InventoryType.BREWING) && gm.getConfiguration().getBanLevelTwoPotions()){
			final BrewerInventory inv = (BrewerInventory) event.getInventory();
			final HumanEntity human = event.getWhoClicked();
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), new CheckBrewingStandAfterClick(inv.getHolder(),human),1);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHopperEvent(InventoryMoveItemEvent event) {
		Inventory inv = event.getDestination();
		if(inv.getType().equals(InventoryType.BREWING) && GameManager.getGameManager().getConfiguration().getBanLevelTwoPotions() && inv.getHolder() instanceof BrewingStand){
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), new CheckBrewingStandAfterClick((BrewingStand) inv.getHolder(),null),1);
		}
		
	}
	
	class CheckBrewingStandAfterClick implements Runnable {
        private BrewingStand stand;
        private HumanEntity human;
        public CheckBrewingStandAfterClick(BrewingStand stand, HumanEntity human) { 
        	this.stand = stand;
        	this.human = human;
        }
        
        public void run() {
        	ItemStack ingredient = stand.getInventory().getIngredient();
			if(ingredient != null && ingredient.getType().equals(Material.GLOWSTONE_DUST)){
				if(human != null)
					human.sendMessage(ChatColor.RED+Lang.ITEMS_POTION_BANNED);
				
				stand.getLocation().getWorld().dropItemNaturally(stand.getLocation(), ingredient.clone());
				stand.getInventory().setIngredient(new ItemStack(Material.AIR));
				
			}
        	
			
        }
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItemDrop().getItemStack();
		GameManager gm = GameManager.getGameManager();

		if (gm.getGameState() == GameState.WAITING && GameItem.isLobbyItem(item)){
			event.setCancelled(true);
			return;
		}

		UhcPlayer uhcPlayer = gm.getPlayersManager().getUhcPlayer(player);
		ItemStack playerRequestItem = new ItemStack(item);

		if (
				gm.getGameState().equals(GameState.WAITING)
				&& UhcItems.isLobbyTeamItem(playerRequestItem)
				&& uhcPlayer.getState().equals(PlayerState.WAITING)
		){
			Player itemPlayer = Bukkit.getPlayer(playerRequestItem.getItemMeta().getDisplayName());
			if(itemPlayer != null){
				UhcPlayer uhcPlayerRequest = gm.getPlayersManager().getUhcPlayer(itemPlayer);
				uhcPlayer.getTeam().denyJoin(uhcPlayerRequest);
			}else{
				player.sendMessage(ChatColor.RED+Lang.TEAM_PLAYER_NOT_ONLINE.replace("%player%", playerRequestItem.getItemMeta().getDisplayName()));
			}

			event.getItemDrop().remove();
		}
	}

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent e){
		if (e.getItem() == null) return;

		if (e.getItem().equals(UhcItems.createGoldenHead())){
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

		Player player = ((Player) e.getWhoClicked()).getPlayer();
		ItemStack item = e.getCurrentItem();
		ItemMeta meta = item.getItemMeta();
		GameManager gm = GameManager.getGameManager();
        PlayersManager pm = gm.getPlayersManager();
		ScenarioManager scenarioManager = gm.getScenarioManager();

		if (clickedInv.getTitle().equals(Lang.SCENARIO_GLOBAL_INVENTORY)){
			e.setCancelled(true);
			player.closeInventory();

			if (meta.getDisplayName().equals(Lang.SCENARIO_GLOBAL_ITEM_EDIT)) {
				Inventory inv = scenarioManager.getScenarioEditInventory();
				player.openInventory(inv);
				return;
			}

			Scenario scenario = Scenario.getScenario(meta.getDisplayName());
			if (scenario == null){
				Bukkit.getLogger().severe("[UhcCore] Could not find scenario from item with display name: " + meta.getDisplayName());
				return;
			}
			player.sendMessage(scenario.getInfo());
		}else if (clickedInv.getTitle().equals(Lang.SCENARIO_GLOBAL_INVENTORY_EDIT)){
			e.setCancelled(true);
			player.closeInventory();

			if (item.getItemMeta().getDisplayName().equals(Lang.SCENARIO_GLOBAL_ITEM_BACK)){
				Inventory inv = scenarioManager.getScenarioMainInventory(true);
				player.openInventory(inv);
				return;
			}

			for (Scenario scenario : Scenario.values()){

				if (scenario.equals(meta.getDisplayName())){
					// toggle scenario
					scenarioManager.toggleScenario(scenario);
					player.openInventory(scenarioManager.getScenarioEditInventory());
				}
			}
		}else if (clickedInv.getTitle().equals(Lang.SCENARIO_GLOBAL_INVENTORY_VOTE)){
            e.setCancelled(true);
            player.closeInventory();
            UhcPlayer uhcPlayer = pm.getUhcPlayer(player);

            for (Scenario scenario : Scenario.values()){

                if (scenario.equals(meta.getDisplayName())){
                    // toggle scenario
                    if (uhcPlayer.getScenarioVotes().contains(scenario)){
                        uhcPlayer.getScenarioVotes().remove(scenario);
                    }else {
                        int maxVotes = gm.getConfiguration().getMaxScenarioVotes();
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
	}

}