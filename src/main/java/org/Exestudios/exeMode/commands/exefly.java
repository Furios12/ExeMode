package org.Exestudios.exeMode.commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.Exestudios.exeMode.utils.messages;
import org.Exestudios.exeMode.ExeMode;
import org.jetbrains.annotations.NotNull;

public class exefly implements CommandExecutor {

    private final ExeMode plugin;

    public exefly(ExeMode plugin) {
        this.plugin = plugin;
                }

                @Override
                public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                       @NotNull String label, @NotNull String[] args) {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(messages.get("no-player"));
                        return true;
                    }

                    if (!player.hasPermission("exemode.fly")) {
                        player.sendMessage(messages.get("no-permission"));
                        return true;
                    }

                    if (args.length == 0) {
                        toggleFly(player);
                        player.sendMessage(messages.get("fly.toggled")
                                .replace("%state%", player.getAllowFlight() ? messages.get("fly.state-on") : messages.get("fly.state-off")));
                    } else if (args.length == 1 && player.hasPermission("exemode.fly.others")) {
                        Player target = plugin.getServer().getPlayer(args[0]);
                        if (target == null) {
                            player.sendMessage(messages.get("fly-player-not-found"));
                            return true;
                        }
                        toggleFly(target);
                        player.sendMessage(messages.get("fly.toggled-for")
                                .replace("%state%", target.getAllowFlight() ? messages.get("fly.state-on") : messages.get("fly.state-off"))
                                .replace("%player%", target.getName()));
                    }

                    return true;
                }

                private void toggleFly(@NotNull Player player) {
                    boolean newFlyState = !player.getAllowFlight();
                    player.setAllowFlight(newFlyState);
                    player.setFlying(newFlyState);
                }
            }