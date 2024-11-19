package io.lightstudios.core.util.files;

import io.lightstudios.core.LightCore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class FileManager {

    /*
     *
     * Configuration-Manager by lightStudios © 2024
     * This class is used to manage the configuration files of your plugin.
     * Features:
     *  - Reload the config / save the config
     *  - Automatically insert new keys into existing config
     *  - Insert manually new ConfigurationSections into existing config
     *
     *  LICENSE: MIT
     *  AUTHOR: lightStudios
     *  VERSION: 1.0
     *  DATE: 2024
     */

    private final JavaPlugin plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;
    private final String configName;
    private final boolean loadDefaultsOneReload;

    public FileManager(JavaPlugin plugin, String configName, boolean loadDefaultsOnReload) {
        this.plugin = plugin;
        this.loadDefaultsOneReload = loadDefaultsOnReload;
        this.configName = configName;
        saveDefaultConfig(configName);

    }

    /**
     * Reloads the config file
     * @param configName the name of the config file
     */
    public void reloadConfig(String configName) {
        if(this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), configName);

        this.plugin.reloadConfig();

        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = this.plugin.getResource(configName);
        if(defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    /**
     * Get the config file
     * @return the FileConfiguration
     */
    public FileConfiguration getConfig() {
        if(this.dataConfig == null)
            reloadConfig(configName);

        return this.dataConfig;

    }

    /**
     * Save changes to the config file
     */
    public void saveConfig() {
        if(this.dataConfig == null || this.configFile == null)
            return;

        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            throw new RuntimeException("[FileManager] §4Could not save config to §c", e);
        }
    }

    /**
     * Check and insert new keys into existing config from a ConfigurationSection
     * @param configName the name of the config file
     * @param section the section to check for new keys
     * TODO: Not live tested yet
     */
    public void addNonExistingKeys(String configName, String section) {
        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(Objects.requireNonNull(this.plugin.getResource(configName))));
        FileConfiguration existingConfig = getConfig();
        ConfigurationSection defaultSection = defaultConfig.getConfigurationSection(section);
        ConfigurationSection existingSection = existingConfig.getConfigurationSection(section);

        if (defaultSection != null && existingSection != null) {
            for (String key : defaultSection.getKeys(true)) {
                if (!existingSection.getKeys(true).contains(key)) {
                    LightCore.instance.getConsolePrinter().printInfo(
                            "Found non existing config key in section " + section + ". Adding " + key + " into " + configName);
                    existingSection.set(key, defaultSection.get(key));
                }
            }

            try {
                existingConfig.save(configFile);
                LightCore.instance.getConsolePrinter().printInfo(
                        "Your config section " + section + " in " + configName + " is up to date.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            saveConfig();
        } else {
            LightCore.instance.getConsolePrinter().printError(
                    "Section " + section + " not found in " + configName);
        }
    }

    /**
     * Save the default config file
     * @param configName the name of the config file
     */
    private void saveDefaultConfig(String configName) {
        if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), this.configName);

        if (!this.configFile.exists()) {
            this.plugin.saveResource(configName, false);
        } else {
            // Merge the default config into the existing config

            if(loadDefaultsOneReload) {
                FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(Objects.requireNonNull(this.plugin.getResource(configName))));
                FileConfiguration existingConfig = getConfig();
                for (String key : defaultConfig.getKeys(true)) {
                    if (!existingConfig.getKeys(true).contains(key)) {
                        LightCore.instance.getConsolePrinter().printInfo(
                                "Found non existing config key. Adding " + key + " into " + configName);
                        existingConfig.set(key, defaultConfig.get(key));

                    }
                }

                try {

                    existingConfig.save(configFile);
                    LightCore.instance.getConsolePrinter().printInfo(
                            "Your config " + configName + " is up to date.");

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                saveConfig();
            }
        }
    }
}
