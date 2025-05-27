package org.Exestudios.exeMode.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.Exestudios.exeMode.ExeMode;
import org.jetbrains.annotations.NotNull;

public class exemute implements CommandExecutor {
    private final ExeMode plugin;

    public exemute(ExeMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Utilizzo: /exemute <giocatore> [motivo]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Giocatore non trovato!");
            return true;
        }

        if (plugin.getMuteManager().isMuted(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Questo giocatore è già mutato!");
            return true;
        }


        String reason = null;
        if (args.length > 1) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                reasonBuilder.append(args[i]).append(" ");
            }
            reason = reasonBuilder.toString().trim();
        }


        plugin.getMuteManager().mutePlayer(
            target.getUniqueId(),
            reason,
            sender.getName(),
            target.getName()
        );


        sender.sendMessage(ChatColor.GREEN + "Hai mutato " + target.getName() + 
                (reason != null ? ChatColor.YELLOW + " per: " + reason : ""));


        target.sendMessage(ChatColor.RED + "Sei stato mutato nella chat" + 
                (reason != null ? ChatColor.YELLOW + "\nMotivo: " + reason : ""));

        return true;
    }
}