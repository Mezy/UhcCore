package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.scenarios.ScenarioListener;


import com.gmail.val59000mc.events.UhcStartedEvent;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TransporterListener extends ScenarioListener{
	
	
	 @EventHandler
	    public void onGameStarted(UhcStartedEvent e){
		 ItemStack pots = new ItemStack(Material.POTION, 64);
	     ItemMeta updog = pots.getItemMeta();
	     updog.setDisplayName("UpDog");
	     pots.setItemMeta(updog);
	     
	        for (UhcPlayer uhcPlayer : e.getPlayersManager().getOnlinePlayingPlayers()){
	            try {
	                Player player = uhcPlayer.getPlayer();
	                player.getInventory().addItem(pots);
	       	     	updog.setDisplayName("DownDog");
	       	     	pots.setItemMeta(updog);
	       	     	player.getInventory().addItem(pots);
	                
	            }catch (UhcPlayerNotOnlineException ex){
	                // No rod for offline players
	            }
	        }
	    }
	 
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event)
    {
    	
    	if(event.isCancelled()) return;
    	Player player = event.getPlayer();
    	if (event.getItem().getItemMeta().hasDisplayName()) {
    		String display = event.getItem().getItemMeta().getDisplayName();
		    	if(display.equalsIgnoreCase("DownDog"))
		    	{	
		    		Location loc = event.getPlayer().getLocation();
		    		loc.setY(loc.getY() - 1);
		    		Block b = loc.getBlock();
		    		while(b.getType()!=Material.AIR && b.getType()!=Material.CAVE_AIR)
		    				{		
		    					loc.setY(loc.getY() - 1);
		    					b = loc.getBlock();
		    					 if(b.getType()==Material.BEDROCK)
		    					 {
		    						 player.sendMessage("There is only bedrock under you");
		    						 return;
		    					 }
		    				}
		    		player.sendMessage("There was a vein below you");
		    		while(b.getType()==Material.AIR || b.getType()==Material.CAVE_AIR)
					{		
						loc.setY(loc.getY() - 1);
						b = loc.getBlock();
						 if(b.getType()!=Material.AIR && b.getType()!=Material.CAVE_AIR)
						 {
							 loc.setY(loc.getY() + 1);
							 player.teleport(loc);
							 return;
						 }
					}
		    		
		    	}
		    	if(display.equalsIgnoreCase("UpDog"))
		    	{	
		    		Location loc = event.getPlayer().getLocation();
		    		loc.setY(loc.getY());
		    		Block b = loc.getBlock();
		    		int count = 0;
		    		while((b.getType()==Material.AIR || b.getType()==Material.CAVE_AIR || b.getType()==Material.WATER) && count<200)
		    				{		
		    					loc.setY(loc.getY() + 1);
		    					b = loc.getBlock();
		    					count++;
		    					if(count == 199)
		    					{return;}
		    				}
		    		count = 0;
		    		while((b.getType()!=Material.AIR && b.getType()!=Material.CAVE_AIR && b.getType()!=Material.WATER) && count<200)
					{		
						loc.setY(loc.getY()+1);
						b = loc.getBlock();
						count++;
						if(count == 199)
						{return;}
					}
		    		player.sendMessage("Going up!");
		    		player.teleport(loc);
		    		
		    	}
    	}
		    	
    }
     
	

}