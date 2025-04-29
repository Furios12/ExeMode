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

public class exeunban implements CommandExecutor {
    private final ExeMode plugin;

    public exeunban(ExeMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Controllo permessi
        if (!sender.hasPermission("exemode.unban")) {
            sender.sendMessage(messages.get("unban.no-permission"));
            return true;
        }

        // Controllo argomenti
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

        // Controlla se il giocatore è effettivamente bannato
        if (!banConfig.getBoolean(targetPlayer + ".banned", false)) {
            sender.sendMessage(messages.get("unban.not-banned"));
            return true;
        }

        // Rimuove il ban
        banConfig.set(targetPlayer + ".banned", false);

        try {
            banConfig.save(banFile);

            // Invia il messaggio di successo
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