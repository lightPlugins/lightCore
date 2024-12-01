package io.lightstudios.core.util.files.configs;

import org.bukkit.configuration.file.FileConfiguration;

public class CoreSettings {

    private final FileConfiguration config;

    public CoreSettings(FileConfiguration config) {
        this.config = config;
    }

    public String language() { return config.getString("language"); }
}
