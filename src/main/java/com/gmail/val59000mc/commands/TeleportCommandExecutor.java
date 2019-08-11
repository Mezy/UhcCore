package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommandExecutor implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (!(sender instanceof Player)){
			sender.sendMessage("Only players can teleport!");
			return true;
		}

		Player player = (Player) sender;
		GameManager gm = GameManager.getGameManager();
		PlayersManager pm = gm.getPlayersManager();

		UhcPlayer uhcPlayer;
		try {
			uhcPlayer = pm.getUhcPlayer(player);

			if(
					player.hasPermission("uhc-core.commands.teleport-admin")
					|| (uhcPlayer.getState().equals(PlayerState.DEAD)
					&& gm.getConfiguration().getSpectatingTeleport())
			){

				if (args.length == 3 && player.hasPermission("uhc-core.commands.teleport-admin")){
					// teleport to coordinates
					double x, y, z;

					try {
						x = Double.parseDouble(args[0]);
						y = Double.parseDouble(args[1]);
						z = Double.parseDouble(args[2]);
					}catch (NumberFormatException ex){
						sender.sendMessage(ChatColor.RED + "Invalid coordinates!");
						return true;
					}

					Location loc = new Location(player.getWorld(), x, y, z);
					player.teleport(loc);

					player.sendMessage(ChatColor.GREEN+Lang.COMMAND_SPECTATING_TELEPORT.replace("%player%", x + "/" + y + "/" + z));
					return true;
				}

				if (args.length != 1){
					uhcPlayer.sendMessage(ChatColor.RED+Lang.COMMAND_SPECTATING_TELEPORT_ERROR);
					return true;
				}

				Player target = Bukkit.getPlayer(args[0]);
				if(target == null){
					uhcPlayer.sendMessage(ChatColor.RED+ Lang.COMMAND_SPECTATING_TELEPORT_ERROR);
					return true;
				}

				UhcPlayer uhcTarget = pm.getUhcPlayer(target);

				if(!uhcTarget.getState().equals(PlayerState.PLAYING) && !player.hasPermission("uhc-core.commands.teleport-admin")){
					uhcPlayer.sendMessage(ChatColor.RED+Lang.COMMAND_SPECTATING_TELEPORT_ERROR);
					return true;
				}

				uhcPlayer.sendMessage(ChatColor.GREEN+Lang.COMMAND_SPECTATING_TELEPORT.replace("%player%", uhcTarget.getName()));
				player.teleport(target);
				return true;
			}
			else{
				uhcPlayer.sendMessage(ChatColor.RED+Lang.COMMAND_SPECTATING_TELEPORT_ERROR);
			}
		} catch (UhcPlayerDoesntExistException e){
			// Nothing, player should always exist!
		}

		return true;
	}

}