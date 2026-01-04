package org.Exestudios.exeMode.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.Exestudios.exeMode.utils.messages;
import org.Exestudios.exeMode.ExeMode;
import org.jetbrains.annotations.NotNull;

public class exewarn implements CommandExecutor {
    private final ExeMode plugin;

    public exewarn(ExeMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("exemode.warn")) {
            sender.sendMessage(messages.get("warn.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(messages.get("warn.usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(messages.get("warn.player-not-found"));
            return true;
        }

        StringBuilder reason = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }
        String reasonStr = reason.toString().trim();

        plugin.getWarnManager().addWarn(
            target.getUniqueId(),
            target.getName(),
            reasonStr,
            sender.getName()
        );

        int warnCount = plugin.getWarnManager().getWarnsCount(target.getUniqueId());

        // Messaggio al giocatore ammonito
        target.sendMessage(messages.get("warn.message")
                .replace("%warner%", sender.getName())
                .replace("%reason%", reasonStr)
                .replace("%count%", String.valueOf(warnCount))
        );

        // Messaggio al mittente (conferma)
        sender.sendMessage(messages.get("warn.success")
                .replace("%player%", target.getName())
                .replace("%count%", String.valueOf(warnCount))
        );

        // Broadcast pubblico
        Bukkit.broadcastMessage(messages.get("warn.broadcast")
                .replace("%player%", target.getName())
                .replace("%warner%", sender.getName())
                .replace("%count%", String.valueOf(warnCount))
        );
        Bukkit.broadcastMessage(messages.get("warn.broadcast-reason")
                .replace("%reason%", reasonStr)
                .replace("%count%", String.valueOf(warnCount))
        );

        return true;
    }
}