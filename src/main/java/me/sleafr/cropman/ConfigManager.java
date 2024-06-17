package me.sleafr.cropman;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigManager {

    private final Plugin plugin;
    private FileConfiguration config;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void createWorldConfigs() {
        List<String> ignoredWorlds = getIgnoredWorlds();
        for (World world : Bukkit.getWorlds()) {
            if (!ignoredWorlds.contains(world.getName())) {
                createWorldConfig(world.getName());
            }
        }
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public List<String> getIgnoredWorlds() {
        return config.getStringList("ignored-worlds");
    }

    public void createWorldConfig(String worldName) {
        File worldConfigFile = new File(plugin.getDataFolder() + File.separator + "worlds", worldName + ".yml");
        if (!worldConfigFile.exists()) {
            try {
                worldConfigFile.getParentFile().mkdirs();
                worldConfigFile.createNewFile();
                YamlConfiguration worldConfig = YamlConfiguration.loadConfiguration(worldConfigFile);

                worldConfig.save(worldConfigFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create config file for world: " + worldName);
                plugin.getLogger().severe("ERROR!\n"+e);
            }
        }
    }

    public YamlConfiguration getWorldConfig(String worldName) {
        File worldConfigFile = new File(plugin.getDataFolder() + File.separator + "worlds", worldName + ".yml");
        if (worldConfigFile.exists()) {
            return YamlConfiguration.loadConfiguration(worldConfigFile);
        } else {
            plugin.getLogger().warning("Config file for world " + worldName + " does not exist.");
            return null;
        }
    }

}