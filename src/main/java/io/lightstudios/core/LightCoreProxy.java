package io.lightstudios.core;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import io.lightstudios.core.proxy.messaging.proxy.receiver.ReceiveBackendRequest;
import io.lightstudios.core.util.ProxyConsolePrinter;
import lombok.Getter;
import org.bstats.velocity.Metrics;

import java.nio.file.Path;

@Getter
@Plugin(id = "lightcore", name = "LightCore", version = "0.1.8",
        url = "https://example.org", description = "Core Plugin of LightStudios", authors = {"LightStudios"})
public class LightCoreProxy {

    public static final MinecraftChannelIdentifier IDENTIFIER =
            MinecraftChannelIdentifier.from("lightstudio:lightcore");

    private final ProxyServer server;
    private final Path dataDirectory;

    public static LightCoreProxy instance;
    private final ProxyConsolePrinter consolePrinter;

    private final Metrics.Factory metrics;

    @Inject
    public LightCoreProxy(ProxyServer server, @DataDirectory Path dataDirectory, Metrics.Factory metrics) {
        this.server = server;
        this.dataDirectory = dataDirectory;
        instance = this;
        this.consolePrinter = new ProxyConsolePrinter();
        consolePrinter.sendInfo("Starting LightCore plugin on Velocity ...");
        this.metrics = metrics;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        consolePrinter.sendInfo("Registering LightCore plugin on Velocity successfully!");
        registerChannelRegistrars();
        consolePrinter.sendInfo("Starting bStats metrics instance ...");
        this.metrics.make(this, 24560);
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        consolePrinter.sendInfo("LightCore plugin has been disabled on Velocity!");

    }

    public void registerChannelRegistrars() {
        server.getChannelRegistrar().register(IDENTIFIER);

        /*
         *   Registering Channel Registrars for LightCore plugin on Velocity side...
         */
        server.getEventManager().register(this, new ReceiveBackendRequest());

        consolePrinter.sendInfo("Registering Channel Registrars for LightCore plugin on Velocity...");
    }


}
