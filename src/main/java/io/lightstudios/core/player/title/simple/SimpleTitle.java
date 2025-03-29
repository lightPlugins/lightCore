package io.lightstudios.core.player.title.simple;

import io.lightstudios.core.player.title.AbstractCountTitle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

public class SimpleTitle extends AbstractCountTitle {

    @Override
    public void sendTitle(Player player, Component upperTitle, Component subtitle, Title.Times times) {
        // Create a title using adventure's Title API and display it
        Title title = Title.title(upperTitle, subtitle, times);
        player.showTitle(title);
    }
}
