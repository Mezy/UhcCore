package com.gmail.val59000mc.configuration.gui;

import com.gmail.val59000mc.configuration.YamlFile;
import com.gmail.val59000mc.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Category{

    private String path, displayName;
    private YamlFile file;
    private Category parent;
    private List<Category> subCategories;
    private List<Option> options;

    public Category(String path, Category parent){
        this.path = path;
        file = null;
        this.parent = parent;
        this.subCategories = new ArrayList<>();
        this.options = new ArrayList<>();

        displayName = ChatColor.DARK_GREEN + path.replaceAll("-", " ");
    }

    public void setFile(YamlFile file) {
        this.file = file;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isRoot(){
        return parent == null;
    }

    public Category getParent() {
        return parent;
    }

    public void addSubCategory(Category category){
        subCategories.add(category);
    }

    public Category getCategory(String name, boolean deep){
        if (name.equals(ChatColor.DARK_GREEN + "root")){
            return this;
        }

        for (Category category : getSubCategories(deep)){
            if (category.getDisplayName().equals(name) || category.getDisplayName().length() > 32 && category.getDisplayName().substring(0,32).equals(name)){
                return category;
            }
        }
        return null;
    }

    public Option getOption(String name){
        for (Option option : options){
            if (option.getDisplayName().equals(name)){
                return option;
            }
        }
        return null;
    }

    public List<Category> getSubCategories(boolean deep) {
        if (!deep) {
            return subCategories;
        }

        List<Category> categories = new ArrayList<>(subCategories);

        for (Category category : subCategories){
            categories.addAll(category.getSubCategories(true));
        }

        return categories;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void loadOption(String name, Object value){
        if (value instanceof MemorySection){
            // Category so return
            return;
        }

        if (value instanceof Integer){
            options.add(new IntegerOption(name, this, (Integer) value));
        }else if (value instanceof Boolean){
            options.add(new BooleanOption(name, this, (Boolean) value));
        }
    }

    public Inventory getGui(){
        String title = displayName;
        if (title.length() > 32){
            title = title.substring(0, 32);
        }
        Inventory inv = Bukkit.createInventory(null, 9*6, title);

        for (Category category : subCategories){
            inv.addItem(category.getCategoryItem());
        }

        for (Option option : options){
            inv.addItem(option.getOptionItem());
        }

        return inv;
    }

    public ItemStack getCategoryItem(){
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }

    public String getConfigPath(){
        if (parent != null && !parent.isRoot()){
            return parent.getConfigPath() + "." + path;
        }
        return path;
    }

    public YamlFile getFile(){
        return parent == null ? file : parent.getFile();
    }

    public void saveConfig(){
        try {
            getFile().saveWithComments();
        }catch (IOException ex){
            throw new RuntimeException(ex);
        }

        // reload config
        GameManager.getGameManager().getConfiguration().load(getFile(), null);
    }

}