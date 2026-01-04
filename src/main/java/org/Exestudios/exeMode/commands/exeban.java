package org.Exestudios.exeMode.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.Exestudios.exeMode.ExeMode;
import org.Exestudios.exeMode.utils.messages;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class exeban implements CommandExecutor {
    private final ExeMode plugin;

    public exeban(ExeMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("exemode.ban")) {
            sender.sendMessage(messages.get("ban.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(messages.get("ban.usage"));
            return true;
        }

        String playerName = args[0];
        Player target = Bukkit.getPlayer(playerName);

        StringBuilder reason = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }
        String banReason = reason.toString().trim();
        String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

        File banFile = new File(plugin.getDataFolder(), "bans.yml");
        FileConfiguration banConfig = YamlConfiguration.loadConfiguration(banFile);

        banConfig.set(playerName + ".banned", true);
        banConfig.set(playerName + ".reason", banReason);
        banConfig.set(playerName + ".banner", sender.getName());
        banConfig.set(playerName + ".date", new Date().getTime());

        try {
            banConfig.save(banFile);
            

            if (plugin.getDiscordWebhook() != null) {
                String description = String.format("""
                    **Giocatore:** %s
                    **Staff:** %s
                    **Motivo:** %s
                    **Data:** %s""",
                    playerName, 
                    sender.getName(), 
                    banReason,
                    currentDate);
                plugin.getDiscordWebhook().sendWebhook("🔨 Ban Eseguito", description, 15158332);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Unable to save bans.yml file for player " + playerName, e);
            sender.sendMessage(messages.get("ban.error"));
            return true;
        }

        if (target != null) {
            String banMessage = messages.get("ban.message")
                    .replace("%banner%", sender.getName())
                    .replace("%reason%", banReason)
                    .replace("%date%", currentDate);
            target.kickPlayer(banMessage);
        }

        Bukkit.broadcastMessage(messages.get("ban.broadcast")
                .replace("%player%", playerName)
                .replace("%banner%", sender.getName()));
        Bukkit.broadcastMessage(messages.get("ban.broadcast-reason")
                .replace("%reason%", banReason));

        return true;
    }
}