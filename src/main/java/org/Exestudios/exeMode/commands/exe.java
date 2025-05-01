package org.Exestudios.exeMode.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class exe implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage("""
            §6§l=== ExeMode Commands ===
            §e/exe §7- Shows this help message
            §e/exc §7- Switch to creative mode
            §e/exs §7- Switch to survival mode
            §e/exa §7- Switch to adventure mode
            §e/exsp §7- Switch to spectator mode
            §e/exeban §7- Ban a player
            §e/exeunban §7- unban a player
            §e/exekick §7- kick a player(Not implemented yet)
            §6§l====================
            """);
        return true;
    }
}