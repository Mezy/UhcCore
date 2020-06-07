package com.gmail.val59000mc.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class JsonItemStack extends ItemStack{

    private int minimum, maximum;

    public JsonItemStack(Material material){
        super(material);
        minimum = 1;
        maximum = 1;
    }

    public JsonItemStack(ItemStack stack){
        super(stack);
        minimum = 1;
        maximum = 1;
    }

    public int getMinimum(){
        return minimum;
    }

    public int getMaximum(){
        return maximum;
    }

    public void setMinimum(int minimum){
        this.minimum = minimum;
    }

    public void setMaximum(int maximum){
        Validate.isTrue(maximum <= getType().getMaxStackSize(), "Maximum item count can't be more than the max stack size ("+getType().getMaxStackSize()+")!");
        this.maximum = maximum;
    }

    @Override
    public int getAmount(){
        if (maximum == 1) {
            return super.getAmount();
        }
        return RandomUtils.randomInteger(minimum, maximum);
    }

    @Override
    public String toString() {
        return JsonItemUtils.getItemJson(this);
    }

}