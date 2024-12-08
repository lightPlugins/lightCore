package io.lightstudios.core.hooks.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;

public class ProtocolLibManager {

    private final ProtocolManager protocolManager;

    public String getVersion() {
        return Bukkit.getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion();
    }

    public ProtocolLibManager() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }
}
