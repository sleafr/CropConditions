package me.sleafr.cropman;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CropMan extends JavaPlugin {

    private ConfigManager configManager;
    public static boolean RealisticSeasons;


    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        configManager.createWorldConfigs();

        if (getServer().getPluginManager().getPlugin("RealisticSeasons") != null) {
            RealisticSeasons = true;
        } else {
            Bukkit.getLogger().info("RealisticSeasons not installed. Some functionalities will be disabled.");
        }

        getServer().getPluginManager().registerEvents(new EventsListener(this, RealisticSeasons), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public ConfigManager getConfigManager() {
        return configManager;
    }
}