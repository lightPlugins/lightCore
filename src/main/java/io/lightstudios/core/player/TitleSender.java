package io.lightstudios.core.player;

import io.lightstudios.core.LightCore;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;


public class TitleSender {

    /**
     * Sends a title to a player with the specified fade in, stay, and fade out times
     * @param player The player to send the title to
     * @param upperTitle The title to display at the top of the screen
     * @param subtitle The title to display at the bottom of the screen
     * @param fadeIn The time in milliseconds for the title to fade in
     * @param stay The time in milliseconds for the title to stay on the screen
     * @param fadeOut The time in milliseconds for the title to fade out
     */
    public void sendTitle(Player player, String upperTitle, String subtitle, long fadeIn, long stay, long fadeOut) {

        Audience audience = (Audience) player;
        Component upperTitleComp = LightCore.instance.getColorTranslation().universalColor(upperTitle, player);
        Component lowerTitleComp = LightCore.instance.getColorTranslation().universalColor(subtitle, player);

        // This is a lambda expression that implements the Times interface
        // Converting longs into Durations (Milliseconds)
        Times times = new Times() {
            @Override
            public @NotNull Duration fadeIn() {
                return Duration.ofMillis(fadeIn);
            }

            @Override
            public @NotNull Duration stay() {
                return Duration.ofMillis(stay);
            }

            @Override
            public @NotNull Duration fadeOut() {
                return Duration.ofMillis(fadeOut);
            }
        };

        Title title = Title.title(upperTitleComp, lowerTitleComp, times);
        audience.showTitle(title);
    }

    /**
     * Sends a title to a player with the default fade in, stay, and fade out times
     * @param player The player to send the title to
     * @param upperTitle The title to display at the top of the screen
     * @param subtitle The title to display at the bottom of the screen
     */
    public void sendTitle(Player player, String upperTitle, String subtitle) {

        Audience audience = (Audience) player;
        Component upperTitleComp = LightCore.instance.getColorTranslation().universalColor(upperTitle, player);
        Component lowerTitleComp = LightCore.instance.getColorTranslation().universalColor(subtitle, player);

        Title title = Title.title(upperTitleComp, lowerTitleComp);
        audience.showTitle(title);
    }

}
