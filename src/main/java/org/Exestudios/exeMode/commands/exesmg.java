package org.Exestudios.exeMode.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class exesmg implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("exeMode.msg")) {
            sender.sendMessage(ChatColor.RED + "Non hai il permesso di utilizzare questo comando!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilizzo: /exesmg <giocatore> <messaggio>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Il giocatore non è online!");
            return true;
        }


        if (sender instanceof Player && target.getUniqueId().equals(((Player) sender).getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Non puoi mandare messaggi a te stesso!");
            return true;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }
        String finalMessage = message.toString().trim();


        String senderName = sender instanceof Player ? ((Player) sender).getDisplayName() : "Console";
        

        sender.sendMessage(ChatColor.GRAY + "[Io -> " + target.getDisplayName() + "] " + ChatColor.WHITE + finalMessage);
        

        target.sendMessage(ChatColor.GRAY + "[" + senderName + " -> Io] " + ChatColor.WHITE + finalMessage);

        return true;
    }
}