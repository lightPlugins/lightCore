package io.lightstudios.core.util.files.configs;

import org.bukkit.configuration.file.FileConfiguration;

public class CoreSettings {

    private final FileConfiguration config;

    public CoreSettings(FileConfiguration config) {
        this.config = config;
    }

    public String language() { return config.getString("language"); }

    // redis server credentials
    public boolean redisEnabled() { return config.getBoolean("server-synchronisation.redis.enable"); }
    public String redisHost() { return config.getString("server-synchronisation.redis.host"); }
    public int redisPort() { return config.getInt("server-synchronisation.redis.port"); }
    public String redisPassword() { return config.getString("server-synchronisation.redis.password"); }
    public String serverName() { return config.getString("server-synchronisation.server-name"); }
}
