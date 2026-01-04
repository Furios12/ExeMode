package org.Exestudios.exeMode.commands;

import org.Exestudios.exeMode.ExeMode;
import org.Exestudios.exeMode.utils.messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class exeunban implements CommandExecutor {
    private final ExeMode plugin;

    public exeunban(ExeMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!sender.hasPermission("exemode.unban")) {
            sender.sendMessage(messages.get("unban.no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(messages.get("unban.usage"));
            return true;
        }

        String targetPlayerName = args[0];
        @SuppressWarnings("deprecation")
        OfflinePlayer targetOp = Bukkit.getOfflinePlayer(targetPlayerName);
        UUID targetUUID = targetOp.getUniqueId();

        boolean unbannedAny = false;

        // Check TempBan
        if (plugin.getTempBanManager().isBanned(targetUUID)) {
            plugin.getTempBanManager().unban(targetUUID, sender.getName());
            unbannedAny = true;
        }

        // Check PermBan (bans.yml)
        File banFile = new File(plugin.getDataFolder(), "bans.yml");
        if (banFile.exists()) {
            FileConfiguration banConfig = YamlConfiguration.loadConfiguration(banFile);
            if (banConfig.getBoolean(targetPlayerName + ".banned", false)) {
                banConfig.set(targetPlayerName + ".banned", false);
                try {
                    banConfig.save(banFile);
                    unbannedAny = true;
                } catch (IOException e) {
                    plugin.getLogger().severe("Errore durante il salvataggio del file bans.yml: " + e.getMessage());
                }
            }
        }

        // Check Bukkit Ban
        if (Bukkit.getBanList(org.bukkit.BanList.Type.NAME).isBanned(targetPlayerName)) {
            Bukkit.getBanList(org.bukkit.BanList.Type.NAME).pardon(targetPlayerName);
            unbannedAny = true;
        }

        if (!unbannedAny) {
            sender.sendMessage(messages.get("unban.not-banned"));
            return true;
        }

        // Send confirmation
        sender.sendMessage(messages.get("unban.success")
                .replace("%player%", targetPlayerName)
                .replace("%unbanner%", sender.getName()));

        String broadcastMsg = messages.get("unban.broadcast")
                .replace("%player%", targetPlayerName)
                .replace("%unbanner%", sender.getName());
        plugin.getServer().broadcastMessage(broadcastMsg);

        // Webhook
        if (plugin.getDiscordWebhook() != null) {
            String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
            String description = String.format("**Giocatore:** %s\n**Staff che rimuove:** %s\n**Data:** %s",
                    targetPlayerName, sender.getName(), currentDate);
            plugin.getDiscordWebhook().sendWebhook("🔓 Ban Rimosso", description, 5763719);
        }

        return true;
    }
}