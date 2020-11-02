package com.gmail.val59000mc.utils.equipment.slot;

import com.gmail.val59000mc.utils.equipment.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

public class StorageEquipmentSlot implements EquipmentSlot {

    private final int index;

    public StorageEquipmentSlot(int index) {
        this.index = index - 9;
    }

    @Override
    public void equip(PlayerInventory inventory, ItemStack stack) {
        Objects.requireNonNull(inventory, "Inventory cannot be null");
        Objects.requireNonNull(stack, "Stack cannot be null");

        if (index > -1 && index < inventory.getSize() && index < 26) {
            inventory.setItem(index + 9, stack);
        }
        else {
            inventory.addItem(stack);
        }
    }
}
