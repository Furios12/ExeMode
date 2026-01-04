package org.Exestudios.exeMode.utils;

import org.Exestudios.exeMode.ExeMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TempMuteManager {
    private final ExeMode plugin;
    private final File file;
    private FileConfiguration config;
    private final Map<UUID, Long> expiry = new HashMap<>();
    private final Map<UUID, String> reason = new HashMap<>();

    public TempMuteManager(ExeMode plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "tempmutes.yml");
        if (!file.exists()) {
            try {
                if (file.getParentFile() != null)
                    file.getParentFile().mkdirs();
                if (!file.createNewFile()) {
                    plugin.getLogger().warning("Unable to create tempmutes.yml file.");
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Error creating tempmutes.yml: " + e.getMessage());
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private final Map<UUID, String> staff = new HashMap<>();

    private void load() {
        expiry.clear();
        reason.clear();
        staff.clear();
        if (config == null)
            return;
        for (String key : config.getKeys(false)) {
            try {
                UUID u = UUID.fromString(key);
                long exp = config.getLong(key + ".expiry", 0L);
                String r = config.getString(key + ".reason", "");
                String s = config.getString(key + ".staff", "Console");
                if (exp > 0) {
                    expiry.put(u, exp);
                    reason.put(u, r);
                    staff.put(u, s);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private void save() {
        config = YamlConfiguration.loadConfiguration(file);
        if (config != null) {
            config.getKeys(false).forEach(k -> config.set(k, null));
            for (Map.Entry<UUID, Long> e : expiry.entrySet()) {
                String k = e.getKey().toString();
                config.set(k + ".expiry", e.getValue());
                config.set(k + ".reason", reason.getOrDefault(e.getKey(), ""));
                config.set(k + ".staff", staff.getOrDefault(e.getKey(), "Console"));
            }
            try {
                config.save(file);
            } catch (IOException ex) {
                plugin.getLogger().severe("Unable to save tempmutes: " + ex.getMessage());
            }
        }
    }

    public boolean isMuted(UUID u) {
        if (!expiry.containsKey(u))
            return false;
        long exp = expiry.get(u);
        if (System.currentTimeMillis() > exp) {
            unmute(u, "auto-expire");
            return false;
        }
        return true;
    }

    public String getReason(UUID u) {
        return reason.get(u);
    }

    public String getStaff(UUID u) {
        return staff.getOrDefault(u, "Console");
    }

    public long getExpiry(UUID u) {
        return expiry.getOrDefault(u, 0L);
    }

    public void tempMute(UUID targetUUID, String targetName, long durationMillis, String r, String staffName) {
        long exp = System.currentTimeMillis() + durationMillis;
        expiry.put(targetUUID, exp);
        reason.put(targetUUID, r == null ? "Nessun motivo specificato" : r);
        staff.put(targetUUID, staffName);
        save();
        org.bukkit.entity.Player p = Bukkit.getPlayer(targetUUID);
        if (p != null && p.isOnline()) {
            p.sendMessage(ChatColor.RED + "Sei stato mutato temporaneamente: " + reason.get(targetUUID));
        }
        if (plugin.getDiscordWebhook() != null) {
            String desc = String.format("**Giocatore:** %s\n**Staff:** %s\n**Motivo:** %s\n**Scade:** %s",
                    targetName, staffName, reason.get(targetUUID), new Date(exp).toString());
            plugin.getDiscordWebhook().sendWebhook("TempMute", desc, 15158332);
        }
    }

    public boolean unmute(UUID u, String staffName) {
        if (!expiry.containsKey(u))
            return false;
        expiry.remove(u);
        reason.remove(u);
        staff.remove(u);
        save();
        return true;
    }
}