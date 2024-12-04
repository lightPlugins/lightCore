package io.lightstudios.core;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

@Plugin(id = "lightcore", name = "LightCore", version = "0.1.7",
        url = "https://example.org", description = "Core Plugin of LightStudios", authors = {"LightStudios"})
public class LightCoreProxy {

    private final ProxyServer server;

    public LightCoreProxy(ProxyServer server) {
        this.server = server;
    }


}
