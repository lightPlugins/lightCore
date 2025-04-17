package io.lightstudios.core.progression.level.events;

import io.lightstudios.core.progression.level.LightLevelManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class OnPlayerJoin implements Listener {

    private final LightLevelManager lightLevelManager;

    public OnPlayerJoin(LightLevelManager lightLevelManager) {
        this.lightLevelManager = lightLevelManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        // Füge den Spieler in alle LightLevels ein
        lightLevelManager.addPlayerWithAllLightLevels(playerUUID);
    }
}
