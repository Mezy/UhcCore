package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.scenarios.ScenarioManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScenarioCommandExecutor implements CommandExecutor{

    private ScenarioManager sm;

    public ScenarioCommandExecutor(){
        sm = GameManager.getGameManager().getScenarioManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender instanceof Player){
            Player p = ((Player) sender).getPlayer();
            // get inventory
            p.openInventory(sm.getScenarioMainInventory(p.hasPermission("uhc-core.scenarios.edit")));
            return true;
        }else {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
    }

}