package com.ceylon.cutter.data;

import com.ceylon.cutter.CutterAddition;
import com.ceylon.cutter.CutterConfig;
import com.ceylon.cutter.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CutterItem {
    private static final Pattern LORE_PATTERN = Pattern.compile("[a-zA-Z가-힣0-9\\s]+\\s[x|X]\\s-*\\d+");

    private ItemStack cutterItem;
    private List<CutterAbility> cutterAbilities;
    private int percent;
    private boolean cutting;

    public CutterItem(ItemStack cutterItem) {
        if(cutterItem == null || cutterItem.getAmount() != 1) {
            return;
        }

        String displayName = ItemUtil.getPlainDisplayName(cutterItem);
        if(!displayName.contains("세공되지 않은") || displayName.equals("세공되지 않은")) {
            return;
        }

        List<Component> lore = cutterItem.lore();
        if(lore == null) {
            return;
        }

        List<CutterAbility> cutterAbilities = new ArrayList<>();
        for(int i = 0; i < lore.size(); i++) {
            String text = PlainComponentSerializer.plain().serialize(lore.get(i));
            if(!CutterItem.LORE_PATTERN.matcher(text).find()) {
                continue;
            }
            String[] split = text.contains("x") ? text.split(" x ") : text.split(" X ");
            if(split.length != 2) {
                continue;
            }
            try {
                cutterAbilities.add(new CutterAbility(split[0], Integer.parseInt(split[1]), i));
            } catch (NumberFormatException ignored) { }
        }

        if(cutterAbilities.size() != 3) {
            return;
        }

        this.cutterItem = cutterItem.clone();
        this.cutterAbilities = cutterAbilities;
        this.percent = CutterConfig.getInstance().getInitial();
        this.cutting = false;
    }

    public boolean doCutting(int line, int order, Player player) {
        CutterAbility cutterAbility = this.cutterAbilities.get((line));
        if(cutterAbility.getCount() != order) {
            return false;
        }
        if(!this.isCutting()) {
            this.cutting = true;
        }
        // Add Cutter Addition
        int percent = this.percent + (line == 2 ? 0 : CutterAddition.getInstance().getAddition(player.getName()));
        boolean result = Math.random() < ((double) percent / 100);
        cutterAbility.addCount(result);
        if(cutterAbility.isCut() && this.isCut()) {
            this.cutting = false;
            this.cutCutterItem();
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, (float) 1.0, (float) 1.0);
        }
        CutterConfig cutterConfig = CutterConfig.getInstance();
        if(result) {
            this.percent = Math.max(this.percent - cutterConfig.getStep(), cutterConfig.getMinimum());
            player.playSound(player.getLocation(),  Sound.BLOCK_ANVIL_LAND , (float) 1.0, (float) 1.0);
        } else {
            this.percent = Math.min(this.percent + cutterConfig.getStep(), cutterConfig.getMaximum());
            player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, (float) 1.0, (float) 1.0);
        }
        return true;
    }

    public ItemStack getCutterItem() {
        return this.cutterItem == null ? null : this.cutterItem.clone();
    }

    public boolean isCutterItem() {
        return this.cutterItem != null && this.cutterAbilities != null;
    }

    public boolean isCutting() {
        return this.cutting;
    }

    public String[] getAbilityNames() {
        String[] abilityNames = new String[3];
        for(int i = 0; i < this.cutterAbilities.size(); i++) {
            abilityNames[i] = this.cutterAbilities.get(i).getAbilityName();
        }
        return abilityNames;
    }

    public int[] getCounts() {
        int[] counts = new int[3];
        for(int i = 0; i < this.cutterAbilities.size(); i++) {
            counts[i] = this.cutterAbilities.get(i).getCount();
        }
        return counts;
    }

    public boolean[][] getResults() {
        boolean[][] results = new boolean[3][9];
        for(int i = 0; i < this.cutterAbilities.size(); i++) {
            results[i] = this.cutterAbilities.get(i).getResults();
        }
        return results;
    }

    public int getPercent() {
        return this.percent;
    }

    public boolean isCut() {
        for(CutterAbility cutterAbility : this.cutterAbilities) {
            if(!cutterAbility.isCut()) {
                return false;
            }
        }
        return true;
    }

    private void cutCutterItem() {
        ItemMeta itemMeta = this.cutterItem.getItemMeta();
        TextComponent displayName = (TextComponent) itemMeta.displayName();
        List<Component> lore = itemMeta.lore();

        if(displayName == null || lore == null) {
            return;
        }

        displayName = displayName.content(displayName.content().replace("세공되지 않은", "세공 된"));

        for(int line = 0; line < this.cutterAbilities.size(); line++) {
            CutterAbility cutterAbility = this.cutterAbilities.get(line);
            int ability = cutterAbility.getAbility();

            lore.set(cutterAbility.getIndex(), Component.text(
                    (line == 2 ? ChatColor.RED : ChatColor.WHITE) +
                            cutterAbility.getAbilityName() +
                            (ability > 0 ? " + " + ability : " - " + (-ability))
            ));
        }

        itemMeta.displayName(displayName);
        itemMeta.lore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        this.cutterItem.setItemMeta(itemMeta);
    }
}
