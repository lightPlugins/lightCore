package io.lightstudios.core.inventory.actions;

import io.lightstudios.core.util.interfaces.LightAction;
import org.bukkit.entity.Player;

public class InvCloseAction implements LightAction {

    @Override
    public void execute(Player player, String[] actionDataArray) {
        player.closeInventory();
    }
}
