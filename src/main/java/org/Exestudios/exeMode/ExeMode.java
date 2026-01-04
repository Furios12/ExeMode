package org.Exestudios.exeMode;

import org.Exestudios.exeMode.commands.*;
import org.Exestudios.exeMode.utils.DiscordWebhook;
import org.Exestudios.exeMode.utils.TempBanManager;
import org.Exestudios.exeMode.utils.TempMuteManager;
import org.Exestudios.exeMode.utils.NoteManager;
import org.Exestudios.exeMode.utils.StaffSpyManager;
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
import org.Exestudios.exeMode.utils.HomeManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.net.URI;

public final class ExeMode extends JavaPlugin {
    private MuteManager muteManager;
    private DiscordWebhook discordWebhook;
    private WarnManager warnManager;
    private HomeManager homeManager;

    // nuovi manager
    private TempBanManager tempBanManager;
    private TempMuteManager tempMuteManager;
    private NoteManager noteManager;
    private StaffSpyManager staffSpyManager;

    // comandi mantenuti come istanze per accesso esterno
    private exetempban exetempbanCmd;
    private exetmute exetmuteCmd;
    private execheck execheckCmd;
    private exenote exenoteCmd;
    private execlearwarn execlearwarnCmd;
    private exespy exespyCmd;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    // private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private void logColored(String message) {
        getLogger().info(message + ANSI_RESET);
    }

    private static final String CURRENT_VERSION = "1.0.3";
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
            logColored(ANSI_YELLOW + "Discord webhook module successfully configured!");
            logColored(ANSI_YELLOW + "Discord Webhook module Initialized!");

        } else {
            getLogger().warning("Discord webhook URL not configured!");
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
        logStartupStep("Setting Up Events", this::setupEvents);
        logStartupStep("Loading Commands", this::registerCommands);
        logStartupStep("Loading Messages", this::setupMessagesAndChecks);
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
        // logColored(ANSI_CYAN + "Need help? Visit:
        // https://github.com/Furios12/ExeMode/wiki (Not available yet)");
        logColored(ANSI_CYAN + "Found a bug? Report it: https://github.com/Furios12/ExeMode/issues");
        logColored(ANSI_GREEN + "----------------------------------------------");
    }

    public DiscordWebhook getDiscordWebhook() {
        return discordWebhook;
    }

    private void logStartupStep(String step, Runnable action) {
        logColored(ANSI_YELLOW + "⚙ " + step + "...");
        action.run();
        logColored(ANSI_GREEN + "✓ " + step + " | completed");
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("exec")).setExecutor(new exec());
        Objects.requireNonNull(this.getCommand("exea")).setExecutor(new exea());
        Objects.requireNonNull(this.getCommand("exesp")).setExecutor(new exesp());
        Objects.requireNonNull(this.getCommand("exes")).setExecutor(new exes());
        Objects.requireNonNull(this.getCommand("exe")).setExecutor(new exe());
        Objects.requireNonNull(this.getCommand("exemute")).setExecutor(new exemute(this));
        Objects.requireNonNull(this.getCommand("exeunmute")).setExecutor(new exeunmute(this));
        Objects.requireNonNull(this.getCommand("exemsg")).setExecutor(new exemsg());
        Objects.requireNonNull(this.getCommand("exeban")).setExecutor(new exeban(this));
        Objects.requireNonNull(this.getCommand("exeunban")).setExecutor(new exeunban(this));
        Objects.requireNonNull(this.getCommand("exeupdate")).setExecutor(new exeupdate(this));
        Objects.requireNonNull(this.getCommand("exekick")).setExecutor(new exekick(this));
        Objects.requireNonNull(this.getCommand("exewarn")).setExecutor(new exewarn(this));
        Objects.requireNonNull(this.getCommand("exeunwarn")).setExecutor(new exeunwarn(this));
        Objects.requireNonNull(this.getCommand("exefly")).setExecutor(new exefly(this));
        Objects.requireNonNull(this.getCommand("sethome")).setExecutor(new sethome(homeManager));
        Objects.requireNonNull(this.getCommand("home")).setExecutor(new home(homeManager));
        Objects.requireNonNull(this.getCommand("removehome")).setExecutor(new removehome(homeManager));

        // nuovi comandi: istanzio e salvo nei campi prima di registrare (per permettere
        // accesso esterno)
        this.exetempbanCmd = new exetempban(this);
        Objects.requireNonNull(this.getCommand("exetempban")).setExecutor(this.exetempbanCmd);

        this.exetmuteCmd = new exetmute(this);
        Objects.requireNonNull(this.getCommand("exetmute")).setExecutor(this.exetmuteCmd);

        this.execheckCmd = new execheck(this);
        Objects.requireNonNull(this.getCommand("execheck")).setExecutor(this.execheckCmd);

        this.exenoteCmd = new exenote(this);
        Objects.requireNonNull(this.getCommand("exenote")).setExecutor(this.exenoteCmd);

        this.execlearwarnCmd = new execlearwarn(this);
        Objects.requireNonNull(this.getCommand("execlearwarn")).setExecutor(this.execlearwarnCmd);

        this.exespyCmd = new exespy(this);
        Objects.requireNonNull(this.getCommand("exespy")).setExecutor(this.exespyCmd);
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
        // inizializzo nuovi manager qui
        this.tempBanManager = new TempBanManager(this);
        this.tempMuteManager = new TempMuteManager(this);
        this.noteManager = new NoteManager(this);
        this.staffSpyManager = new StaffSpyManager();

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinBanCheck(this), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        this.homeManager = new HomeManager(getDataFolder());
        logColored(ANSI_BLUE + "ExeMode " + CURRENT_VERSION + " Loading Extensions...");
        logColored(ANSI_CYAN + "ExeMode " + CURRENT_VERSION + " Extensions Ready to use: ChatColor, EssentialsChat");
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }

    // getter nuovi manager
    public TempBanManager getTempBanManager() {
        return tempBanManager;
    }

    public TempMuteManager getTempMuteManager() {
        return tempMuteManager;
    }

    public NoteManager getNoteManager() {
        return noteManager;
    }

    public StaffSpyManager getStaffSpyManager() {
        return staffSpyManager;
    }

    // getter comandi
    public exetempban getExetempban() {
        return exetempbanCmd;
    }

    public exetmute getExetmute() {
        return exetmuteCmd;
    }

    public execheck getExecheck() {
        return execheckCmd;
    }

    public exenote getExenote() {
        return exenoteCmd;
    }

    public execlearwarn getExeclearwarn() {
        return execlearwarnCmd;
    }

    public exespy getExespy() {
        return exespyCmd;
    }

    public void checkUpdates(Player player, boolean force) {

        if (!force && System.currentTimeMillis() - lastUpdateCheck < UPDATE_CHECK_INTERVAL
                && lastUpdateResult != null) {
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
                    player.sendMessage(
                            ChatColor.RED + "(Update Error) Error checking for updates. Check the console for details");
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
            throw new IOException("Unable to check for updates. Response code: " + conn.getResponseCode());
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
            player.sendMessage(ChatColor.YELLOW + "Warning: A new update for ExeMode is available!");
            player.sendMessage(ChatColor.GREEN + "current version: " + ChatColor.RED + CURRENT_VERSION);
            player.sendMessage(ChatColor.GREEN + "latest version: " + ChatColor.GREEN + result.latestVersion());
            player.sendMessage(ChatColor.AQUA + "Download it here: " + ChatColor.UNDERLINE + result.downloadUrl());
        } else {
            player.sendMessage(ChatColor.GREEN + "ExeMode is up to date! Thank you for keeping your plugin updated.");
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
                "Config Version",
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
                "player.quit",

                "no-home",
                "home-teleport",
                "home-removed",
                "home-set",

                "warn.no-permission",
                "warn.usage",
                "warn.player-not-found",
                "warn.message",
                "warn.success",
                "warn.broadcast",
                "warn.broadcast-reason",
                "warn.error",

                "unmute.no-permission",
                "unmute.usage",
                "unmute.player-not-found",
                "unmute.not-muted",
                "unmute.success-sender",
                "unmute.success-target",

                "msg.no-permission",
                "msg.usage",
                "msg.player-not-found",
                "msg.cannot-self",
                "msg.sent",
                "msg.received",

                "mute.no-permission",
                "mute.usage",
                "mute.player-not-found",
                "mute.already-muted",
                "mute.success-sender",
                "mute.success-target",

                "fly.toggled",
                "fly.state-on",
                "fly.state-off",
                "fly.toggled-for",
                "fly-no-permission",
                "fly-player-not-found",

                "unwarn.no-permission",
                "unwarn.usage",
                "unwarn.player-not-found",
                "unwarn.no-warns",
                "unwarn.broadcast",
                "unwarn.target"
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

        logShutdownStep(" Saving warn data", () -> {
            if (warnManager != null)
                warnManager.saveWarnFile();
        });

        logShutdownStep(" Saving mute data", () -> {
            if (muteManager != null)
                muteManager.saveMutedPlayers();
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
                logColored(ANSI_BLUE + "Updated version of messages.yml file found! Update in progress...");

                if (messagesFile.delete()) {
                    saveResource("messages.yml", true);
                    logColored(ANSI_GREEN + "messages.yml updated to version: " + currentVersion);
                } else {
                    getLogger().warning(
                            "Unable to delete old messages.yml file. The update may not complete successfully.");
                }
            }
        }
    }

    public WarnManager getWarnManager() {
        return warnManager;
    }
}