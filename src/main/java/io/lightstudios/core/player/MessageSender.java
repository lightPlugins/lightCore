package io.lightstudios.core.player;

import io.lightstudios.core.LightCore;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageSender {

    /**
     * Send a message to a player (synchronously)
     * @param player the player to send the message to
     * @param rawMessage the raw message from the config
     */
    public void sendPlayerMessage(Player player, String rawMessage) {
        // convert a Player into an Audience
        Audience audience = (Audience) player;
        if(audience == null) {
            LightCore.instance.getConsolePrinter().printError("Cannot resolve Player as Audience");
            return;
        }
        // send synchronously a message to the specified player
        Bukkit.getScheduler().runTask(LightCore.instance, () -> {
            Component component = LightCore.instance.getColorTranslation().universalColor(rawMessage, player);
            audience.sendMessage(component);
        });
    }

    /**
     * Send a list of messages to a player (synchronously)
     * @param player the player to send the messages to
     * @param rawMessages the list of raw messages from the config
     */
    public void sendPlayerMessage(Player player, List<String> rawMessages) {
        // convert a Player into an Audience
        Audience audience = (Audience) player;
        if(audience == null) {
            LightCore.instance.getConsolePrinter().printError("Cannot resolve Player as Audience");
            return;
        }

        for(String message : rawMessages) {
            // send synchronously a message to the specified player
            Bukkit.getScheduler().runTask(LightCore.instance, () -> {
                Component component = LightCore.instance.getColorTranslation().universalColor(message, player);
                audience.sendMessage(component);
            });
        }
    }

}
