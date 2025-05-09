package org.Exestudios.exeMode.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.Exestudios.exeMode.ExeMode;
import org.jetbrains.annotations.NotNull;

public class exeunwarn implements CommandExecutor {
    private final ExeMode plugin;

    public exeunwarn(ExeMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("exemode.unwarn")) {
            sender.sendMessage(ChatColor.RED + "Non hai il permesso di eseguire questo comando!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Utilizzo: /exeunwarn <giocatore>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Il giocatore non è online!");
            return true;
        }

        if (plugin.getWarnManager().removeWarn(target.getUniqueId())) {
            int remainingWarns = plugin.getWarnManager().getWarnsCount(target.getUniqueId());
            

            Bukkit.broadcastMessage(ChatColor.GREEN + "Un'ammonizione di " + target.getName() + 
                                  " è stata rimossa da " + sender.getName() +
                                  " (Warn rimanenti: " + remainingWarns + ")");
            

            target.sendMessage(ChatColor.GREEN + "Una tua ammonizione è stata rimossa! Warn rimanenti: " + remainingWarns);
        } else {
            sender.sendMessage(ChatColor.RED + "Il giocatore non ha ammonizioni da rimuovere!");
        }

        return true;
    }
}