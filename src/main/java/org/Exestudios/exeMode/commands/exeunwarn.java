 package org.Exestudios.exeMode.commands;
 import org.bukkit.Bukkit;
 import org.bukkit.command.Command;
 import org.bukkit.command.CommandExecutor;
 import org.bukkit.command.CommandSender;
 import org.bukkit.entity.Player;
 import org.Exestudios.exeMode.ExeMode;
 import org.Exestudios.exeMode.utils.messages;
 import org.jetbrains.annotations.NotNull;

    public class exeunwarn implements CommandExecutor {
        private final ExeMode plugin;

        public exeunwarn(ExeMode plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                 @NotNull String label, @NotNull String[] args) {
            if (!sender.hasPermission("exemode.unwarn")) {
                sender.sendMessage(messages.get("unwarn.no-permission"));
                return true;
            }

            if (args.length < 1) {
                sender.sendMessage(messages.get("unwarn.usage"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(messages.get("unwarn.player-not-found"));
                return true;
            }

            int currentWarns = plugin.getWarnManager().getWarnsCount(target.getUniqueId());
            if (currentWarns <= 0) {
                sender.sendMessage(messages.get("unwarn.no-warns"));
                return true;
            }

            if (plugin.getWarnManager().removeWarn(target.getUniqueId())) {
                int remainingWarns = plugin.getWarnManager().getWarnsCount(target.getUniqueId());

                if (plugin.getDiscordWebhook() != null) {
                    String description = String.format("""
                        **Giocatore:** %s
                        **Staff:** %s
                        **Warns rimanenti:** %d
                        **Azione:** Rimozione warn""",
                        target.getName(),
                        sender.getName(),
                        remainingWarns);
                    plugin.getDiscordWebhook().sendWebhook("⚠️ Warn Rimosso", description, 5763719);
                }

                Bukkit.broadcastMessage(messages.get("unwarn.broadcast")
                        .replace("%player%", target.getName())
                        .replace("%staff%", sender.getName())
                        .replace("%remaining%", String.valueOf(remainingWarns)));

                target.sendMessage(messages.get("unwarn.target")
                        .replace("%remaining%", String.valueOf(remainingWarns)));
            }

            return true;
        }
    }