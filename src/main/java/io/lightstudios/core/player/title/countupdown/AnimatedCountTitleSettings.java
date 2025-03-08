package io.lightstudios.core.player.title.countupdown;

import lombok.Data;
import net.kyori.adventure.text.Component;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class AnimatedCountTitleSettings {

    private boolean enable;                      // Aktiviert/Deaktiviert die Funktion
    private BigDecimal minAmountTrigger;         // Minimaler Trigger-Betrag (optional)
    private AnimationSettings animation;         // Allgemeine Animationseinstellungen

    @Data
    public static class AnimationSettings {
        private Component upperTitle;            // Oberer Titel
        private Component lowerTitle;            // Unterer Titel
        private Map<Integer, SoundRangeSettings> sounds; // Sounds mit Start-End-Pitch
        private EndAnimationSettings endAnimation; // Einstellungen für das Ende der Animation
    }

    @Data
    public static class SoundRangeSettings {
        private String sound;                    // Soundname
        private double volume;                   // Lautstärke
        private double startPitch;               // Start-Tonhöhe
        private double endPitch;                 // End-Tonhöhe
    }

    @Data
    public static class EndAnimationSettings {
        private Component upperTitle;            // Oberer Titel am Ende der Animation
        private Component lowerTitle;            // Unterer Titel
        private long stayTime;                   // Dauer des Titels in ms
        private long fadeOutTime;                // Fade-Out-Dauer in ms
        private Map<Integer, SimpleSoundSettings> sounds; // Sounds mit einfacher Tonhöhe
    }

    @Data
    public static class SimpleSoundSettings {
        private String sound;                    // Soundname
        private double volume;                   // Lautstärke
        private double pitch;                    // Einheitliche Tonhöhe
    }
}