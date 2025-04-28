package io.lightstudios.core.proxy.messaging.backend.receiver;

import io.lightstudios.core.util.interfaces.LightMessageListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;


public class ReceiveProxyRequest implements PluginMessageListener {

    private final ArrayList<LightMessageListener> listeners = new ArrayList<>();

    public ReceiveProxyRequest() {
        listeners.addAll(Arrays.asList(
                new BalanceUpdateRequest(),
                new PlayerTeleportRequest()
        ));
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte [] bytes) {
        for (LightMessageListener listener : listeners) {
            listener.onPluginMessageReceived(channel, player, bytes);
        }
    }
}
