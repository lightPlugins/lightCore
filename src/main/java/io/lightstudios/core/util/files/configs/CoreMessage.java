package io.lightstudios.core.util.files.configs;

import org.bukkit.configuration.file.FileConfiguration;

public class CoreMessage {

    private final FileConfiguration config;

    public CoreMessage(FileConfiguration config) {
        this.config = config;
    }

    public int version() { return config.getInt("version"); }

    public String prefix() { return config.getString("prefix"); }
    public String noPermission() { return config.getString("noPermission"); }
    public String coreReload() { return config.getString("coreReload"); }
    public String wrongSyntax() { return config.getString("wrongSyntax"); }
    public String noNumber() { return config.getString("noNumber"); }
    public String commandCooldown() { return config.getString("protections.commandCooldown"); }
}
