package com.ceylon.cutter.command;

import com.ceylon.cutter.CutterAddition;
import com.ceylon.cutter.api.CommandConstructor;
import com.ceylon.cutter.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class CutterCommand extends CommandConstructor {
    public CutterCommand(Plugin plugin, String command) {
        super(plugin, command);
    }

    @Override
    public boolean onCommandEmpty(CommandSender sender, Command command, String label) {
        sender.sendMessage("Usage: /세공설정 <player> <var>");
        return true;
    }

    @Override
    public boolean onBeforeCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 2) {
            sender.sendMessage("Usage: /세공설정 <player> <integer>");
            return false;
        }

        try {
            CutterAddition.getInstance().setCutterAddition(args[0], Integer.parseInt(args[1]));
        } catch (NumberFormatException e) {
            sender.sendMessage("Usage: /세공설정 <player> <integer>");
            return false;
        }

        sender.sendMessage("[ 세공 ] "+args[0]+" 의 추가확률이 "+args[1]+"로 설정되었습니다.");
        return false;
    }

    @Override
    public boolean onAfterCommand(CommandSender sender, Command command, String label, String[] args, boolean sub_result) {
        return true;
    }
}
