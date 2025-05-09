package org.Exestudios.exeMode.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
            sender.sendMessage(ChatColor.RED + "Non hai il permesso di eseguire questo comando!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilizzo: /exewarn <giocatore> <motivo>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Il giocatore non è online!");
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


        target.sendMessage(ChatColor.RED + "Sei stato ammonito! Motivo: " + ChatColor.YELLOW + reasonStr);
        target.sendMessage(ChatColor.RED + "Questo è il tuo warn #" + warnCount);


        Bukkit.broadcastMessage(ChatColor.RED + "Il giocatore " + target.getName() + 
                              " è stato ammonito da " + sender.getName() + 
                              " per: " + ChatColor.YELLOW + reasonStr +
                              ChatColor.RED + " (Warn #" + warnCount + ")");

        return true;
    }
}