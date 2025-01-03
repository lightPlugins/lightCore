package io.lightstudios.core.util.files.configs;

import io.lightstudios.core.LightCore;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class CoreSettings {

    private final FileConfiguration config;

    public CoreSettings(FileConfiguration config) {
        this.config = config;
    }

    public String language() { return config.getString("language"); }
    public Boolean multiServerEnabled() { return config.getBoolean("server-synchronisation.enable"); }

    public String syncType() {
        String data = config.getString("server-synchronisation.type");

        if(data == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "The server-synchronisation.type is not set in the core.yml",
                    "Please set it to either 'redis' or 'mysql'",
                    "and restart the server!"
            ));
            return "none";
        }

        if(!data.equalsIgnoreCase("redis") && !data.equalsIgnoreCase("mysql")) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "The server-synchronisation.type is not set to a valid value in the core.yml",
                    "Please set it to either 'redis' or 'mysql'",
                    "and restart the server!"
            ));
            return "none";
        }
        return data;
    }

    // redis server credentials
    public boolean redisEnabled() { return config.getBoolean("server-synchronisation.redis.enable"); }
    public String redisHost() { return config.getString("server-synchronisation.redis.host"); }
    public int redisPort() { return config.getInt("server-synchronisation.redis.port"); }
    public String redisPassword() { return config.getString("server-synchronisation.redis.password"); }
    public String serverName() { return config.getString("server-synchronisation.server-name"); }
}
