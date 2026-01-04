 package org.Exestudios.exeMode.commands;
 import org.bukkit.Bukkit;
 import org.bukkit.command.Command;
 import org.bukkit.command.CommandExecutor;
 import org.bukkit.command.CommandSender;
 import org.bukkit.entity.Player;
 import org.Exestudios.exeMode.ExeMode;
 import org.Exestudios.exeMode.utils.messages;
 import org.jetbrains.annotations.NotNull;


 public class exemute implements CommandExecutor {
     private final ExeMode plugin;

     public exemute(ExeMode plugin) {
         this.plugin = plugin;
     }

            @Override
            public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                     @NotNull String label, @NotNull String[] args) {
                if (!sender.hasPermission("exemode.mute")) {
                    sender.sendMessage(messages.get("mute.no-permission"));
                    return true;
                }

                if (args.length < 1) {
                    sender.sendMessage(messages.get("mute.usage"));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(messages.get("mute.player-not-found"));
                    return true;
                }

                if (plugin.getMuteManager().isMuted(target.getUniqueId())) {
                    sender.sendMessage(messages.get("mute.already-muted").replace("%player%", target.getName()));
                    return true;
                }

                String reason = null;
                if (args.length > 1) {
                    StringBuilder reasonBuilder = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        reasonBuilder.append(args[i]).append(" ");
                    }
                    reason = reasonBuilder.toString().trim();
                }

                plugin.getMuteManager().mutePlayer(
                    target.getUniqueId(),
                    reason,
                    sender.getName(),
                    target.getName()
                );

                sender.sendMessage(messages.get("mute.success-sender")
                        .replace("%player%", target.getName())
                        .replace("%reason%", reason != null ? reason : ""));

                target.sendMessage(messages.get("mute.success-target")
                        .replace("%reason%", reason != null ? reason : ""));

                return true;
            }
        }