package org.Exestudios.exeMode.events;

import org.Exestudios.exeMode.ExeMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.Exestudios.exeMode.utils.messages;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerJoinBanCheck implements Listener {
    private final ExeMode plugin;

    public PlayerJoinBanCheck(ExeMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        File banFile = new File(plugin.getDataFolder(), "bans.yml");
        if (!banFile.exists()) {
            plugin.getLogger().warning("bans.yml file not found, skipping ban check.");
            return;
        }

        FileConfiguration banConfig = YamlConfiguration.loadConfiguration(banFile);
        String playerName = event.getPlayer().getName();

        if (banConfig.getBoolean(playerName + ".banned", false)) {
            String banner = banConfig.getString(playerName + ".banner", "Console");
            String reason = banConfig.getString(playerName + ".reason", "Nessun motivo specificato");
            long banDate = banConfig.getLong(playerName + ".date", System.currentTimeMillis());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String formattedDate = dateFormat.format(new Date(banDate));

            String banMessage = messages.get("ban.message")
                    .replace("%banner%", banner)
                    .replace("%reason%", reason)
                    .replace("%date%", formattedDate);

            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, banMessage);
            plugin.getLogger().info("Login attempt blocked for banned player: " + playerName);
        }
    }
}