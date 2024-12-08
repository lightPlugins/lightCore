package io.lightstudios.core.hooks.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class ProtocolLibManager {

    private final ProtocolManager protocolManager;

    public ProtocolLibManager() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }
}
