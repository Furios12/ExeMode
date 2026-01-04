package org.Exestudios.exeMode.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.Exestudios.exeMode.ExeMode;
import org.Exestudios.exeMode.utils.messages;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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

        @SuppressWarnings("deprecation")
        OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);
        UUID targetUuid = offline.getUniqueId();
        String targetName = (offline.getName() != null) ? offline.getName() : args[0];

        boolean wasUnmuted = false;

        // rimuovi temp mute se presente
        try {
            if (plugin.getTempMuteManager() != null && plugin.getTempMuteManager().isMuted(targetUuid)) {
                plugin.getTempMuteManager().unmute(targetUuid, sender.getName());
                wasUnmuted = true;
            }
        } catch (Throwable ignored) {
        }

        // rimuovi mute normale se presente
        try {
            if (plugin.getMuteManager() != null && plugin.getMuteManager().isMuted(targetUuid)) {
                plugin.getMuteManager().unmutePlayer(targetUuid, targetName, sender.getName());
                wasUnmuted = true;
            }
        } catch (Throwable ignored) {
        }

        if (!wasUnmuted) {
            sender.sendMessage(messages.get("unmute.not-muted"));
            return true;
        }

        sender.sendMessage(messages.get("unmute.success-sender").replace("%player%", targetName));
        Player online = Bukkit.getPlayer(targetUuid);
        if (online != null && online.isOnline()) {
            online.sendMessage(messages.get("unmute.success-target"));
        }
        return true;
    }
}