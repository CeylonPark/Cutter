package com.ceylon.cutter.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {
    public static String getPlainDisplayName(ItemStack itemStack) {
        if(itemStack == null) {
            return null;
        }
        Component component = itemStack.getItemMeta().displayName();
        return component == null ? "" : PlainComponentSerializer.plain().serialize(component);
    }
}
