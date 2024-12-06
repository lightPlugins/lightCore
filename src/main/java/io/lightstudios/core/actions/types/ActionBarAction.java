package io.lightstudios.core.actions.types;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.interfaces.LightAction;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public class ActionBarAction implements LightAction {
    @Override
    public void execute(Player player, String[] actionDataArray) {

        if (actionDataArray.length < 2) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Invalid action message action data",
                    "Usage: 'actionbar;message'"
            ));
            return;
        }

        String message = actionDataArray[1];
        Component component = Component.text(message);
        Audience audience = (Audience) player;

        if(audience != null) {
            audience.sendActionBar(component);
        }

    }
}
