package org.Exestudios.exeMode.commands;

import org.Exestudios.exeMode.utils.HomeManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.Exestudios.exeMode.utils.messages;

public class sethome implements CommandExecutor {
    private final HomeManager homeManager;

    public sethome(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.get("no-player"));
            return true;
        }
        homeManager.setHome(player);
        player.sendMessage(messages.get("home-set"));
        return true;
    }
}