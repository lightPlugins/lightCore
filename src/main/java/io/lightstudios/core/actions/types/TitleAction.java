package io.lightstudios.core.actions.types;

import io.lightstudios.core.util.interfaces.LightAction;
import org.bukkit.entity.Player;

public class TitleAction implements LightAction {
    @Override
    public void execute(Player player, String[] actionDataArray) {

        String title = actionDataArray[1];
        String subtitle = actionDataArray[2];
        int fadeIn = Integer.parseInt(actionDataArray[3]);
        int stay = Integer.parseInt(actionDataArray[4]);
        int fadeOut = Integer.parseInt(actionDataArray[5]);

        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);

    }
}
