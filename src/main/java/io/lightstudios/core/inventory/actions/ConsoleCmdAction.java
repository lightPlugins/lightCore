package io.lightstudios.core.inventory.actions;

import io.lightstudios.core.util.interfaces.LightAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConsoleCmdAction implements LightAction {
    @Override
    public void execute(Player player, String[] actionDataArray) {

        String command = actionDataArray[1];
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
