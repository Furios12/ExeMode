package org.Exestudios.exeMode.commands;

import org.Exestudios.exeMode.utils.HomeManager;
import org.Exestudios.exeMode.utils.messages;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class home implements CommandExecutor {
    private final HomeManager homeManager;

    public home(HomeManager homeManager) {
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
        Location home = homeManager.getHome(player);
        player.teleport(home);
        player.sendMessage(messages.get("home-teleport"));
        return true;
    }
}