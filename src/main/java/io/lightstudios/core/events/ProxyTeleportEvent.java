package io.lightstudios.core.events;

import io.lightstudios.core.LightCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class ProxyTeleportEvent implements Listener {

    @EventHandler
    public void onServerJoinEvent(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (LightCore.instance.getTeleportRequests().containsKey(uuid)) {
            player.teleport(LightCore.instance.getTeleportRequests().get(uuid));
            LightCore.instance.getTeleportRequests().remove(uuid);
            player.sendMessage("§aYou have been teleported :)");
        }

    }
}
