package io.lightstudios.core.hooks.placeholderapi;

import org.bukkit.Bukkit;

public class PlaceholderAPIManager {

    public String getVersion() {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getDescription().getVersion();
    }
}
