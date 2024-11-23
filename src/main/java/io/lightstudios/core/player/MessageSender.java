package io.lightstudios.core.player;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.LightTimers;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
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
        Component component = LightCore.instance.getColorTranslation().universalColor(rawMessage, player);

        // send synchronously a message to the specified player
        LightTimers.doSync(task -> { audience.sendMessage(component); }, 0L);
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
            Component component = LightCore.instance.getColorTranslation().universalColor(message, player);
            // send synchronously a message to the specified player
            LightTimers.doSync(task -> { audience.sendMessage(component); }, 0L);
        }
    }

}
