package org.Exestudios.exeMode.commands;

import org.Exestudios.exeMode.ExeMode;
import org.Exestudios.exeMode.utils.StaffSpyManager;
import org.Exestudios.exeMode.utils.messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class exespy implements CommandExecutor {
    private final ExeMode plugin;

    public exespy(ExeMode plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(messages.get("spy.console-not-supported"));
            return true;
        }
        if (!sender.hasPermission("exemode.spy")) {
            sender.sendMessage(messages.get("spy.no-permission"));
            return true;
        }
        StaffSpyManager sm = plugin.getStaffSpyManager();
        java.util.UUID uuid = ((Player) sender).getUniqueId();
        boolean now = sm.toggle(uuid);
        sender.sendMessage((now ? messages.get("spy.toggled-on") : messages.get("spy.toggled-off")));
        return true;
    }
}