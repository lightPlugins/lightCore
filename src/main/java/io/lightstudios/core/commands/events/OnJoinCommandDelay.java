package io.lightstudios.core.commands.events;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.LightTimers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OnJoinCommandDelay implements Listener {

    public List<UUID> onCooldown = new ArrayList<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {

        if(!LightCore.instance.getSettings().protectionCommandCooldownEnable()) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = event.getPlayer().getUniqueId();

        if(onCooldown.contains(uuid)) {
            LightCore.instance.getMessageSender().sendPlayerMessage(player,
                    LightCore.instance.getMessages().prefix() + LightCore.instance.getMessages().commandCooldown());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {

        if(!LightCore.instance.getSettings().protectionCommandCooldownEnable()) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        long time = LightCore.instance.getSettings().protectionCommandCooldownTime();

        if(onCooldown.contains(uuid)) {
            return;
        }

        onCooldown.add(uuid);
        LightTimers.doSync((task) -> onCooldown.remove(uuid), 20L * time);
    }
}
