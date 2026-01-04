package org.Exestudios.exeMode.commands;

import org.Exestudios.exeMode.ExeMode;
import org.Exestudios.exeMode.utils.messages;
import org.Exestudios.exeMode.utils.WarnManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class execlearwarn implements CommandExecutor {
    private final ExeMode plugin;

    public execlearwarn(ExeMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!sender.hasPermission("exemode.clearwarn")) {
            sender.sendMessage(messages.get("clearwarn.no-permission"));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(messages.get("clearwarn.usage"));
            return true;
        }
        String targetName = args[0];
        @SuppressWarnings("deprecation")
        java.util.UUID targetUUID = Bukkit.getOfflinePlayer(targetName).getUniqueId();
        WarnManager wm = plugin.getWarnManager();
        while (wm.removeWarn(targetUUID)) {
        }
        sender.sendMessage(messages.get("clearwarn.success").replace("%player%", targetName));
        return true;
    }
}