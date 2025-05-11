package org.Exestudios.exeMode.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.Exestudios.exeMode.ExeMode;

public class ChatListener implements Listener {
    private final ExeMode plugin;

    public ChatListener(ExeMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (plugin.getMuteManager().isMuted(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            String reason = plugin.getMuteManager().getMuteReason(event.getPlayer().getUniqueId());
            event.getPlayer().sendMessage(ChatColor.RED + "Non puoi scrivere mentre sei mutato!" + 
                    (reason != null ? ChatColor.YELLOW + "\nMotivo: " + reason : ""));
        }
    }
}