package io.lightstudios.core.player.title;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.ColorTranslation;
import io.lightstudios.core.util.LightTimers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public class AnimatedTitleSender extends AbstractTitle {

    private final ColorTranslation colorTranslation;

    // Konstruktor: Hole die ColorTranslation-Instanz aus LightCore
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
     * Sendet einen animierten Titel mit einer BigDecimal-Zahl, die exponentiell ersetzt wird - unterstützt Color & PlaceholderAPI.
     *
     * @param player       Der Spieler, an den der Titel gesendet wird.
     * @param upperTitle   Der obere Titeltext als Component mit Platzhalter (z. B. "#counter#").
     * @param lowerTitle   Der untere Titeltext als Component mit Platzhalter (z. B. "#counter#").
     * @param startValue   Der Startwert, von dem aus animiert wird.
     * @param target       Der Zielwert, bis zu dem animiert wird.
     * @param duration     Die Gesamtdauer der Animation in Millisekunden.
     */
    public void sendCountUpTitle(Player player, Component upperTitle, Component lowerTitle, BigDecimal startValue, BigDecimal target, long duration) {
        // Totale Anzahl an Frames für die Animation (je höher, desto flüssiger)
        int totalFrames = 50;

        // Zeit pro Frame in Ticks
        long ticksPerFrame = (duration / totalFrames) / 50;

        // Initialer Wert: Der Wert, von dem gezählt wird (nach oben)
        BigDecimal[] currentValue = {startValue};

        // Wachstumsfaktor für lineare Animation
        BigDecimal incrementFactor = target.subtract(startValue).divide(new BigDecimal(totalFrames), RoundingMode.HALF_UP);

        // Startet die Animation mit einem Zähler
        LightTimers.startTaskWithCounter((task, frame) -> {
            if (frame > totalFrames) {
                // Animation beenden, wenn die maximale Anzahl an Frames erreicht wurde
                task.cancel();
                // Sicherstellen, dass der finale Zielwert angezeigt wird
                Title.Times times = Title.Times.times(
                        Duration.ZERO, Duration.ofMillis(duration), Duration.ZERO);

                // Finale Werte für Counter vorbereiten
                String finalCounter = target.setScale(2, RoundingMode.HALF_UP).toPlainString();

                // Ersetze den Platzhalter final mit dem Zielwert und färbe die Komponenten
                Component finalUpperTitle = replaceCounterAndColor(upperTitle, finalCounter, player);
                Component finalLowerTitle = replaceCounterAndColor(lowerTitle, finalCounter, player);

                sendTitle(player, finalUpperTitle, finalLowerTitle, times);
                return;
            }

            // Berechne den aktuellen Wert
            currentValue[0] = currentValue[0].add(incrementFactor);

            // Sicherstellen, dass der aktuelle Wert nicht das Ziel überschreitet
            if (currentValue[0].compareTo(target) > 0) {
                currentValue[0] = target;
            }

            // Aktuellen Wert als String für den Platzhalter
            String currentCounter = currentValue[0].setScale(2, RoundingMode.HALF_UP).toPlainString();

            // Ersetze den Platzhalter "#counter#" im oberen und unteren Titel und färbe die Komponenten
            Component dynamicUpperTitle = replaceCounterAndColor(upperTitle, currentCounter, player);
            Component dynamicLowerTitle = replaceCounterAndColor(lowerTitle, currentCounter, player);

            // Titel im Hauptthread aktualisieren
            Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofMillis(duration), Duration.ZERO);
            sendTitle(player, dynamicUpperTitle, dynamicLowerTitle, times);

        }, 0L, ticksPerFrame); // Startet sofort mit dem angegebenen Takt
    }


    public void sendCountDownTitle(Player player, Component upperTitle, Component lowerTitle, BigDecimal startValue, BigDecimal target, long duration) {
        // Totale Anzahl an Frames für die Animation (je höher, desto flüssiger)
        int totalFrames = 50;

        // Zeit pro Frame in Ticks
        long ticksPerFrame = (duration / totalFrames) / 50;

        // Initialer Wert: Der Wert, von dem gezählt wird (nach unten)
        BigDecimal[] currentValue = {startValue};

        // Wachstumsfaktor für lineare Animation
        BigDecimal decrementFactor = startValue.subtract(target).divide(new BigDecimal(totalFrames), RoundingMode.HALF_UP);

        // Startet die Animation mit einem Zähler
        LightTimers.startTaskWithCounter((task, frame) -> {
            if (frame > totalFrames) {
                // Animation beenden, wenn die maximale Anzahl an Frames erreicht wurde
                task.cancel();
                // Sicherstellen, dass der finale Zielwert angezeigt wird
                Title.Times times = Title.Times.times(
                        Duration.ZERO, Duration.ofMillis(duration), Duration.ZERO);

                // Finale Werte für Counter vorbereiten
                String finalCounter = target.setScale(2, RoundingMode.HALF_UP).toPlainString();

                // Ersetze den Platzhalter final mit dem Zielwert und färbe die Komponenten
                Component finalUpperTitle = replaceCounterAndColor(upperTitle, finalCounter, player);
                Component finalLowerTitle = replaceCounterAndColor(lowerTitle, finalCounter, player);

                sendTitle(player, finalUpperTitle, finalLowerTitle, times);
                return;
            }

            // Berechne den aktuellen Wert
            currentValue[0] = currentValue[0].subtract(decrementFactor);

            // Sicherstellen, dass der aktuelle Wert nicht unter das Ziel fällt
            if (currentValue[0].compareTo(target) < 0) {
                currentValue[0] = target;
            }

            // Aktuellen Wert als String für den Platzhalter
            String currentCounter = currentValue[0].setScale(2, RoundingMode.HALF_UP).toPlainString();

            // Ersetze den Platzhalter "#counter#" im oberen und unteren Titel und färbe die Komponenten
            Component dynamicUpperTitle = replaceCounterAndColor(upperTitle, currentCounter, player);
            Component dynamicLowerTitle = replaceCounterAndColor(lowerTitle, currentCounter, player);

            // Titel im Hauptthread aktualisieren
            Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofMillis(duration), Duration.ZERO);
            sendTitle(player, dynamicUpperTitle, dynamicLowerTitle, times);

        }, 0L, ticksPerFrame); // Startet sofort mit dem angegebenen Takt
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
        // Serialisiere den ursprünglichen Text
        String originalText = component.toString();

        // Ersetze den Platzhalter "#counter#" im Text
        String updatedText = originalText.replace("#counter#", replacement);

        // Färbe den Text und löse PlaceholderAPI auf
        return colorTranslation.universalColor(updatedText, player);
    }
}