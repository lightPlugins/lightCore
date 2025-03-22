package io.lightstudios.core.player.title.countupdown;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.player.title.AbstractCountTitle;
import io.lightstudios.core.util.ColorTranslation;
import io.lightstudios.core.util.LightTimers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public class AnimatedCountTitle extends AbstractCountTitle {

    private final ColorTranslation colorTranslation;

    // Constructor: Retrieve the ColorTranslation instance from LightCore
    public AnimatedCountTitle() {
        this.colorTranslation = LightCore.instance.getColorTranslation();
    }

    @Override
    public void sendTitle(Player player, Component upperTitle, Component subtitle, Title.Times times) {
        // Standardimplementierung zum Anzeigen eines Titels
        Title title = Title.title(upperTitle, subtitle, times);
        player.showTitle(title);
    }

    /**
     * Sends an animated title with a BigDecimal number that counts up.
     * Settings and parameters are retrieved from AnimatedCountTitleSettings.
     *
     * @param player     The player to whom the title is sent.
     * @param startValue The start value from which the animation starts.
     * @param target     The target value up to which the animation runs.
     * @param duration   The total duration of the animation in milliseconds.
     * @param settings   The AnimatedCountTitleSettings providing configuration.
     */
    public void sendCountUpTitle(
            Player player,
            BigDecimal startValue,
            BigDecimal target,
            long duration,
            AnimatedCountTitleSettings settings) {

        // Prüfen, ob die Funktion aktiviert ist und die Differenz groß genug ist
        if (!settings.isEnable() || target.subtract(startValue).compareTo(settings.getMinAmountTrigger()) < 0) {
            return;
        }

        // Sicherstellen, dass der Startwert nicht null oder kleiner als 0 ist
        if (startValue.compareTo(BigDecimal.ZERO) <= 0) {
            startValue = BigDecimal.valueOf(0.01);
        }

        // Konfigurationswerte abrufen
        AnimatedCountTitleSettings.AnimationSettings animation = settings.getAnimation();
        if (animation == null) {
            throw new IllegalArgumentException("AnimationSettings cannot be null");
        }

        // Unterschied zwischen Ziel und Start berechnen
        BigDecimal difference = target.subtract(startValue);
        final BigDecimal initialValue = startValue; // Damit startValue effektiv final ist

        // Wrapper für startValue, um innerhalb des Lambdas zu modifizieren
        final BigDecimal[] currentValue = {startValue};

        // Berechnung der Ticks pro Frame anhand der Dauer
        int totalFrames = 50; // Anzahl der Frames (z. B. 50 Ticks)
        long ticksPerFrame = duration / (totalFrames * 50L); // Ticks zwischen Updates

        // Animation starten
        LightTimers.startTaskWithCounter((task, frame) -> {
            // Beenden, wenn maximale Frameanzahl überschritten ist
            if (frame > totalFrames) {
                task.cancel();

                // Endanimation anzeigen, falls vorhanden
                AnimatedCountTitleSettings.EndAnimationSettings endAnimation = animation.getEndAnimation();
                if (endAnimation != null) {
                    // Finalen Titel vorbereiten
                    Component finalUpperTitle =
                            replaceCounterAndColor(endAnimation.getUpperTitle(), formatNumber(target), player);
                    Component finalLowerTitle =
                            replaceCounterAndColor(endAnimation.getLowerTitle(), formatNumber(target), player);

                    sendTitle(player, finalUpperTitle, finalLowerTitle, Title.Times.times(
                            Duration.ZERO,
                            Duration.ofMillis(endAnimation.getStayTime()),
                            Duration.ofMillis(endAnimation.getFadeOutTime())
                    ));

                    // Endanimation-Sounds abspielen
                    if (endAnimation.getSounds() != null) {
                        endAnimation.getSounds().values().forEach(sound -> {
                            player.playSound(
                                    player.getLocation(),
                                    sound.getSound(),
                                    (float) sound.getVolume(),
                                    (float) sound.getPitch());
                        });
                    }
                }
                return;
            }

            // Fortschritt zwischen 0.0 und 1.0 berechnen
            double normalizedFrame = (double) frame / totalFrames;
            double adjustedFrame = Math.pow(normalizedFrame, 3.7); // Verstärkter Effekt für sanftes Ende
            double progress = 0.5 * (1 - Math.cos(Math.PI * adjustedFrame)); // Ease-In/Ease-Out (S-Kurve)

            // Aktuellen Wert berechnen
            BigDecimal interpolation = difference.multiply(BigDecimal.valueOf(progress));
            currentValue[0] = initialValue.add(interpolation).setScale(2, RoundingMode.HALF_UP);

            // Aktuellen Wert nicht über das Ziel hinaus erhöhen
            if (currentValue[0].compareTo(target) > 0) {
                currentValue[0] = target;
            }

            // Zahlenwert formatieren und Platzhalter ersetzen
            String currentCounter = formatNumber(currentValue[0]);
            Component dynamicUpperTitle = replaceCounterAndColor(animation.getUpperTitle(), currentCounter, player);
            Component dynamicLowerTitle = replaceCounterAndColor(animation.getLowerTitle(), currentCounter, player);

            // Titel aktualisieren
            sendTitle(player, dynamicUpperTitle, dynamicLowerTitle, Title.Times.times(
                    Duration.ZERO, Duration.ofMillis(duration), Duration.ZERO
            ));

            // Dynamischen Pitch für Sounds berechnen
            float startPitch = 0.0f;
            float endPitch = 1.0f;
            float pitch = startPitch + (float) (progress * (endPitch - startPitch));

            // Abspielen der Sounds zur aktuellen Animation
            if (animation.getSounds() != null && animation.getSounds().containsKey(frame)) {
                AnimatedCountTitleSettings.SoundRangeSettings soundSettings = animation.getSounds().get(frame);
                player.playSound(
                        player.getLocation(),
                        soundSettings.getSound(),
                        (float) soundSettings.getVolume(),
                        pitch);
            }

        }, 0L, ticksPerFrame);
    }



    /**
     * Sends an animated title with a BigDecimal number that counts down.
     * Uses LightTimers for scheduling and AnimatedCountTitleSettings for configurations.
     *
     * @param player     The player to whom the title is sent.
     * @param startValue The start value from which the animation starts.
     * @param target     The target value to which the animation counts down.
     * @param duration   The total duration of the animation in milliseconds.
     * @param settings   The AnimatedCountTitleSettings providing configuration.
     */
    public void sendCountDownTitle(
            Player player,
            BigDecimal startValue,
            BigDecimal target,
            long duration,
            AnimatedCountTitleSettings settings) {

        // Prüfen, ob die Funktion aktiviert ist und ob der Unterschied den Mindestwert überschreitet
        if (!settings.isEnable() || startValue.subtract(target).compareTo(settings.getMinAmountTrigger()) < 0) {
            return;
        }

        // Sicherstellen, dass der Startwert nicht null oder kleiner als das Ziel ist
        if (startValue.compareTo(BigDecimal.ZERO) <= 0) {
            startValue = BigDecimal.valueOf(0.01);
        }

        // Konfigurationswerte abrufen
        AnimatedCountTitleSettings.AnimationSettings animation = settings.getAnimation();
        if (animation == null) {
            throw new IllegalArgumentException("AnimationSettings cannot be null");
        }

        // Unterschied zwischen Start- und Zielwert
        final BigDecimal initialValue = startValue; // Damit startValue effektiv final ist
        BigDecimal difference = startValue.subtract(target);

        // Änderung für die Berechnung: Verwende einen finalisierten lokalen Wrap-Wert
        final BigDecimal[] currentValue = {startValue};

        // Berechnung der Ticks pro Frame anhand der Dauer
        int totalFrames = 50; // Anzahl der Frames (z. B. 50 Ticks)
        long ticksPerFrame = duration / (totalFrames * 50L); // Ticks zwischen Updates

        // Animation starten
        LightTimers.startTaskWithCounter((task, frame) -> {
            // Frame-Limit prüfen
            if (frame > totalFrames) {
                task.cancel();

                // Endanimation anzeigen, falls vorhanden
                AnimatedCountTitleSettings.EndAnimationSettings endAnimation = animation.getEndAnimation();
                if (endAnimation != null) {
                    // Finalen Titel vorbereiten
                    Component finalUpperTitle =
                            replaceCounterAndColor(endAnimation.getUpperTitle(), formatNumber(target), player);
                    Component finalLowerTitle =
                            replaceCounterAndColor(endAnimation.getLowerTitle(), formatNumber(target), player);

                    sendTitle(player, finalUpperTitle, finalLowerTitle, Title.Times.times(
                            Duration.ZERO,
                            Duration.ofMillis(endAnimation.getStayTime()),
                            Duration.ofMillis(endAnimation.getFadeOutTime())
                    ));

                    // Sounds der Endanimation abspielen
                    if (endAnimation.getSounds() != null) {
                        endAnimation.getSounds().values().forEach(sound -> {
                            player.playSound(
                                    player.getLocation(),
                                    sound.getSound(),
                                    (float) sound.getVolume(),
                                    (float) sound.getPitch());
                        });
                    }
                }
                return;
            }

            // Fortschritt zwischen 0.0 und 1.0 berechnen
            double normalizedFrame = (double) frame / totalFrames;
            double adjustedFrame = Math.pow(normalizedFrame, 3.7); // Verstärkter Effekt für sanftes Ende
            double progress = 0.5 * (1 - Math.cos(Math.PI * adjustedFrame)); // Ease-in/Ease-out (S-Kurve)

            // Berechnung des aktuellen Werts basierend auf progress
            BigDecimal interpolation = difference.multiply(BigDecimal.valueOf(progress));
            currentValue[0] = initialValue.subtract(interpolation).setScale(2, RoundingMode.HALF_UP);

            // Sicherstellen, dass der aktuelle Wert nicht kleiner als das Ziel ist
            if (currentValue[0].compareTo(target) < 0) {
                currentValue[0] = target;
            }

            // Zahlenwert formatieren und durch Platzhalter im Titel ersetzen
            String currentCounter = formatNumber(currentValue[0]);
            Component dynamicUpperTitle = replaceCounterAndColor(animation.getUpperTitle(), currentCounter, player);
            Component dynamicLowerTitle = replaceCounterAndColor(animation.getLowerTitle(), currentCounter, player);

            // Titel aktualisieren
            sendTitle(player, dynamicUpperTitle, dynamicLowerTitle, Title.Times.times(
                    Duration.ZERO, Duration.ofMillis(duration), Duration.ZERO
            ));

            // Dynamischen Pitch für den Sound berechnen
            float startPitch = 0.0f;
            float endPitch = 1.0f;
            float pitch = startPitch + (float) (progress * (endPitch - startPitch));

            // Abspielen der Sounds zur aktuellen Animation
            if (animation.getSounds() != null && animation.getSounds().containsKey(frame)) {
                AnimatedCountTitleSettings.SoundRangeSettings soundSettings = animation.getSounds().get(frame);
                player.playSound(
                        player.getLocation(),
                        soundSettings.getSound(),
                        (float) soundSettings.getVolume(),
                        pitch);
            }

        }, 0L, ticksPerFrame);
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

    /**
     * Interpolates the sound pitch based on the progress of the animation.
     *
     * @param settings The SoundRangeSettings for the sound.
     * @param ticks    The current tick.
     * @param duration The total duration of the animation in milliseconds.
     * @return The interpolated pitch for the current step.
     */
    private double interpolateSoundPitch(AnimatedCountTitleSettings.SoundRangeSettings settings, long ticks, long duration) {
        double progress = (double) (ticks * 50) / duration; // Progress between 0 and 1
        return settings.getStartPitch() + progress * (settings.getEndPitch() - settings.getStartPitch());
    }

}