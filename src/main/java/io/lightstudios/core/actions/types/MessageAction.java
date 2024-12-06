package io.lightstudios.core.actions.types;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.interfaces.LightAction;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageAction implements LightAction {

    @Override
    public void execute(Player player, String[] actionDataArray) {

        if (actionDataArray.length < 2) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Invalid message action data",
                    "Usage: 'chat;message'"
            ));
            return;
        }

        Component component = Component.text(actionDataArray[1]);

        Audience audience = (Audience) player;
        audience.sendMessage(component);
    }
}
