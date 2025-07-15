package org.Exestudios.exeMode.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeManager {
    private final Map<UUID, Location> homes = new HashMap<>();
    private final File file;
    private final YamlConfiguration config;

    public HomeManager(File dataFolder) {
        this.file = new File(dataFolder, "homes.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        loadHomes();
    }

    public void setHome(Player player) {
        homes.put(player.getUniqueId(), player.getLocation());
        saveHome(player);
    }

    public Location getHome(Player player) {
        return homes.get(player.getUniqueId());
    }

    public void removeHome(Player player) {
        UUID uuid = player.getUniqueId();
        homes.remove(uuid);
        String path = uuid.toString();
        config.set(path, null);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasHome(Player player) {
        return homes.containsKey(player.getUniqueId());
    }

    private void saveHome(Player player) {
        Location loc = homes.get(player.getUniqueId());
        String path = player.getUniqueId().toString();
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadHomes() {
        for (String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            String world = config.getString(key + ".world");
            double x = config.getDouble(key + ".x");
            double y = config.getDouble(key + ".y");
            double z = config.getDouble(key + ".z");
            float yaw = (float) config.getDouble(key + ".yaw");
            float pitch = (float) config.getDouble(key + ".pitch");
            Location loc = new Location(org.bukkit.Bukkit.getWorld(world), x, y, z, yaw, pitch);
            homes.put(uuid, loc);
        }
    }
}