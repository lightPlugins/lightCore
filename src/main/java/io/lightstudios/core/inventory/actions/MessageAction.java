package io.lightstudios.core.inventory.actions;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.interfaces.LightAction;
import org.bukkit.entity.Player;

public class MessageAction implements LightAction {

    @Override
    public void execute(Player player, String[] actionDataArray) {

        if (actionDataArray.length < 2) {
            player.sendMessage("Config error: No message to send.");
            return;
        }
        LightCore.instance.getMessageSender().sendPlayerMessage(player, actionDataArray[1]);
    }
}
