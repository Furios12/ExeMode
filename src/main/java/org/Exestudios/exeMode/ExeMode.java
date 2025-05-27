package org.Exestudios.exeMode;

import org.Exestudios.exeMode.commands.*;
import org.Exestudios.exeMode.utils.DiscordWebhook;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.Exestudios.exeMode.events.PlayerJoinBanCheck;
import org.Exestudios.exeMode.events.PlayerConnectionListener;
import org.Exestudios.exeMode.utils.messages;
import org.Exestudios.exeMode.utils.WarnManager;
import org.Exestudios.exeMode.events.ChatListener;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.Exestudios.exeMode.utils.MuteManager;


import java.io.*;
import java.net.HttpURLConnection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.net.URI;

public final class ExeMode extends JavaPlugin {
    private MuteManager muteManager;
    private DiscordWebhook discordWebhook;
    private WarnManager warnManager;
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    //private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private void logColored(String message) {
        getLogger().info(message + ANSI_RESET);
    }

    private static final String CURRENT_VERSION = "1.0.2";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/Furios12/ExeMode/releases/latest";
    private UpdateResult lastUpdateResult;
    private long lastUpdateCheck;
    private static final long UPDATE_CHECK_INTERVAL = TimeUnit.HOURS.toMillis(1);

    private record UpdateResult(boolean updateAvailable, String latestVersion, String downloadUrl) {
    }

    private void setupDiscordWebhook() {
        saveDefaultConfig();
        String webhookUrl = getConfig().getString("discord-webhook-url", "");
        if (!webhookUrl.isEmpty()) {
            this.discordWebhook = new DiscordWebhook(webhookUrl);
            getLogger().info("Discord webhook configurato con successo!");
        } else {
            getLogger().warning("URL del webhook Discord non configurato!");
        }
    }



    @Override
    public void onEnable() {
    logColored(ANSI_CYAN + "----------------------------------------------");
    logColored(ANSI_CYAN + "               EXEMODE | STARTING UP          ");
    logColored(ANSI_CYAN + "----------------------------------------------");
    logColored(ANSI_YELLOW + "     Version: " + CURRENT_VERSION);
    logColored(ANSI_YELLOW + "     State: Loading");
    logColored(ANSI_YELLOW + "----------------------------------------------");

    logStartupStep("Checking Updates", () -> checkUpdates(null, false));
    logStartupStep("Loading Commands", this::registerCommands);
    logStartupStep("Loading Messages", this::setupMessagesAndChecks);
    logStartupStep("Setting Up Events", this::setupEvents);
    logStartupStep("Starting Warn System", () -> this.warnManager = new WarnManager(this));
    logStartupStep("Loading Discord Webhook", this::setupDiscordWebhook);


    logColored(ANSI_CYAN + "----------------------------------------------");
    logColored(ANSI_CYAN + "               EXEMODE | STARTED UP          ");
    logColored(ANSI_GREEN + "----------------------------------------------");
    logColored(ANSI_YELLOW + "     Version: " + CURRENT_VERSION);
    logColored(ANSI_GREEN + "     Status: ACTIVE ✓");
    logColored(ANSI_YELLOW + "     Author: Exestudios");
    logColored(ANSI_YELLOW + "     Premium: No");
    logColored(ANSI_GREEN + "----------------------------------------------");
    logColored(ANSI_CYAN + "Need help? Visit: https://github.com/Furios12/ExeMode/wiki (Not available yet)");
    logColored(ANSI_CYAN + "Found a bug? Report it: https://github.com/Furios12/ExeMode/issues");
    logColored(ANSI_GREEN + "----------------------------------------------");
}


public DiscordWebhook getDiscordWebhook() {
    return discordWebhook;
}

private void logStartupStep(String step, Runnable action) {
    logColored(ANSI_YELLOW + "⚙ " + step + "...");
    action.run();
    logColored(ANSI_GREEN + "✓ " + step + "| completed");
}

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("exc")).setExecutor(new exc());
        Objects.requireNonNull(this.getCommand("exa")).setExecutor(new exa());
        Objects.requireNonNull(this.getCommand("exsp")).setExecutor(new exsp());
        Objects.requireNonNull(this.getCommand("exs")).setExecutor(new exs());
        Objects.requireNonNull(this.getCommand("exe")).setExecutor(new exe());
        Objects.requireNonNull(this.getCommand("exemute")).setExecutor(new exemute(this));
        Objects.requireNonNull(this.getCommand("exeunmute")).setExecutor(new exeunmute(this));
        Objects.requireNonNull(this.getCommand("exesmg")).setExecutor(new exesmg());
        Objects.requireNonNull(this.getCommand("exeban")).setExecutor(new exeban(this));
        Objects.requireNonNull(this.getCommand("exeunban")).setExecutor(new exeunban(this));
        Objects.requireNonNull(this.getCommand("exeupdate")).setExecutor(new exeupdate(this));
        Objects.requireNonNull(this.getCommand("exekick")).setExecutor(new exekick(this));
        Objects.requireNonNull(this.getCommand("exewarn")).setExecutor(new exewarn(this));
        Objects.requireNonNull(this.getCommand("exeunwarn")).setExecutor(new exeunwarn(this));
        Objects.requireNonNull(this.getCommand("exefly")).setExecutor(new exefly(this));
    }

    private void setupMessagesAndChecks() {
        logColored(ANSI_YELLOW + "ExeMode " + CURRENT_VERSION + " Checking updates for messages.yml...");
        checkMessageVersion();
        logColored(ANSI_GREEN + "ExeMode " + CURRENT_VERSION + " Checking updates for messages.yml... Done!");
        
        logColored(ANSI_BLUE + "ExeMode " + CURRENT_VERSION + " Loading Messages...");
        messages.init(this);
        verifyRequiredMessages();
        logColored(ANSI_GREEN + "ExeMode " + CURRENT_VERSION + " Loading Messages... Done!");
    }

    private void setupEvents() {
        this.muteManager = new MuteManager(this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinBanCheck(this), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        logColored(ANSI_BLUE + "ExeMode " + CURRENT_VERSION + " Loading Extensions...");
        logColored(ANSI_CYAN + "ExeMode " + CURRENT_VERSION + " Extensions active: ChatColor, EssentialsChat");
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }


    public void checkUpdates(Player player, boolean force) {

        if (!force && System.currentTimeMillis() - lastUpdateCheck < UPDATE_CHECK_INTERVAL && lastUpdateResult != null) {
            if (player != null) {
                sendUpdateMessage(player, lastUpdateResult);
            }
            return;
        }

        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {
                UpdateResult result = performUpdateCheck();
                lastUpdateResult = result;
                lastUpdateCheck = System.currentTimeMillis();

                if (player != null) {
                    getServer().getScheduler().runTask(this, () -> sendUpdateMessage(player, result));
                } else if (result.updateAvailable) {
                    logUpdateAvailable(result);
                }
            } catch (Exception e) {
                getLogger().warning("Update Error: " + e.getMessage());
                if (player != null) {
                    player.sendMessage(ChatColor.RED + "(Update Error) Errore durante il controllo degli aggiornamenti. Controlla la console per i dettagli.");
                }
            }
        });
    }

    private UpdateResult performUpdateCheck() throws Exception {
        URI uri = URI.create(GITHUB_API_URL);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new IOException("Impossibile controllare gli aggiornamenti. Codice risposta: " + conn.getResponseCode());
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response.toString());
            String latestVersion = ((String) json.get("tag_name")).replaceAll("^v", "");
            String downloadUrl = (String) json.get("html_url");

            boolean updateAvailable = !CURRENT_VERSION.equals(latestVersion);
            return new UpdateResult(updateAvailable, latestVersion, downloadUrl);
        }
    }

private void sendUpdateMessage(Player player, UpdateResult result) {
    if (result.updateAvailable) {
        player.sendMessage(ChatColor.GREEN + """
            ╔════════════════════════════════════╗
            ║         UPDATE AVAILABLE!          ║
            ╠════════════════════════════════════╣
            ║  Current: %s%s
            ║  Latest:  %s%s
            ║  Download: %s%s
            ╚════════════════════════════════════╝""".formatted(
                ChatColor.RED, CURRENT_VERSION,
                ChatColor.GREEN, result.latestVersion,
                ChatColor.AQUA, result.downloadUrl));
    } else {
        player.sendMessage(ChatColor.GREEN + """
            ╔════════════════════════════════════╗
            ║      EXEMODE IS UP TO DATE!        ║
            ╠════════════════════════════════════╣
            ║  Current Version: %s               ║
            ╚════════════════════════════════════╝""".formatted(CURRENT_VERSION));
    }
}

    private void logUpdateAvailable(UpdateResult result) {
        logColored(ANSI_YELLOW + "=================================");
        logColored(ANSI_YELLOW + "A new update is available!");
        logColored(ANSI_RED + "Current version: " + CURRENT_VERSION);
        logColored(ANSI_GREEN + "New version: " + result.latestVersion());
        logColored(ANSI_CYAN + "Download at: " + result.downloadUrl());
        logColored(ANSI_YELLOW + "=================================");
    }

    private void verifyRequiredMessages() {
        logColored(ANSI_BLUE + "Checking required messages:");
        String[] requiredMessages = {
                "ban.message",
                "ban.broadcast",
                "ban.broadcast-reason",
                "no-player",
                "no-permission",
                "gamemode-creative",
                "gamemode-survival",
                "gamemode-adventure",
                "gamemode-spectator",
                "ban.no-permission",
                "ban.message",
                "ban.broadcast",
                "ban.broadcast-reason",
                "ban.error",
                "ban.usage",
                "unban.no-permission",
                "unban.usage",
                "unban.not-banned",
                "unban.broadcast",
                "unban.error",
                "unban.success",
                "kick.no-permission",
                "kick.usage",
                "kick.player-not-found",
                "kick.message",
                "kick.broadcast",
                "kick.broadcast-reason",
                "player.join",
                "player.quit"
        };

        for (String path : requiredMessages) {
            if (messages.exists(path)) {
                logColored(ANSI_GREEN + "✓ " + path + " found");
            } else {
                logColored(ANSI_RED + "✗ " + path + " missing!");
            }
        }
    }

    @Override
    public void onDisable() {
        logColored(ANSI_CYAN + "----------------------------------------------");
        logColored(ANSI_CYAN + "               EXEMODE | SHUTTING DOWN        ");
        logColored(ANSI_CYAN + "----------------------------------------------");
        logColored(ANSI_YELLOW + "     Version: " + CURRENT_VERSION);
        logColored(ANSI_YELLOW + "     State: Disabling");
        logColored(ANSI_YELLOW + "----------------------------------------------");

    logShutdownStep("Saving warn data", () -> {
        if (warnManager != null) warnManager.saveWarnFile();
    });

    logShutdownStep("Saving mute data", () -> {
        if (muteManager != null) muteManager.saveMutedPlayers();
    });

    logColored(ANSI_CYAN + "----------------------------------------------");
        logColored(ANSI_CYAN + "               EXEMODE | SHUTTING DOWN    ");
    logColored(ANSI_CYAN + "----------------------------------------------");
    logColored(ANSI_YELLOW + "     Version: " + CURRENT_VERSION);
    logColored(ANSI_RED + "     Status: OFFLINE ✗");
    logColored(ANSI_YELLOW + "     Author: Exestudios");
    logColored(ANSI_YELLOW + "     Premium: No");
    logColored(ANSI_RED + "----------------------------------------------");
    logColored(ANSI_YELLOW + "Thanks for using ExeMode! See you next time!");
    logColored(ANSI_RED + "----------------------------------------------");
}

private void logShutdownStep(String step, Runnable action) {
    logColored(ANSI_YELLOW + "⚡ " + step + "...");
    action.run();
    logColored(ANSI_GREEN + "✓ " + step + " completed");
}

    private void checkMessageVersion() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        FileConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        String currentVersion = CURRENT_VERSION;

        if (messagesFile.exists()) {
            String fileVersion = messagesConfig.getString("Config Version");

            if (fileVersion == null || !fileVersion.equals(currentVersion)) {
                getLogger().info("Rilevata una versione diversa del file messages.yml. Aggiornamento in corso...");

                if (messagesFile.delete()) {
                    saveResource("messages.yml", true);
                    getLogger().info("File messages.yml aggiornato alla versione " + currentVersion);
                } else {
                    getLogger().warning("Impossibile eliminare il vecchio file messages.yml. L'aggiornamento potrebbe non essere completato correttamente.");
                }
            }
        }
    }

    public WarnManager getWarnManager() {
        return warnManager;
    }
}