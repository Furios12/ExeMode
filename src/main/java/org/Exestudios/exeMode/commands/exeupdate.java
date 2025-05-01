package org.Exestudios.exeMode.commands;

import org.Exestudios.exeMode.ExeMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class exeupdate implements CommandExecutor {
    private final ExeMode plugin;

    public exeupdate(ExeMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("exemode.update")) {
            sender.sendMessage("§cNon hai il permesso di eseguire questo comando!");
            return true;
        }

        boolean force = args.length > 0 && args[0].equalsIgnoreCase("force");
        
        if (sender instanceof Player) {
            plugin.checkUpdates((Player) sender, force);
        } else {
            plugin.checkUpdates(null, force);
        }
        
        return true;
    }
}