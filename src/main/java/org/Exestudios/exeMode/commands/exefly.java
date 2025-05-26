package org.Exestudios.exeMode.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.Exestudios.exeMode.ExeMode;
import org.jetbrains.annotations.NotNull;

public class exefly implements CommandExecutor {
    
    private final ExeMode plugin;
    
    public exefly(ExeMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Questo comando può essere eseguito solo da un giocatore!");
            return true;
        }

        if (!player.hasPermission("exemode.fly")) {
            player.sendMessage(ChatColor.RED + "Non hai il permesso di utilizzare questo comando!");
            return true;
        }

        if (args.length == 0) {
            toggleFly(player);
        } else if (args.length == 1 && player.hasPermission("exemode.fly.others")) {
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Giocatore non trovato!");
                return true;
            }
            toggleFly(target);
            player.sendMessage(ChatColor.GREEN + "Hai " + (target.getAllowFlight() ? "attivato" : "disattivato") + 
                             " la modalità volo per " + target.getName());
        }

        return true;
    }

    private void toggleFly(@NotNull Player player) {
        boolean newFlyState = !player.getAllowFlight();
        player.setAllowFlight(newFlyState);
        player.setFlying(newFlyState);
        player.sendMessage(ChatColor.GREEN + "Modalità volo " + 
                         (newFlyState ? "attivata" : "disattivata") + "!");
    }
}