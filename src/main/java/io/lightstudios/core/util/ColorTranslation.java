package io.lightstudios.core.util;

import io.lightstudios.core.LightCore;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
     * Übersetzt ein Component farblich und verwendet PlaceholderAPI, um es an einen Spieler anzupassen.
     *
     * @param inputComponent Das ursprüngliche Component, das übersetzt werden soll
     * @param player         Der Spieler, für den PlaceholderAPI angewendet wird
     * @return Das übersetzte Component
     */
    public Component translateComponent(Component inputComponent, Player player) {
        // Serialisiere das ursprüngliche Component in einen Legacy-Text
        String legacyText = PlainTextComponentSerializer.plainText().serialize(inputComponent);

        // Übersetze PlaceholderAPI (falls verfügbar)
        String translatedWithPlaceholders = PlaceholderAPI.setPlaceholders(player, legacyText);

        // Wandle die Legacy-Farbcodes (&) in ein Component mithilfe von MiniMessage um
        Component component = MiniMessage.miniMessage().deserialize(translatedWithPlaceholders);

        // Setze standardmäßig "italic" auf false
        return component.decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Übersetzt eine Liste von Components farblich und verwendet PlaceholderAPI, um sie an einen Spieler anzupassen.
     *
     * @param inputComponents Die ursprüngliche Liste von Components
     * @param player          Der Spieler, für den PlaceholderAPI angewendet wird
     * @return Die übersetzte Liste von Components
     */
    public List<Component> translateComponents(List<Component> inputComponents, Player player) {
        return inputComponents.stream()
                .map(component -> {
                    // Serialize the component to a plain text string
                    String plainText = PlainTextComponentSerializer.plainText().serialize(component);

                    // Apply PlaceholderAPI to replace placeholders
                    String translatedText = PlaceholderAPI.setPlaceholders(player, plainText);

                    // Deserialize the translated text back into a Component
                    Component translatedComponent = MiniMessage.miniMessage().deserialize(translatedText);

                    // Ensure "italic" decoration is set to false
                    return translatedComponent.decoration(TextDecoration.ITALIC, false);
                })
                .collect(Collectors.toList());
    }

    /**
     * Übersetzt eine Liste von Components farblich, verwendet PlaceholderAPI und ersetzt benutzerdefinierte Platzhalter.
     *
     * @param inputComponents Die ursprüngliche Liste von Components
     * @param player          Der Spieler, für den PlaceholderAPI angewendet wird
     * @param replacements    Eine Map, die benutzerdefinierte Platzhalter (Schlüssel) und deren Ersatzwerte (Werte) enthält
     * @return Die übersetzte Liste von Components
     */
    public List<Component> translateComponents(List<Component> inputComponents, Player player, Map<String, String> replacements) {
        return inputComponents.stream()
                .map(component -> {
                    // Serialize the component to a plain text string
                    // String plainText = PlainTextComponentSerializer.plainText().serialize(component);
                    String plainText = PlainTextComponentSerializer.plainText().serialize(component);

                    // Apply PlaceholderAPI to replace placeholders
                    String translatedText = PlaceholderAPI.setPlaceholders(player, plainText);

                    // Replace custom placeholders
                    for (Map.Entry<String, String> entry : replacements.entrySet()) {
                        String placeholder = entry.getKey();
                        String replacement = entry.getValue();
                        translatedText = translatedText.replace(placeholder, replacement);
                    }

                    // Deserialize the translated text back into a Component
                    Component translatedComponent = MiniMessage.miniMessage().deserialize(translatedText);

                    // Ensure "italic" decoration is set to false
                    return translatedComponent.decoration(TextDecoration.ITALIC, false);
                })
                .collect(Collectors.toList());
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

    /**
     * Übersetzt ein einzelnes Component farblich, verwendet PlaceholderAPI und ersetzt benutzerdefinierte Platzhalter.
     *
     * @param inputComponent Das ursprüngliche Component
     * @param player         Der Spieler, für den PlaceholderAPI angewendet wird
     * @param replacements   Eine Map mit benutzerdefinierten Platzhaltern und deren Ersatzwerten
     * @return Das übersetzte Component
     */
    public Component translateComponentWithReplacements(Component inputComponent, Player player, Map<String, String> replacements) {
        // Serialize the original Component to a MiniMessage-compatible string
        String miniMessageString = PlainTextComponentSerializer.plainText().serialize(inputComponent);

        // Apply PlaceholderAPI to replace placeholders
        String translatedWithPlaceholders = PlaceholderAPI.setPlaceholders(player, miniMessageString);

        // Replace custom placeholders
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String placeholder = entry.getKey();
            String replacement = entry.getValue();
            translatedWithPlaceholders = translatedWithPlaceholders.replace(placeholder, replacement);
        }

        // Deserialize the final string back into a Component
        Component component = MiniMessage.miniMessage().deserialize(translatedWithPlaceholders);

        // Set "italic" decoration to false
        return component.decoration(TextDecoration.ITALIC, false);
    }
}
