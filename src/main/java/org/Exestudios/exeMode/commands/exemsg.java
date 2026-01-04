package org.Exestudios.exeMode.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.Exestudios.exeMode.utils.messages;
import org.jetbrains.annotations.NotNull;

public class exemsg implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("exeMode.msg")) {
            sender.sendMessage(messages.get("msg.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(messages.get("msg.usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(messages.get("msg.player-not-found"));
            return true;
        }

        if (sender instanceof Player && target.getUniqueId().equals(((Player) sender).getUniqueId())) {
            sender.sendMessage(messages.get("msg.cannot-self"));
            return true;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }
        String finalMessage = message.toString().trim();

        String senderName = sender instanceof Player ? ((Player) sender).getDisplayName() : "Console";

        sender.sendMessage(messages.get("msg.sent")
                .replace("%target%", target.getDisplayName())
                .replace("%message%", finalMessage));

        target.sendMessage(messages.get("msg.received")
                .replace("%sender%", senderName)
                .replace("%message%", finalMessage));

        return true;
    }
}