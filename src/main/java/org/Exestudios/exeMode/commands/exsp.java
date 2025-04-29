package org.Exestudios.exeMode.commands;

import org.Exestudios.exeMode.utils.messages;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class exsp implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.get("no-player"));
            return true;
        }

        if (!player.hasPermission("exemode.spectator")) {
            sender.sendMessage(messages.get("no-permission"));
            return true;
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(messages.get("gamemode-spectator"));
        return true;
    }
}