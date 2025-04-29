package org.Exestudios.exeMode;

import org.Exestudios.exeMode.commands.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.Exestudios.exeMode.events.PlayerJoinBanCheck;
import org.Exestudios.exeMode.utils.messages;

import java.io.File;
import java.util.Objects;

public final class ExeMode extends JavaPlugin {



    @Override
    public void onEnable() {
        getLogger().info("""
            -
            █▄─▄▄─█▄─▀─▄█▄─▄▄─█▄─▀█▀─▄█─▄▄─█▄─▄▄▀█▄─▄▄─█
            ██─▄█▀██▀─▀███─▄█▀██─█▄█─██─██─██─██─██─▄█▀█
            ▀▄▄▄▄▄▀▄▄█▄▄▀▄▄▄▄▄▀▄▄▄▀▄▄▄▀▄▄▄▄▀▄▄▄▄▀▀▄▄▄▄▄▀""");
        getLogger().info("ExeMode 1.0.0 Loading Commands Protocol...");
        Objects.requireNonNull(this.getCommand("exc")).setExecutor(new exc());
        Objects.requireNonNull(this.getCommand("exa")).setExecutor(new exa());
        Objects.requireNonNull(this.getCommand("exsp")).setExecutor(new exsp());
        Objects.requireNonNull(this.getCommand("exs")).setExecutor(new exs());
        Objects.requireNonNull(this.getCommand("exe")).setExecutor(new exe());
        Objects.requireNonNull(this.getCommand("exeban")).setExecutor(new exeban(this));
        Objects.requireNonNull(this.getCommand("exeunban")).setExecutor(new exeunban(this));
        getLogger().info("ExeMode 1.0.0 Loading Commands Protocol... Done!");
        getLogger().info("ExeMode 1.0.0 Starting checking updates for messages.yml...");
        checkMessageVersion();
        getLogger().info("ExeMode 1.0.0 Starting checking updates for messages.yml... Done!");
        getLogger().info("ExeMode 1.0.0 Loading Messages Protocol...");
        messages.init(this);
        // Debug dei messaggi
getLogger().info("Verifica dei messaggi necessari:");
String[] messaggiRichiesti = {
    "ban.message",
    "ban.broadcast",
    "ban.broadcast-reason"
};

for (String path : messaggiRichiesti) {
    if (messages.exists(path)) {
        getLogger().info("✓ " + path + " trovato");
    } else {
        getLogger().warning("✗ " + path + " mancante!");
    }
}
        getLogger().info("ExeMode 1.0.0 Loading Messages Protocol... Done!");
        getLogger().info("ExeMode 1.0.0 Loading Events Protocol...");
        getServer().getPluginManager().registerEvents(new PlayerJoinBanCheck(this), this);
        getLogger().info("ExeMode 1.0.0 Loading Events Protocol... Done!");
        getLogger().info("ExeMode 1.0.0 Loading Extensions Protocol...");
        getLogger().info("ExeMode 1.0.0 Extensions active: ChatColor, EssentialsChat ");
        getLogger().info("ExeMode 1.0.0 Ready to use!");
        getLogger().info("ExeMode 1.0.0 Developed by Exestudios");
    }

    private void checkMessageVersion() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        FileConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Versione attuale del file messages.yml
        String currentVersion = "1.0.0";

        // Controlla se il file esiste e leggi la sua versione
        if (messagesFile.exists()) {
            String fileVersion = messagesConfig.getString("Config Version");

            // Se la versione è diversa, sostituisci il file
            if (fileVersion == null || !fileVersion.equals(currentVersion)) {
                getLogger().info("Rilevata una versione diversa del file messages.yml. Aggiornamento in corso...");

                // Elimina il vecchio file e verifica il risultato
                if (messagesFile.delete()) {
                    // Salva la nuova versione del file
                    saveResource("messages.yml", true);
                    getLogger().info("File messages.yml aggiornato alla versione " + currentVersion);
                } else {
                    getLogger().warning("Impossibile eliminare il vecchio file messages.yml. L'aggiornamento potrebbe non essere completato correttamente.");
                }
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("""
            -
            █▄─▄▄─█▄─▀─▄█▄─▄▄─█▄─▀█▀─▄█─▄▄─█▄─▄▄▀█▄─▄▄─█
            ██─▄█▀██▀─▀███─▄█▀██─█▄█─██─██─██─██─██─▄█▀█
            ▀▄▄▄▄▄▀▄▄█▄▄▀▄▄▄▄▄▀▄▄▄▀▄▄▄▀▄▄▄▄▀▄▄▄▄▀▀▄▄▄▄▄▀""");
        getLogger().info("ExeMode 1.0.0 Closing Commands Protocol...");
        getLogger().info("ExeMode 1.0.0 Closing Messages Protocol...");
        getLogger().info("ExeMode 1.0.0 Closing Protocol system...");
        getLogger().info("ExeMode 1.0.0 Closing Protocol system... Done!");
        getLogger().info("ExeMode 1.0.0 Closed!");
        getLogger().info("ExeMode 1.0.0 Developed by Exestudios");
    }
}