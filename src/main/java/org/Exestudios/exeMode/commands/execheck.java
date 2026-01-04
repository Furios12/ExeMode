package org.Exestudios.exeMode.commands;

import org.Exestudios.exeMode.ExeMode;
import org.Exestudios.exeMode.utils.NoteManager;
import org.Exestudios.exeMode.utils.TempBanManager;
import org.Exestudios.exeMode.utils.TempMuteManager;
import org.Exestudios.exeMode.utils.WarnManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class execheck implements CommandExecutor {
    private final ExeMode plugin;

    public execheck(ExeMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!sender.hasPermission("exemode.check")) {
            sender.sendMessage("No permission");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage("Usage: /execheck <player>");
            return true;
        }
        String targetName = args[0];
        Player p = Bukkit.getPlayer(targetName);
        @SuppressWarnings("deprecation")
        UUID targetUUID = (p != null) ? p.getUniqueId() : Bukkit.getOfflinePlayer(targetName).getUniqueId();

        WarnManager wm = plugin.getWarnManager();
        TempMuteManager mm = plugin.getTempMuteManager();
        TempBanManager bm = plugin.getTempBanManager();
        NoteManager nm = plugin.getNoteManager();

        int warns = wm.getWarnsCount(targetUUID);
        boolean muted = mm.isMuted(targetUUID);
        boolean banned = bm.isBanned(targetUUID);
        sender.sendMessage("Warns: " + warns);
        sender.sendMessage("Muted: " + muted + (muted ? " until " + mm.getExpiry(targetUUID) : ""));
        sender.sendMessage("Banned: " + banned + (banned ? " until " + bm.getExpiry(targetUUID) : ""));
        List<String> notes = nm.getNotes(targetUUID);
        sender.sendMessage("Staff notes: ");
        for (String n : notes)
            sender.sendMessage("- " + n);
        return true;
    }
}