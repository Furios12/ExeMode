package org.Exestudios.exeMode.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.Exestudios.exeMode.ExeMode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MuteManager {
    private final File muteFile;
    private final Map<UUID, String> mutedPlayers; // UUID -> motivo
    private final Logger logger;
    private final ExeMode plugin;

    public MuteManager(ExeMode plugin) {
        this.plugin = plugin;
        this.mutedPlayers = new HashMap<>();
        this.muteFile = new File(plugin.getDataFolder(), "muted_players.yml");
        this.logger = plugin.getLogger();
        loadMutedPlayers();
    }

    private void loadMutedPlayers() {
        if (!muteFile.exists()) {
            try {
                if (!muteFile.createNewFile()) {
                    logger.severe("Impossibile creare il file muted_players.yml");
                    return;
                }
                logger.info("File muted_players.yml creato con successo");
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Errore durante la creazione del file muted_players.yml", e);
                return;
            }
        }
        
        FileConfiguration muteConfig = YamlConfiguration.loadConfiguration(muteFile);
        ConfigurationSection mutedSection = muteConfig.getConfigurationSection("muted-players");
        
        mutedPlayers.clear();
        if (mutedSection != null) {
            for (String uuidStr : mutedSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    String reason = mutedSection.getString(uuidStr + ".reason", "Nessun motivo specificato");
                    mutedPlayers.put(uuid, reason);
                } catch (IllegalArgumentException e) {
                    logger.log(Level.WARNING, "UUID non valido trovato nel file: " + uuidStr, e);
                }
            }
        }
    }

    public void saveMutedPlayers() {
        FileConfiguration muteConfig = new YamlConfiguration();
        ConfigurationSection mutedSection = muteConfig.createSection("muted-players");
        
        for (Map.Entry<UUID, String> entry : mutedPlayers.entrySet()) {
            String uuidStr = entry.getKey().toString();
            mutedSection.set(uuidStr + ".reason", entry.getValue());
        }
        
        try {
            muteConfig.save(muteFile);
            logger.info("File muted_players.yml salvato con successo");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore durante il salvataggio del file muted_players.yml", e);
        }
    }

    public boolean isMuted(UUID playerUUID) {
        return mutedPlayers.containsKey(playerUUID);
    }

    public String getMuteReason(UUID playerUUID) {
        return mutedPlayers.getOrDefault(playerUUID, null);
    }

    public void mutePlayer(UUID playerUUID, String reason, String staffMember, String playerName) {
        String finalReason = reason != null ? reason : "Nessun motivo specificato";
        mutedPlayers.put(playerUUID, finalReason);
        saveMutedPlayers();


        if (plugin.getDiscordWebhook() != null) {
            String description = String.format("**Giocatore:** %s\n**Staff:** %s\n**Motivo:** %s", 
                playerName, staffMember, finalReason);
            plugin.getDiscordWebhook().sendWebhook("Nuovo Mute", description, 15158332); // Colore rosso
        }
    }

    public void unmutePlayer(UUID playerUUID, String playerName, String staffMember) {
        if (mutedPlayers.remove(playerUUID) != null && plugin.getDiscordWebhook() != null) {
            String description = String.format("**Giocatore:** %s\n**Staff:** %s\n**Mute rimosso con successo**", 
                playerName, staffMember);
            plugin.getDiscordWebhook().sendWebhook("Unmute", description, 5763719); // Colore verde
        }
        saveMutedPlayers();
    }
}