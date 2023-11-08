package com.ceylon.cutter.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;
    private List<Component> lore = new ArrayList<>();

    public ItemBuilder(Material material) {
        this(material, 1);
    }
    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = this.itemStack.getItemMeta();
    }
    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = new ItemStack(itemStack);
        this.itemMeta = this.itemStack.getItemMeta();
        if(this.itemMeta.hasLore()) {
            this.lore = this.itemMeta.lore();
        }
    }

    public ItemBuilder displayName(Component displayName) {
        this.itemMeta.displayName(displayName);
        return this;
    }

    public ItemBuilder displayName(String displayName) {
        this.itemMeta.displayName(Component.text(displayName));
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder addLore(Component lore) {
        this.lore.add(lore);
        return this;
    }

    public ItemBuilder addLore(String lore) {
        this.lore.add(Component.text(lore));
        return this;
    }

    public ItemBuilder setLore(List<Component> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder setDamage(int damage) {
        ((Damageable) this.itemMeta).setDamage(damage);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        this.itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        this.itemMeta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder setCustomModelData(int i) {
        this.itemMeta.setCustomModelData(i);
        return this;
    }

    public ItemStack build() {
        this.itemMeta.lore(this.lore);
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }
}
