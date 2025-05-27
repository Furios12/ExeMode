package org.Exestudios.exeMode.commands;

import org.Exestudios.exeMode.ExeMode;
import org.Exestudios.exeMode.utils.messages;
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

public class exeunban implements CommandExecutor {
    private final ExeMode plugin;

    public exeunban(ExeMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("exemode.unban")) {
            sender.sendMessage(messages.get("unban.no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(messages.get("unban.usage"));
            return true;
        }

        String targetPlayer = args[0];
        File banFile = new File(plugin.getDataFolder(), "bans.yml");

        if (!banFile.exists()) {
            sender.sendMessage(messages.get("unban.not-banned"));
            return true;
        }

        FileConfiguration banConfig = YamlConfiguration.loadConfiguration(banFile);

        if (!banConfig.getBoolean(targetPlayer + ".banned", false)) {
            sender.sendMessage(messages.get("unban.not-banned"));
            return true;
        }


        String originalReason = banConfig.getString(targetPlayer + ".reason", "Non specificato");
        String originalBanner = banConfig.getString(targetPlayer + ".banner", "Sconosciuto");

        banConfig.set(targetPlayer + ".banned", false);

        try {
            banConfig.save(banFile);


            if (plugin.getDiscordWebhook() != null) {
                String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
                String description = String.format("""
                    **Giocatore:** %s
                    **Staff che rimuove:** %s
                    **Ban originale da:** %s
                    **Motivo originale:** %s
                    **Data unban:** %s""",
                    targetPlayer,
                    sender.getName(),
                    originalBanner,
                    originalReason,
                    currentDate);
                plugin.getDiscordWebhook().sendWebhook("🔓 Ban Rimosso", description, 5763719); // Verde
            }

            sender.sendMessage(messages.get("unban.success")
                    .replace("%player%", targetPlayer)
                    .replace("%unbanner%", sender.getName()));
            
            String unbanMessage = messages.get("unban.broadcast")
                    .replace("%player%", targetPlayer)
                    .replace("%unbanner%", sender.getName());

            plugin.getServer().broadcastMessage(unbanMessage);

        } catch (IOException e) {
            sender.sendMessage(messages.get("unban.error"));
            plugin.getLogger().severe("Errore durante il salvataggio del file bans.yml: " + e.getMessage());
            return true;
        }

        return true;
    }
}