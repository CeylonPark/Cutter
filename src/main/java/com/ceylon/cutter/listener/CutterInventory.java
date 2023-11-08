package com.ceylon.cutter.listener;

import com.ceylon.cutter.api.CommandConstructor;
import com.ceylon.cutter.data.CutterItem;
import com.ceylon.cutter.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class CutterInventory extends CommandConstructor implements Listener {
    private static final int CUTTER_SLOT = 4;
    private static final int[] CUTTER_FRAME = {
            0, 1, 2, 3, 5, 6, 7, 8,
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            36, 37, 38, 39, 40, 41, 42, 43, 44
    };
    private static final int[][] CUTTER_LINE = {
            {18, 19, 20, 21, 22, 23, 24, 25, 26},
            {27, 28, 29, 30, 31, 32, 33, 34, 35},
            {45, 46, 47, 48, 49, 50, 51, 52, 53}
    };

    private final Plugin plugin;
    private final Map<UUID, CutterItem> cutting = new HashMap<>();

    public CutterInventory(Plugin plugin, String command) {
        super(plugin, command);
        this.plugin = plugin;
    }

    private void openCutterInventory(Player player) {
        // Cutter Inventory Open
        Inventory inv = Bukkit.createInventory(player, 54, Component.text("세공"));
        // frame
        for (int slot : CutterInventory.CUTTER_FRAME) {
            ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            inv.setItem(slot, itemStack);
        }
        // 텍스쳐팩 전용 기능
        //inv.setItem(8, new ItemBuilder(Material.QUARTZ).setCustomModelData(7).build());

        if (this.cutting.containsKey(player.getUniqueId())) {
            this.refreshCutterAll(inv, this.cutting.get(player.getUniqueId()));
        }
        player.openInventory(inv);
    }

    @EventHandler
    public void onNpcClick(PlayerInteractEntityEvent event) {
        if(event.getHand() != EquipmentSlot.HAND || event.getRightClicked().getType() != EntityType.PLAYER) {
            return;
        }
        if(!event.getRightClicked().getName().contains("대장장이")) {
            return;
        }
        // Cutter Inventory Open
        this.openCutterInventory(event.getPlayer());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().title().equals(Component.text("세공"))) {
            return;
        }

        // 바깥 인벤토리 클릭 이벤트 취소
        Inventory inv = event.getClickedInventory();
        if (inv == null) {
            return;
        }

        // 플레이어 인벤토리에서 세공 인벤토리로 shift + 클릭 이벤트 취소
        InventoryAction action = event.getAction();
        if (inv.getType() == InventoryType.PLAYER && (action == InventoryAction.MOVE_TO_OTHER_INVENTORY || action == InventoryAction.COLLECT_TO_CURSOR)) {
            event.setCancelled(true);
            return;
        }

        // 세공 인벤토리만 처리
        if (inv.getType() != InventoryType.CHEST) {
            return;
        }

        // 세공 아이템 슬릇을 제외한 다른 슬릇 이벤트 취소
        int slot = event.getRawSlot();
        if (slot != CutterInventory.CUTTER_SLOT) {
            event.setCancelled(true);
        }

        UUID uuid = event.getWhoClicked().getUniqueId();
        CutterItem cutterItem = this.cutting.get(uuid);

        // 세공 아이템
        if (slot == CutterInventory.CUTTER_SLOT) {
            if (action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE || action == InventoryAction.SWAP_WITH_CURSOR) {
                // 같은 아이템 겹침 방지
                if (action == InventoryAction.PLACE_ONE && event.getCurrentItem() != null) {
                    event.setCancelled(true);
                    return;
                }

                // 세공 중 상태 확인
                if (cutterItem != null && cutterItem.isCutting()) {
                    event.setCancelled(true);
                    return;
                }

                // 커팅 아이템 확인, 맞다면 등록
                CutterItem newCutterItem = new CutterItem(event.getCursor());
                if (newCutterItem.isCutterItem()) {
                    this.cutting.put(uuid, newCutterItem);
                    this.refreshCutter(inv, newCutterItem);
                } else {
                    event.setCancelled(true);
                }
                return;
            } else if (action == InventoryAction.PICKUP_ALL || action == InventoryAction.PICKUP_HALF || action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                // 세공 중 상태 확인
                if (cutterItem == null || !cutterItem.isCutting()) {
                    this.cutting.remove(uuid);
                    this.refreshCutter(inv, null);
                    return;
                }
                event.setCancelled(true);
            } else {
                event.setCancelled(true);
            }
            return;
        }

        if (cutterItem == null) {
            return;
        }

        int line;
        if (CutterInventory.CUTTER_LINE[0][0] <= slot && slot <= CutterInventory.CUTTER_LINE[0][8]) {
            line = 0;
        } else if (CutterInventory.CUTTER_LINE[1][0] <= slot && slot <= CutterInventory.CUTTER_LINE[1][8]) {
            line = 1;
        } else if (CutterInventory.CUTTER_LINE[2][0] <= slot && slot <= CutterInventory.CUTTER_LINE[2][8]) {
            line = 2;
        } else {
            return;
        }

        int order = slot - CutterInventory.CUTTER_LINE[line][0];
        if (cutterItem.doCutting(line, order, (Player) event.getWhoClicked())) {
            refreshCutter(inv, cutterItem);
        }
        ;
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!event.getView().title().equals(Component.text("세공"))) {
            return;
        }

        Map<Integer, ItemStack> items = event.getNewItems();
        for (int i : items.keySet()) {
            if (i <= 54 && i != CutterInventory.CUTTER_SLOT) {
                event.setCancelled(true);
                return;
            }
        }

        if (!items.containsKey(CutterInventory.CUTTER_SLOT)) {
            return;
        }

        // 같은 아이템 겹침 방지
        ItemStack itemStack = items.get(CutterInventory.CUTTER_SLOT);
        if (itemStack.getAmount() != 1) {
            event.setCancelled(true);
            return;
        }

        UUID uuid = event.getWhoClicked().getUniqueId();
        CutterItem cutterItem = this.cutting.get(uuid);

        // 세공 중 상태 확인
        if (cutterItem != null && cutterItem.isCutting()) {
            event.setCancelled(true);
            return;
        }

        // 커팅 아이템 확인, 맞다면 등록
        CutterItem newCutterItem = new CutterItem(itemStack);
        if (newCutterItem.isCutterItem()) {
            this.cutting.put(uuid, newCutterItem);
            this.refreshCutter(event.getInventory(), newCutterItem);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!event.getView().title().equals(Component.text("세공"))) {
            return;
        }
        Player player = (Player) event.getPlayer();
        if (this.cutting.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> player.openInventory(event.getInventory()), 1);
        }
    }

    private void refreshCutterAll(Inventory inv, CutterItem cutterItem) {
        for (int line = 0; line < 3; line++) {
            for (int slot = 0; slot < 9; slot++) {
                inv.setItem(CutterInventory.CUTTER_LINE[line][slot], null);
            }
        }
        if (cutterItem == null) {
            return;
        }
        String[] abilityNames = cutterItem.getAbilityNames();
        int[] counts = cutterItem.getCounts();
        boolean[][] results = cutterItem.getResults();
        int percent = cutterItem.getPercent();

        for (int line = 0; line < 3; line++) {
            int count = counts[line];
            for (int i = 0; i <= count; i++) {
                if (i != 0) {
                    this.setStoneItem(inv, line, abilityNames[line], i - 1, results[line][i - 1]);
                }
            }
            if (count != 9) {
                this.setButtonItem(inv, line, count, percent);
            }
        }

        inv.setItem(CutterInventory.CUTTER_SLOT, cutterItem.getCutterItem());
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for(HumanEntity humanEntity : inv.getViewers()) {
                ((Player) humanEntity).updateInventory();
            }
        }, 1);
    }

    private void refreshCutter(Inventory inv, CutterItem cutterItem) {
        if(cutterItem == null) {
            for (int line = 0; line < 3; line++) {
                for(int slot = 0; slot < 9; slot++) {
                    inv.setItem(CutterInventory.CUTTER_LINE[line][slot], null);
                }
            }
        } else {
            String[] abilityNames = cutterItem.getAbilityNames();
            int[] counts = cutterItem.getCounts();
            boolean[][] results = cutterItem.getResults();
            int percent = cutterItem.getPercent();

            for (int line = 0; line < 3; line++) {
                int count = counts[line];
                if (count != 0) {
                    this.setStoneItem(inv, line, abilityNames[line], count - 1, results[line][count - 1]);
                }
                if (count != 9) {
                    this.setButtonItem(inv, line, count, percent);
                }
            }

            if(cutterItem.isCut()) {
                inv.setItem(CutterInventory.CUTTER_SLOT, cutterItem.getCutterItem());
            }
        }
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for(HumanEntity humanEntity : inv.getViewers()) {
                ((Player) humanEntity).updateInventory();
            }
        }, 1);
    }

    private void setButtonItem(Inventory inv, int line, int count, int percent) {
        inv.setItem(CutterInventory.CUTTER_LINE[line][count], new ItemBuilder(Material.NETHER_BRICKS).displayName(Component.text(ChatColor.WHITE + (percent + "%"))).build());
    }

    private void setStoneItem(Inventory inv, int line, String abilityName, int count, boolean result) {
        if(result) {
            if(line == 2) {
                inv.setItem(CutterInventory.CUTTER_LINE[line][count], new ItemBuilder(Material.BLAZE_POWDER).displayName(ChatColor.WHITE + abilityName).build());
            } else {
                inv.setItem(CutterInventory.CUTTER_LINE[line][count], new ItemBuilder(Material.BLUE_DYE).displayName(ChatColor.WHITE + abilityName).build());
            }
        } else {
            inv.setItem(CutterInventory.CUTTER_LINE[line][count], new ItemBuilder(Material.BROWN_DYE).displayName(ChatColor.WHITE + abilityName).build());
        }
    }

    @Override
    public boolean onCommandEmpty(CommandSender sender, Command command, String label) {
        if (sender instanceof Player) {
            this.openCutterInventory((Player) sender);
        }
        return true;
    }

    @Override
    public boolean onBeforeCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public boolean onAfterCommand(CommandSender sender, Command command, String label, String[] args, boolean sub_result) {
        return true;
    }
}
