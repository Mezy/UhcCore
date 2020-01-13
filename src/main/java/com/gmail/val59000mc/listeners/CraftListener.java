package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.customitems.Craft;
import com.gmail.val59000mc.customitems.CraftsManager;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.UhcPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CraftListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCrafting(CraftItemEvent event){
		ItemStack item = event.getRecipe().getResult();
		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()){
			return;
		}

		if (!(event.getWhoClicked() instanceof Player)){
			return;
		}

		Craft craft = CraftsManager.getCraft(item);
		if (craft == null){
			return;
		}

		Player player = (Player) event.getWhoClicked();
		GameManager gm = GameManager.getGameManager();
		UhcPlayer uhcPlayer = gm.getPlayersManager().getUhcPlayer(player);

		if(gm.getConfiguration().getEnableCraftsPermissions() && !player.hasPermission("uhc-core.craft."+craft.getName())){
			uhcPlayer.sendMessage(ChatColor.RED+Lang.ITEMS_CRAFT_NO_PERMISSION.replace("%craft%",craft.getName()));
			event.setCancelled(true);
			return;
		}

		if(craft.getLimit() != -1 && (event.isShiftClick() || event.isRightClick())){
			uhcPlayer.sendMessage(ChatColor.RED+Lang.ITEMS_CRAFT_LEFT_CLICK.replace("%craft%", craft.getName()));
			event.setCancelled(true);
			return;
		}

		if (craft.isReviveItem()){
			List<UhcPlayer> deadMembers = uhcPlayer.getTeam().getDeadMembers();

			if (deadMembers.isEmpty()){
				event.setCancelled(true);
				uhcPlayer.sendMessage(Lang.ITEMS_REVIVE_ERROR);
				return;
			}

			UhcPlayer revivePlayer = deadMembers.get(0);
			gm.getPlayersManager().revivePlayer(revivePlayer);

			uhcPlayer.sendMessage(Lang.ITEMS_REVIVE_SUCCESS.replace("%player%", revivePlayer.getName()));

			Bukkit.getScheduler().runTask(UhcCore.getPlugin(), () -> {
				player.setItemOnCursor(null);
				player.closeInventory();
			});
			return;
		}

		if(!uhcPlayer.addCraftedItem(craft.getName())){
			uhcPlayer.sendMessage(ChatColor.RED+Lang.ITEMS_CRAFT_LIMIT.replace("%craft%", craft.getName()).replace("%limit%",""+craft.getLimit()));
			event.setCancelled(true);
		}else{
			uhcPlayer.sendMessage(ChatColor.GREEN+Lang.ITEMS_CRAFT_CRAFTED.replace("%craft%", craft.getName()));
		}
	}

}