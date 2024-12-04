package io.lightstudios.core;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import io.lightstudios.core.util.ProxyConsolePrinter;
import lombok.Getter;

import java.nio.file.Path;

@Getter
@Plugin(id = "lightcore", name = "LightCore", version = "0.1.7",
        url = "https://example.org", description = "Core Plugin of LightStudios", authors = {"LightStudios"})
public class LightCoreProxy {

    private final ProxyServer server;
    private final Path dataDirectory;

    public static LightCoreProxy instance;
    private final ProxyConsolePrinter consolePrinter;

    public static final MinecraftChannelIdentifier IDENTIFIER =
            MinecraftChannelIdentifier.from("lightcore:lightstudios");

    public LightCoreProxy(ProxyServer server, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.dataDirectory = dataDirectory;
        this.instance = this;
        this.consolePrinter = new ProxyConsolePrinter();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        consolePrinter.sendInfo("Registering LightCore plugin on Velocity...");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        consolePrinter.sendInfo("LightCore plugin has been disabled on Velocity!");

    }

    public void registerChannelRegistrars() {
        server.getChannelRegistrar().register(MinecraftChannelIdentifier.create("lightcore", "teleport"));

    }


}
