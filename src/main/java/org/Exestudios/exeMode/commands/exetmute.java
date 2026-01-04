package org.Exestudios.exeMode.commands;

import org.Exestudios.exeMode.ExeMode;
import org.Exestudios.exeMode.utils.TimeUtil;
import org.Exestudios.exeMode.utils.TempMuteManager;
import org.Exestudios.exeMode.utils.messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class exetmute implements CommandExecutor {
    private final ExeMode plugin;

    public exetmute(ExeMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!sender.hasPermission("exemode.tempmute")) {
            sender.sendMessage(messages.get("tempmute.no-permission"));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(messages.get("tempmute.usage"));
            return true;
        }
        String targetName = args[0];
        String timeStr = args[1];
        String reason = args.length >= 3 ? String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length))
                : "Nessun motivo specificato";
        long dur = TimeUtil.parseDurationMillis(timeStr);
        if (dur <= 0) {
            sender.sendMessage(messages.get("tempmute.usage"));
            return true;
        }
        Player target = Bukkit.getPlayer(targetName);
        @SuppressWarnings("deprecation")
        java.util.UUID targetUUID = (target != null) ? target.getUniqueId()
                : Bukkit.getOfflinePlayer(targetName).getUniqueId();
        TempMuteManager mm = plugin.getTempMuteManager();
        mm.tempMute(targetUUID, targetName, dur, reason, sender.getName());

        sender.sendMessage(messages.get("tempmute.success-sender")
                .replace("%player%", targetName)
                .replace("%time%", timeStr)
                .replace("%reason%", reason));

        if (target != null && target.isOnline()) {
            target.sendMessage(messages.get("tempmute.success-target")
                    .replace("%player%", targetName)
                    .replace("%time%", timeStr)
                    .replace("%reason%", reason)
                    .replace("%staff%", sender.getName()));
        }
        return true;
    }
}