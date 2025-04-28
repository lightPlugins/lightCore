package io.lightstudios.core.player.title;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.player.title.events.TitleSendEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TitleSender {

    private final Map<UUID, Title> titleQueue = new HashMap<>();

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
        TitleSendEvent event = new TitleSendEvent(player, Title.title(upperTitleComp, lowerTitleComp, times));
        Bukkit.getPluginManager().callEvent(event);
    }

    public void sendTitle(Player player, Component upperTitle, Component subtitle, long fadeIn, long stay, long fadeOut) {


        Component upperTitleComp = LightCore.instance.getColorTranslation().translateComponent(upperTitle, player);
        Component lowerTitleComp = LightCore.instance.getColorTranslation().translateComponent(subtitle, player);

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

        TitleSendEvent event = new TitleSendEvent(player, Title.title(upperTitleComp, lowerTitleComp, times));
        Bukkit.getPluginManager().callEvent(event);

    }

    /**
     * Sends a title to a player with the default fade in, stay, and fade out times
     * @param player The player to send the title to
     * @param upperTitle The title to display at the top of the screen
     * @param subtitle The title to display at the bottom of the screen
     */
    public void sendTitle(Player player, String upperTitle, String subtitle) {

        Component upperTitleComp = LightCore.instance.getColorTranslation().universalColor(upperTitle, player);
        Component lowerTitleComp = LightCore.instance.getColorTranslation().universalColor(subtitle, player);

        TitleSendEvent event = new TitleSendEvent(player, Title.title(upperTitleComp, lowerTitleComp));
        Bukkit.getPluginManager().callEvent(event);
    }

}
