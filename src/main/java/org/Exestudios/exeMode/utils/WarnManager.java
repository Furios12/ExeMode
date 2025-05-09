package org.Exestudios.exeMode.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.Exestudios.exeMode.ExeMode;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class WarnManager {
    private final ExeMode plugin;
    private final File warnFile;
    private FileConfiguration warnConfig;

    public WarnManager(ExeMode plugin) {
        this.plugin = plugin;
        this.warnFile = new File(plugin.getDataFolder(), "warn.yml");
        loadWarnFile();
    }

    private void loadWarnFile() {
        if (!warnFile.exists()) {
            plugin.saveResource("warn.yml", false);
        }
        warnConfig = YamlConfiguration.loadConfiguration(warnFile);
    }

    public void saveWarnFile() {
        try {
            warnConfig.save(warnFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossibile salvare warn.yml: " + e.getMessage());
        }
    }

    public void addWarn(UUID playerUUID, String playerName, String reason, String staffMember) {
        String path = "players." + playerUUID;
        List<String> warns = warnConfig.getStringList(path + ".warns");

        String warnEntry = String.format("Staff: %s, Motivo: %s, Data: %s",
                staffMember,
                reason,
                LocalDateTime.now()
        );

        warns.add(warnEntry);

        warnConfig.set(path + ".name", playerName);
        warnConfig.set(path + ".warns", warns);
        warnConfig.set(path + ".total", warns.size());

        saveWarnFile();
    }

    public boolean removeWarn(UUID playerUUID) {
        String path = "players." + playerUUID;
        List<String> warns = warnConfig.getStringList(path + ".warns");

        if (warns.isEmpty()) {
            return false;
        }

        warns.removeLast();

        if (warns.isEmpty()) {
            warnConfig.set(path, null);
        } else {
            warnConfig.set(path + ".warns", warns);
            warnConfig.set(path + ".total", warns.size());
        }

        saveWarnFile();
        return true;
    }

    public int getWarnsCount(UUID playerUUID) {
        return warnConfig.getInt("players." + playerUUID + ".total", 0);
    }
}