package com.gmail.val59000mc.utils.equipment.slot;

import com.gmail.val59000mc.utils.equipment.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

public class HotbarEquipmentSlot implements EquipmentSlot {

    private final int index;

    public HotbarEquipmentSlot(int index) {
        this.index = index;
    }

    @Override
    public void equip(PlayerInventory inventory, ItemStack stack) {
        Objects.requireNonNull(inventory, "Inventory cannot be null");
        Objects.requireNonNull(stack, "Stack cannot be null");

        if (index > -1 && index < inventory.getSize() && index < 9) {
            inventory.setItem(index, stack);
        }
        else {
            inventory.addItem(stack);
        }
    }

}
