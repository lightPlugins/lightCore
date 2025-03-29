package io.lightstudios.core.player.title;

import io.lightstudios.core.LightCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

public abstract class AbstractCountTitle {

    // Default times for fadeIn, stay, and fadeOut
    protected Title.Times defaultTimes = Title.Times.times(
            Duration.ofMillis(1000), // fadeIn: 1 second
            Duration.ofMillis(3000), // stay: 3 seconds
            Duration.ofMillis(1000)  // fadeOut: 1 second
    );

    /**
     * Abstract method: This must be implemented by subclasses to define
     * how the title is sent to the player.
     *
     * @param player      The player to whom the title is sent.
     * @param upperTitle  The upper title as a Component.
     * @param subtitle    The subtitle as a Component.
     * @param times       The timings for fadeIn, stay, and fadeOut.
     */
    public abstract void sendTitle(Player player, Component upperTitle, Component subtitle, Title.Times times);

    /**
     * Helper method: Sends a title with default times.
     *
     * @param player      The player to whom the title is sent.
     * @param upperTitle  The upper title as a String.
     * @param subtitle    The subtitle as a String.
     */
    public void sendTitle(Player player, String upperTitle, String subtitle) {

        Component upperTitleComp = LightCore.instance.getColorTranslation().universalColor(upperTitle, player);
        Component subtitleComp = LightCore.instance.getColorTranslation().universalColor(subtitle, player);

        sendTitle(player,
                upperTitleComp,
                subtitleComp,
                defaultTimes);
    }

    /**
     * Helper method: Sends a title with custom timings in milliseconds.
     *
     * @param player      The player to whom the title is sent.
     * @param upperTitle  The upper title as a String.
     * @param subtitle    The subtitle as a String.
     * @param fadeIn      The fade-in duration in milliseconds.
     * @param stay        The display time in milliseconds.
     * @param fadeOut     The fade-out duration in milliseconds.
     */
    public void sendTitle(Player player, String upperTitle, String subtitle, long fadeIn, long stay, long fadeOut) {
        // Create Title.Times instance for custom durations
        Title.Times times = Title.Times.times(
                Duration.ofMillis(fadeIn),
                Duration.ofMillis(stay),
                Duration.ofMillis(fadeOut)
        );

        Component upperTitleComp = LightCore.instance.getColorTranslation().universalColor(upperTitle, player);
        Component subtitleComp = LightCore.instance.getColorTranslation().universalColor(subtitle, player);

        sendTitle(player,
                upperTitleComp,
                subtitleComp,
                times);
    }

    /**
     * Helper method: Sends a title with default times using Component inputs.
     *
     * @param player      The player to whom the title is sent.
     * @param upperTitle  The upper title as a Component.
     * @param subtitle    The subtitle as a Component.
     */
    public void sendTitle(Player player, Component upperTitle, Component subtitle) {
        sendTitle(player, upperTitle, subtitle, defaultTimes);
    }

    /**
     * Helper method: Sends a title with custom timings using Component inputs.
     *
     * @param player      The player to whom the title is sent.
     * @param upperTitle  The upper title as a Component.
     * @param subtitle    The subtitle as a Component.
     * @param fadeIn      The fade-in duration in milliseconds.
     * @param stay        The display time in milliseconds.
     * @param fadeOut     The fade-out duration in milliseconds.
     */
    public void sendTitle(Player player, Component upperTitle, Component subtitle, long fadeIn, long stay, long fadeOut) {
        Title.Times times = Title.Times.times(
                Duration.ofMillis(fadeIn),
                Duration.ofMillis(stay),
                Duration.ofMillis(fadeOut)
        );

        sendTitle(player, upperTitle, subtitle, times);
    }
}