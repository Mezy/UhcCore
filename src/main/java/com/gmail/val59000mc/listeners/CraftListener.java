package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.customitems.Craft;
import com.gmail.val59000mc.customitems.CraftsManager;
import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.UhcPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCrafting(CraftItemEvent event){
		ItemStack item = event.getRecipe().getResult();

		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
			Craft craft = CraftsManager.getCraft(item);
			if(craft != null){
				HumanEntity human = event.getWhoClicked();
				if(human instanceof Player){
					Player player = (Player) human;
					
					try {
						UhcPlayer uhcPlayer = GameManager.getGameManager().getPlayersManager().getUhcPlayer(player);
						if(GameManager.getGameManager().getConfiguration().getEnableCraftsPermissions() && !player.hasPermission("uhc-core.craft."+craft.getName())){
							uhcPlayer.sendMessage(ChatColor.RED+Lang.ITEMS_CRAFT_NO_PERMISSION.replace("%craft%",craft.getName()));
							event.setCancelled(true);
						}else{
							if(craft.getLimit() != -1 && (event.isShiftClick() || event.isRightClick())){
								uhcPlayer.sendMessage(ChatColor.RED+Lang.ITEMS_CRAFT_LEFT_CLICK.replace("%craft%", craft.getName()));
								event.setCancelled(true);
							}else{
								if(!uhcPlayer.addCraftedItem(craft.getName())){
									uhcPlayer.sendMessage(ChatColor.RED+Lang.ITEMS_CRAFT_LIMIT.replace("%craft%", craft.getName()).replace("%limit%",""+craft.getLimit()));
									event.setCancelled(true);
								}else{
									uhcPlayer.sendMessage(ChatColor.GREEN+Lang.ITEMS_CRAFT_CRAFTED.replace("%craft%", craft.getName()));					
								}
							}
						}
					} catch (UhcPlayerDoesntExistException e) {
						// No craft for offline players
					}
				}
			}
		}
	}

}