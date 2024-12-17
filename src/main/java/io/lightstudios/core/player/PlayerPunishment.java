package io.lightstudios.core.player;

import io.lightstudios.core.proxy.messaging.backend.send.SendProxyKickRequest;
import io.lightstudios.core.util.LightTimers;
import org.bukkit.entity.Player;

public class PlayerPunishment {

    /**
     * Kick a player from the server
     * If exists a proxy server, the player will be kicked from the proxy server
     * @param player The player to kick
     * @param reason The reason for the kick
     */
    public void autoKickPlayer(Player player, String reason) {
        SendProxyKickRequest.sendProxyPing(player, reason);

        LightTimers.doSync(task -> {
            player.kickPlayer("SERVER-KICK\n" + reason);
        }, 10L);
    }
}
