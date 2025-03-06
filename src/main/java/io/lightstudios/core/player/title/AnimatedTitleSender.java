package io.lightstudios.core.player.title;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.ColorTranslation;
import io.lightstudios.core.util.LightNumbers;
import io.lightstudios.core.util.LightTimers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public class AnimatedTitleSender extends AbstractTitle {

    private final ColorTranslation colorTranslation;

    // Constructor: Retrieve the ColorTranslation instance from LightCore
    public AnimatedTitleSender() {
        this.colorTranslation = LightCore.instance.getColorTranslation();
    }

    @Override
    public void sendTitle(Player player, Component upperTitle, Component subtitle, Title.Times times) {
        // Standardimplementierung zum Anzeigen eines Titels
        Title title = Title.title(upperTitle, subtitle, times);
        player.showTitle(title);
    }

    /**
     * Sends an animated title with a BigDecimal number that is replaced exponentially - supports Color & PlaceholderAPI.
     *
     * @param player       The player to whom the title is sent.
     * @param upperTitle   The upper title text as a Component with placeholders (e.g., "#counter#").
     * @param lowerTitle   The lower title text as a Component with placeholders (e.g., "#counter#").
     * @param startValue   The start value from which the animation starts.
     * @param target       The target value up to which the animation runs.
     * @param duration     The total duration of the animation in milliseconds.
     */

    public void sendCountUpTitle(Player player, Component upperTitle, Component lowerTitle, BigDecimal startValue, BigDecimal target, long duration) {

        // Sicherstellen, dass der Startwert nicht 0 ist, um Division durch 0 zu vermeiden
        if (startValue.compareTo(BigDecimal.ZERO) == 0) {
            startValue = BigDecimal.valueOf(0.01);
        }

        // Totale Anzahl an Frames (Minecraft-Ticks)
        int totalFrames = 50; // Wir animieren über 50 Frames
        long ticksPerFrame = duration / (totalFrames * 50L); // Dauer in Ticks pro Frame

        // Arrays für startValue und currentValue
        final BigDecimal[] wrappedStartValue = {startValue};
        final BigDecimal[] currentValue = {startValue};

        // Berechnung des Unterschieds zwischen Ziel und Start
        BigDecimal difference = target.subtract(startValue);

        // Verstärkter Effekt für langsames Ende
        double steepnessFactor = 3.7;

        // Minimaler Pitch-Wert
        float startPitch = 0.0f;
        // Maximaler Pitch-Wert
        float endPitch = 1.0f;

        // Start der Animation
        LightTimers.startTaskWithCounter((task, frame) -> {
            if (frame > totalFrames) {
                // Animation statisch beenden, wenn maximale Frames erreicht wurden
                task.cancel();

                // Finalen Titel anzeigen
                Component finalUpperTitle = replaceCounterAndColor(upperTitle, formatNumber(target), player);
                Component finalLowerTitle = replaceCounterAndColor(lowerTitle, formatNumber(target), player);

                sendTitle(player, finalUpperTitle, finalLowerTitle, Title.Times.times(
                        Duration.ZERO, // Startzeitpunkt
                        Duration.ofMillis(duration), // Sichtbarkeitszeit
                        Duration.ofMillis(1000)  // Ausblendzeit
                ));
                return;
            }

            // Normalisierter Fortschritt zwischen 0.0 (Start) und 1.0 (Ende)
            double normalizedFrame = (double) frame / totalFrames;
            // Quadratische Wurzel für sanftes Ansteigen, langsames Ende
            double adjustedFrame = Math.pow(normalizedFrame, steepnessFactor);
            // Ease-In-Ease-Out-Berechnung (S-Kurve)
            double progress = 0.5 * (1 - Math.cos(Math.PI * adjustedFrame));

            // Berechnung des aktuellen Werts basierend auf progress
            BigDecimal interpolation = difference.multiply(BigDecimal.valueOf(progress));
            currentValue[0] = wrappedStartValue[0].add(interpolation).setScale(2, RoundingMode.HALF_UP);

            // Sicherstellen, dass der Wert nicht das Ziel überschreitet
            if (currentValue[0].compareTo(target) > 0) {
                currentValue[0] = target;
            }

            // Aktuellen Wert in das passende String-Format umwandeln
            String currentCounter = LightNumbers.formatForMessages(currentValue[0], 2);

            // Komponenten mit Platzhaltern ersetzen
            Component dynamicUpperTitle = replaceCounterAndColor(upperTitle, currentCounter, player);
            Component dynamicLowerTitle = replaceCounterAndColor(lowerTitle, currentCounter, player);

            // Titel aktualisieren
            sendTitle(player, dynamicUpperTitle, dynamicLowerTitle, Title.Times.times(
                    Duration.ZERO, Duration.ofMillis(duration), Duration.ZERO
            ));

            // Dynamischen Pitch berechnen (nur steigend)
            float pitch = startPitch + (float) (progress * (endPitch - startPitch));

            // Sound-Frequenz dynamisch anpassen (am Anfang langsamer, am Ende schneller)
            double soundFrequency = (1.0 - normalizedFrame) * totalFrames / 10.0;  // Frequenz sinkt gegen Ende
            if (frame % Math.max(1, (int) soundFrequency) == 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.4f, pitch);
                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.2f, pitch);
            }

        }, 0L, ticksPerFrame); // Beginne sofort bei 0 Ticks
    }

    /**
     * Sends an animated title with a BigDecimal number that counts down exponentially - supports Color & PlaceholderAPI.
     *
     * @param player       The player to whom the title is sent.
     * @param upperTitle   The upper title text as a Component with placeholders (e.g., "#counter#").
     * @param lowerTitle   The lower title text as a Component with placeholders (e.g., "#counter#").
     * @param startValue   The start value from which the animation starts.
     * @param target       The target value to which the animation counts down.
     * @param duration     The total duration of the animation in milliseconds.
     */
    public void sendCountDownTitle(Player player, Component upperTitle, Component lowerTitle, BigDecimal startValue, BigDecimal target, long duration) {

        // Sicherstellen, dass der Startwert größer als der Zielwert ist
        if (startValue.compareTo(target) <= 0) {
            throw new IllegalArgumentException("Startwert muss größer als Zielwert sein, um herunterzuzählen.");
        }

        // Totale Anzahl an Frames (Minecraft-Ticks)
        int totalFrames = 50; // Wir animieren über 50 Frames
        long ticksPerFrame = duration / (totalFrames * 50L); // Dauer in Ticks pro Frame

        // Arrays für startValue und currentValue
        final BigDecimal[] wrappedStartValue = {startValue};
        final BigDecimal[] currentValue = {startValue};

        // Berechnung des Unterschieds zwischen Ziel und Start
        BigDecimal difference = startValue.subtract(target);

        // Verstärkter Effekt für langsames Ende
        double steepnessFactor = 3.7;

        // Minimale und maximale Tonhöhenwerte
        float startPitch = 1.0f; // Hohe Tonhöhe zu Beginn
        float endPitch = 0.0f; // Niedrigere Tonhöhe am Ende

        // Start der Animation
        LightTimers.startTaskWithCounter((task, frame) -> {
            if (frame > totalFrames) {
                // Animation statisch beenden, wenn maximale Frames erreicht wurden
                task.cancel();

                // Finalen Titel anzeigen
                Component finalUpperTitle = replaceCounterAndColor(upperTitle, formatNumber(target), player);
                Component finalLowerTitle = replaceCounterAndColor(lowerTitle, formatNumber(target), player);

                sendTitle(player, finalUpperTitle, finalLowerTitle, Title.Times.times(
                        Duration.ZERO, // Startzeitpunkt
                        Duration.ofMillis(duration), // Sichtbarkeitszeit
                        Duration.ofMillis(1000)  // Ausblendzeit
                ));
                return;
            }

            // Normalisierter Fortschritt zwischen 0.0 (Start) und 1.0 (Ende)
            double normalizedFrame = (double) frame / totalFrames;
            // Quadratische Wurzel für sanftes Ansteigen, langsames Ende
            double adjustedFrame = Math.pow(normalizedFrame, steepnessFactor);
            // Ease-In-Ease-Out-Berechnung (S-Kurve)
            double progress = 0.5 * (1 - Math.cos(Math.PI * adjustedFrame));

            // Berechnung des aktuellen Werts basierend auf progress
            BigDecimal interpolation = difference.multiply(BigDecimal.valueOf(progress));
            currentValue[0] = wrappedStartValue[0].subtract(interpolation).setScale(2, RoundingMode.HALF_UP);

            // Sicherstellen, dass der Wert nicht das Ziel unterschreitet
            if (currentValue[0].compareTo(target) < 0) {
                currentValue[0] = target;
            }

            // Aktuellen Wert in das passende String-Format umwandeln
            String currentCounter = LightNumbers.formatForMessages(currentValue[0], 2);

            // Komponenten mit Platzhaltern ersetzen
            Component dynamicUpperTitle = replaceCounterAndColor(upperTitle, currentCounter, player);
            Component dynamicLowerTitle = replaceCounterAndColor(lowerTitle, currentCounter, player);

            // Titel aktualisieren
            sendTitle(player, dynamicUpperTitle, dynamicLowerTitle, Title.Times.times(
                    Duration.ZERO, Duration.ofMillis(duration), Duration.ZERO
            ));

            // Dynamischen Pitch berechnen (hier sinkend)
            float pitch = startPitch - (float) (progress * (startPitch - endPitch));

            // Sound-Frequenz dynamisch anpassen (am Anfang schneller, gegen Ende langsamer)
            double soundFrequency = normalizedFrame * totalFrames / 10.0;
            if (frame % Math.max(1, (int) soundFrequency) == 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.4f, pitch);
                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.2f, pitch);
            }

        }, 0L, ticksPerFrame); // Beginne sofort bei 0 Ticks
    }



    /**
     * Formats a BigDecimal number to 2 decimal places and returns it as a String.
     *
     * @param value The BigDecimal number to be formatted.
     * @return The formatted value as a String.
     */

    private String formatNumber(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }


    /**
     * Ersetzt den Platzhalter "#counter#" in einer Component und übersetzt gleichzeitig Farben und PlaceholderAPI-Daten.
     *
     * @param component     Die ursprüngliche Component mit Stil und Farbe.
     * @param replacement   Der Text, der den Platzhalter "#counter#" ersetzen soll.
     * @param player        Der Spieler, für den die Farben und Platzhalter aufgelöst werden sollen.
     * @return Die neue Component mit dem ersetzten Text, Farben und Auswertung der PlaceholderAPI.
     */
    private Component replaceCounterAndColor(Component component, String replacement, Player player) {
        // Verwende den PlainTextComponentSerializer, um den reinen Text des Components zu extrahieren
        String originalText = PlainTextComponentSerializer.plainText().serialize(component);

        // Ersetze den Platzhalter "#counter#" im Text
        String updatedText = originalText.replace("#counter#", replacement);

        // Färbe den Text und löse PlaceholderAPI auf
        return colorTranslation.universalColor(updatedText, player);
    }

}