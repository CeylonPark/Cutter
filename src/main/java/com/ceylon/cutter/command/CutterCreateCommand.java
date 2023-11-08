package com.ceylon.cutter.command;

import com.ceylon.cutter.api.CommandConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class CutterCreateCommand extends CommandConstructor {
    public CutterCreateCommand(Plugin plugin, String command) {
        super(plugin, command);
    }

    @Override
    public boolean onCommandEmpty(CommandSender sender, Command command, String label) {
        sender.sendMessage("Usage: /돌제작 <이름> <능력치1> <능력치2> <능력치3> <1의 배수> <2의 배수> <3의 배수> <설명>");
        sender.sendMessage("Usage: 손에 아이템을 들고 있어야합니다.");
        sender.sendMessage("Usage: 띄어씨기는 \"_\" 사용");
        return true;
    }

    @Override
    public boolean onBeforeCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 8) {
            sender.sendMessage("Usage: /돌제작 <이름> <능력치1> <능력치2> <능력치3> <1의 배수> <2의 배수> <3의 배수> <설명>");
            sender.sendMessage("Usage: 손에 아이템을 들고 있어야합니다.");
            sender.sendMessage("Usage: 띄어씨기는 \"_\" 사용");
            return false;
        }

        Player player = ((Player) sender);
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(
                Component.text("세공되지 않은 "+args[0].replace("_", " "))
                        .color(TextColor.fromCSSHexString("#ffffff"))
                        .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.WHITE+args[7].replace("_", " ").replace("&", "§")));
        lore.add(Component.text(ChatColor.DARK_GRAY+"--------------------------------"));
        lore.add(Component.text(ChatColor.WHITE+args[1].replace("_", " ")+" X "+args[4]));
        lore.add(Component.text(ChatColor.WHITE+args[2].replace("_", " ")+" X "+args[5]));
        lore.add(Component.text(""));
        lore.add(Component.text(ChatColor.RED+args[3].replace("_", " ")+" X "+args[6]));
        lore.add(Component.text(ChatColor.DARK_GRAY+"--------------------------------"));

        itemMeta.lore(lore);
        itemStack.setItemMeta(itemMeta);

        sender.sendMessage("[ 세공 ] 설정되었습니다.");
        return false;
    }

    @Override
    public boolean onAfterCommand(CommandSender sender, Command command, String label, String[] args, boolean sub_result) {
        return true;
    }
}
