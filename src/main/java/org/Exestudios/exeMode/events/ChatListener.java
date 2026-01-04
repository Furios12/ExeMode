package org.Exestudios.exeMode.events;

import org.Exestudios.exeMode.ExeMode;
import org.Exestudios.exeMode.utils.messages;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatListener implements Listener {
    private final ExeMode plugin;

    public ChatListener(ExeMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        boolean muted = false;
        String muteReason = null;
        long expiry = 0L;

        try {
            if (plugin.getMuteManager() != null && plugin.getMuteManager().isMuted(uuid)) {
                muted = true;
            }
        } catch (Throwable ignored) {
        }

        try {
            if (plugin.getTempMuteManager() != null && plugin.getTempMuteManager().isMuted(uuid)) {
                muted = true;
                muteReason = plugin.getTempMuteManager().getReason(uuid);
                expiry = plugin.getTempMuteManager().getExpiry(uuid);
            }
        } catch (Throwable ignored) {
        }

        if (muted) {
            event.setCancelled(true);
            if (expiry > 0) {
                long remaining = expiry - System.currentTimeMillis();
                if (remaining < 0)
                    remaining = 0;
                long hrs = TimeUnit.MILLISECONDS.toHours(remaining);
                long mins = TimeUnit.MILLISECONDS.toMinutes(remaining) - TimeUnit.HOURS.toMinutes(hrs);
                long secs = TimeUnit.MILLISECONDS.toSeconds(remaining)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remaining));
                String timeStr = String.format("%02dh %02dm %02ds", hrs, mins, secs);

                String staffName = "Console";
                try {
                    if (plugin.getTempMuteManager() != null) {
                        staffName = plugin.getTempMuteManager().getStaff(uuid);
                    }
                } catch (Throwable ignored) {
                }

                String msg = messages.exists("tempmute.chat-muted") ? messages.get("tempmute.chat-muted")
                        : (ChatColor.RED + "Sei mutato. Motivo: %reason%");
                player.sendMessage(msg.replace("%reason%", (muteReason == null ? "Nessun motivo" : muteReason))
                        .replace("%time%", timeStr)
                        .replace("%staff%", staffName));

            } else {
                String single = messages.exists("mute.chat-denied") ? messages.get("mute.chat-denied")
                        : (ChatColor.RED + "Sei mutato.");
                player.sendMessage(single);
            }
        }
    }
}