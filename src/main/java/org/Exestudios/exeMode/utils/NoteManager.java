package org.Exestudios.exeMode.utils;

import org.Exestudios.exeMode.ExeMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NoteManager {
    private final ExeMode plugin;
    private final File file;
    private FileConfiguration config;

    public NoteManager(ExeMode plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "notes.yml");
        if (!file.exists()) {
            try {
                if (file.getParentFile() != null) file.getParentFile().mkdirs();
                if (!file.createNewFile()) {
                    plugin.getLogger().warning("Unable to create notes.yml file.");
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Error creating notes.yml: " + e.getMessage());
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void addNote(UUID target, String staff, String note) {
        String path = "players." + target + ".notes";
        List<String> notes = config.getStringList(path);
        notes.add(String.format("%s | %s | %s", new Date().toString(), staff, note));
        config.set("players." + target + ".notes", notes);
        try { config.save(file); } catch (IOException e) { plugin.getLogger().severe("Unable to save notes: " + e.getMessage()); }
    }

    public List<String> getNotes(UUID target) {
        return config.getStringList("players." + target + ".notes");
    }
}