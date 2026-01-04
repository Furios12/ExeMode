package org.Exestudios.exeMode.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.Exestudios.exeMode.ExeMode;
import org.Exestudios.exeMode.utils.messages;
import org.jetbrains.annotations.NotNull;

public class exekick implements CommandExecutor {
    public exekick(ExeMode plugin) {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!sender.hasPermission("exemode.kick")) {
            sender.sendMessage(messages.get("kick.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(messages.get("kick.usage"));
            return true;
        }

        String playerName = args[0];
        Player target = Bukkit.getPlayer(playerName);

        if (target == null) {
            sender.sendMessage(messages.get("kick.player-not-found"));
            return true;
        }

        StringBuilder reason = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }
        String kickReason = reason.toString().trim();

        String kickMessage = messages.get("kick.message")
                .replace("%kicker%", sender.getName())
                .replace("%reason%", kickReason)
                .replace("%date%", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()));

        target.kickPlayer(kickMessage);

        Bukkit.broadcastMessage(messages.get("kick.broadcast")
                .replace("%player%", playerName)
                .replace("%kicker%", sender.getName()));
        Bukkit.broadcastMessage(messages.get("kick.broadcast-reason")
                .replace("%reason%", kickReason));

        return true;
    }
}