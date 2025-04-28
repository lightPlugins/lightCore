package io.lightstudios.core.player;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.proxy.messaging.backend.sender.SendProxyRequest;
import io.lightstudios.core.util.LightTimers;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PlayerPunishment {

    /**
     * Kick a player from the server
     * If exists a proxy server, the player will be kicked from the proxy server
     * @param player The player to kick
     * @param reason The reason for the kick
     */
    public void autoKickPlayer(Player player, String reason) {
        SendProxyRequest.kickPlayerFromProxy(player, reason);

        Component component = LightCore.instance.getColorTranslation().universalColor(reason, player);

        LightTimers.doSync(task -> {
            player.kick(component);
        }, 10L);
    }
}
