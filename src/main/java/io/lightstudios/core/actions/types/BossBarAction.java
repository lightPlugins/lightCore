package io.lightstudios.core.actions.types;

import io.lightstudios.core.util.interfaces.LightAction;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class BossBarAction implements LightAction {

    @Override
    public void execute(Player player, String[] actionDataArray) {

        String color = actionDataArray[1];
        String message = actionDataArray[2];
        Component component = Component.text(message);

        BossBar.Color bossBarColor = BossBar.Color.valueOf(color.toUpperCase());

        Audience audience = (Audience) player;
        BossBar bossbar = BossBar.bossBar(component, 1.0f, bossBarColor, BossBar.Overlay.PROGRESS);
        audience.showBossBar(bossbar);

    }
}
