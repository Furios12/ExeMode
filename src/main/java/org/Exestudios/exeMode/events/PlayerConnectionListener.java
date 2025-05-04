package org.Exestudios.exeMode.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.Exestudios.exeMode.utils.messages;

public class PlayerConnectionListener implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String joinMessage = messages.get("player.join").replace("%player%", event.getPlayer().getName());
        event.setJoinMessage(joinMessage);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String quitMessage = messages.get("player.quit").replace("%player%", event.getPlayer().getName());
        event.setQuitMessage(quitMessage);
    }
}