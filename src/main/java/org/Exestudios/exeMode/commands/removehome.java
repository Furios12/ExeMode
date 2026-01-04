package org.Exestudios.exeMode.commands;

import org.Exestudios.exeMode.utils.HomeManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.Exestudios.exeMode.utils.messages;

public class removehome implements CommandExecutor {
    private final HomeManager homeManager;

    public removehome(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.get("no-player"));
            return true;
        }
        if (!homeManager.hasHome(player)) {
            player.sendMessage(messages.get("no-home"));
            return true;
        }
        homeManager.removeHome(player);
        player.sendMessage(messages.get("home-removed"));
        return true;
    }
}