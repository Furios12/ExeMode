package org.Exestudios.exeMode.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class messages {
    private static FileConfiguration messages;
    
    public static void init(Plugin plugin) {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    public static String get(String path) {
        String message = messages.getString(path, "&4&lErrore(Missing Message): " + path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }


    public static boolean exists(String path) {
        return messages.contains(path);
    }
}