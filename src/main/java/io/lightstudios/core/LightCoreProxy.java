package io.lightstudios.core;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import io.lightstudios.core.proxy.messaging.proxy.receive.ReceiveBackendKickRequest;
import io.lightstudios.core.proxy.messaging.proxy.receive.ReceiveBackendTeleportRequest;
import io.lightstudios.core.util.ProxyConsolePrinter;
import lombok.Getter;

import java.nio.file.Path;

@Getter
@Plugin(id = "lightcore", name = "LightCore", version = "0.1.7",
        url = "https://example.org", description = "Core Plugin of LightStudios", authors = {"LightStudios"})
public class LightCoreProxy {

    public static final MinecraftChannelIdentifier IDENTIFIER =
            MinecraftChannelIdentifier.from("lightstudio:lightcore");

    private final ProxyServer server;
    private final Path dataDirectory;

    public static LightCoreProxy instance;
    private final ProxyConsolePrinter consolePrinter;

    @Inject
    public LightCoreProxy(ProxyServer server, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.dataDirectory = dataDirectory;
        instance = this;
        this.consolePrinter = new ProxyConsolePrinter();
        consolePrinter.sendInfo("Starting LightCore plugin on Velocity...");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        consolePrinter.sendInfo("Registering LightCore plugin on Velocity successfully!");
        registerChannelRegistrars();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        consolePrinter.sendInfo("LightCore plugin has been disabled on Velocity!");

    }

    public void registerChannelRegistrars() {
        server.getChannelRegistrar().register(IDENTIFIER);
        server.getEventManager().register(this, new ReceiveBackendTeleportRequest());
        server.getEventManager().register(this, new ReceiveBackendKickRequest());
        consolePrinter.sendInfo("Registering Channel Registrars for LightCore plugin on Velocity...");
    }


}
