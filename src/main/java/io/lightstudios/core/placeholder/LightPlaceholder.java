package io.lightstudios.core.placeholder;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public interface LightPlaceholder {
    String onRequest(OfflinePlayer player, @NotNull String params);
}
