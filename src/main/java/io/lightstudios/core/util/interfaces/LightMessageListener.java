package io.lightstudios.core.util.interfaces;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface LightMessageListener {

    void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] bytes);
}
