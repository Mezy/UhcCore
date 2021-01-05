package com.gmail.val59000mc.customitems;

import com.gmail.val59000mc.configuration.MainConfig;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Kit{

	private final String key;
	private String name;
	private ItemStack symbol;
	private final List<ItemStack> items;

	private Kit(String key){
		this.key = key;
		items = new ArrayList<>();
	}

	public String getKey(){
		return key;
	}

	public String getName(){
		return name;
	}

	public ItemStack getSymbol(){
		return symbol;
	}

	public ItemStack[] getItems(){
		return items.toArray(new ItemStack[]{});
	}

	public boolean canBeUsedBy(Player player, MainConfig configuration){
		return !configuration.get(MainConfig.ENABLE_KITS_PERMISSIONS) || player.hasPermission("uhc-core.kit."+key);
	}

	/**
	 * This class is used to create kits.
	 */
	public static class Builder{
		private final Kit kit;

		/**
		 * Builder constructor
		 * @param key They key of the kit.
		 */
		public Builder(String key){
			Validate.notNull(key);
			kit = new Kit(key);
		}

		/**
		 * Method used to set the kit name.
		 * @param name The name of the kit.
		 * @return Returns this kit builder.
		 */
		public Builder setName(String name){
			Validate.notNull(name);
			kit.name = name;
			return this;
		}

		/**
		 * Method used to set the symbol of the kit, this is used in the kit selection inventory.
		 * @param symbol The kit symbol.
		 * @return Returns this kit builder.
		 */
		public Builder setSymbol(ItemStack symbol){
			Validate.notNull(symbol);
			kit.symbol = symbol;
			return this;
		}

		/**
		 * Method used to add a item to the kit.
		 * @param item The item getting added.
		 * @return Returns this kit builder.
		 */
		public Builder addItem(ItemStack item){
			Validate.notNull(item);
			kit.items.add(item);
			return this;
		}

		/**
		 * Method used to build the kit.
		 * @return Returns the kit object.
		 */
		public Kit build(){
			Validate.isTrue(kit.name != null, "The kit name has not been assigned.");
			Validate.isTrue(kit.symbol != null, "The kit symbol has not been assigned.");
			Validate.isTrue(!kit.items.isEmpty(), "The kit does not have items yet.");

			return kit;
		}
	}

}