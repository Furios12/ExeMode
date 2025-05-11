package org.Exestudios.exeMode.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.Exestudios.exeMode.ExeMode;
import org.jetbrains.annotations.NotNull;

public class exeunmute implements CommandExecutor {
    private final ExeMode plugin;

    public exeunmute(ExeMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§c§lUso: /unmute <giocatore>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§c§lGiocatore non trovato!");
            return true;
        }

        if (!plugin.getMuteManager().isMuted(target.getUniqueId())) {
            sender.sendMessage("§c§lQuesto giocatore non è mutato!");
            return true;
        }

        plugin.getMuteManager().unmutePlayer(target.getUniqueId());
        
        sender.sendMessage("§a§lHai smutato " + target.getName());
        target.sendMessage("§a§lNon sei più mutato nella chat");
        
        return true;
    }
}