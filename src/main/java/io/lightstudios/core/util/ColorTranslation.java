package io.lightstudios.core.util;

import io.lightstudios.core.LightCore;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorTranslation {

    private final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    /**
     * A method to convert color codes in the input message string to the corresponding ChatColor in Minecraft.
     * Including translating PlaceholderAPI placeholders.
     * @param message the raw input message string
     * @param player the player to translate placeholders for
     * @return the translated message string with color codes converted to ChatColor / PlaceholderAPI placeholders
     */

    public String adventureTranslator(String message, Player player) {
        Component parsed = MiniMessage.miniMessage().deserialize(message);
        return PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes(
                '&', LegacyComponentSerializer.legacyAmpersand().serialize(parsed)));
    }

    /**
     * A method to convert color codes in the input message string to the corresponding ChatColor in Minecraft.
     *
     * @param msg the input message string
     * @return the message string with color codes converted to ChatColor
     */
    public Component universalColor(String msg, Player player) {
        // Check if the server version is within the supported range
        if (Bukkit.getVersion().matches("1\\.1[6-9]|1\\.20")) {
            // Translate alternative hex input ("&#ffdc73 to "#ffdc73")
            if (msg.contains("&#")) {
                msg = msg.replace("&#", "#");
            }

            // Use regular expression to find color codes and replace them with ChatColor.
            Matcher match = pattern.matcher(msg);
            while (match.find()) {
                String color = msg.substring(match.start(), match.end());
                msg = msg.replace(color, String.valueOf(ChatColor.of(color)));
                match = pattern.matcher(msg);
            }
        }

        // Translate '&' color codes to ChatColor
        String legacyColor = ChatColor.translateAlternateColorCodes('&', msg);

        // Use MiniMessage to deserialize the legacy color codes
        return miniMessage(PlaceholderAPI.setPlaceholders(player, legacyColor));
    }

    /**
     * Returns a Component object after deserializing the given message using MiniMessage.
     *
     * @param  message   the message to be deserialized
     * @return          the deserialized Component object
     */
    public Component miniMessage(String message) {
        MiniMessage mm = MiniMessage.miniMessage();
        return mm.deserialize(message);
    }

    public String convertLegacyToMiniMessage(String legacyText) {
        Component component = LegacyComponentSerializer.legacySection().deserialize(legacyText);
        return MiniMessage.miniMessage().serialize(component);
    }

}
