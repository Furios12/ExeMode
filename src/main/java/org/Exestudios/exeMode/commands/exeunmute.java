package org.Exestudios.exeMode.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.Exestudios.exeMode.ExeMode;
import org.Exestudios.exeMode.utils.messages;
import org.jetbrains.annotations.NotNull;

public class exeunmute implements CommandExecutor {
    private final ExeMode plugin;

    public exeunmute(ExeMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("exemode.unmute")) {
            sender.sendMessage(messages.get("unmute.no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(messages.get("unmute.usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(messages.get("unmute.player-not-found"));
            return true;
        }

        if (!plugin.getMuteManager().isMuted(target.getUniqueId())) {
            sender.sendMessage(messages.get("unmute.not-muted"));
            return true;
        }

        plugin.getMuteManager().unmutePlayer(
            target.getUniqueId(),
            target.getName(),
            sender.getName()
        );

        sender.sendMessage(messages.get("unmute.success-sender").replace("%player%", target.getName()));
        target.sendMessage(messages.get("unmute.success-target"));

        return true;
    }
}