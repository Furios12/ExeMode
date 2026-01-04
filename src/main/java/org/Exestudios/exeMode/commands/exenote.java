package org.Exestudios.exeMode.commands;

import org.Exestudios.exeMode.ExeMode;
import org.Exestudios.exeMode.utils.NoteManager;
import org.Exestudios.exeMode.utils.messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class exenote implements CommandExecutor {
    private final ExeMode plugin;

    public exenote(ExeMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!sender.hasPermission("exemode.note")) {
            sender.sendMessage(messages.get("note.no-permission"));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(messages.get("note.usage"));
            return true;
        }
        String targetName = args[0];
        String note = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        @SuppressWarnings("deprecation")
        UUID targetUUID = Bukkit.getOfflinePlayer(targetName).getUniqueId();
        NoteManager nm = plugin.getNoteManager();
        nm.addNote(targetUUID, sender.getName(), note);
        sender.sendMessage(messages.get("note.added").replace("%player%", targetName));
        return true;
    }
}