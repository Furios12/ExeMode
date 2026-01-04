package org.Exestudios.exeMode.utils;

import org.Exestudios.exeMode.ExeMode;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TempBanManager {
    private final ExeMode plugin;
    private final File file;
    private FileConfiguration config;
    private final Map<UUID, Long> expiry = new HashMap<>();
    private final Map<UUID, String> reason = new HashMap<>();
    private final Map<UUID, String> staff = new HashMap<>();

    public TempBanManager(ExeMode plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "tempbans.yml");
        if (!file.exists()) {
            try {
                if (file.getParentFile() != null)
                    file.getParentFile().mkdirs();
                if (!file.createNewFile()) {
                    plugin.getLogger().warning("Unable to create tempbans.yml file.");
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Error creating tempbans.yml: " + e.getMessage());
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        load();
    }

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
        // clear existing
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
                plugin.getLogger().severe("Unable to save tempbans: " + ex.getMessage());
            }
        }
    }

    public boolean isBanned(UUID u) {
        if (!expiry.containsKey(u))
            return false;
        long exp = expiry.get(u);
        if (System.currentTimeMillis() > exp) {
            unban(u, "auto-expire");
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

    public void tempBan(UUID targetUUID, String targetName, long durationMillis, String r, String staffName) {
        long exp = System.currentTimeMillis() + durationMillis;
        expiry.put(targetUUID, exp);
        reason.put(targetUUID, r == null ? "Nessun motivo specificato" : r);
        staff.put(targetUUID, staffName);
        Date until = new Date(exp);
        Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(targetName, reason.get(targetUUID), until, staffName);
        OfflinePlayer op = Bukkit.getOfflinePlayer(targetUUID);
        if (op.isOnline()) {
            org.bukkit.entity.Player p = op.getPlayer();
            if (p != null && p.isOnline()) {
                long hrs = TimeUnit.MILLISECONDS.toHours(durationMillis);
                long mins = TimeUnit.MILLISECONDS.toMinutes(durationMillis) - TimeUnit.HOURS.toMinutes(hrs);
                long secs = TimeUnit.MILLISECONDS.toSeconds(durationMillis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMillis));
                String timeStr = String.format("%02dh %02dm %02ds", hrs, mins, secs);

                String kickMsg = messages.exists("tempban.success-target") ? messages.get("tempban.success-target")
                        : ("Sei stato temporaneamente bannato: %reason%");
                p.kickPlayer(kickMsg.replace("%reason%", reason.get(targetUUID))
                        .replace("%time%", timeStr)
                        .replace("%staff%", staffName)
                        .replace("%player%", targetName));
            }
        }
        save();
        if (plugin.getDiscordWebhook() != null) {
            String desc = String.format("**Giocatore:** %s\n**Staff:** %s\n**Motivo:** %s\n**Scade:** %s",
                    targetName, staffName, reason.get(targetUUID), new Date(exp).toString());
            plugin.getDiscordWebhook().sendWebhook("TempBan", desc, 15158332);
        }
    }

    public boolean unban(UUID u, String staffName) {
        if (!expiry.containsKey(u))
            return false;
        expiry.remove(u);
        reason.remove(u);
        staff.remove(u);
        save();
        return true;
    }
}