package io.lightstudios.core.inventory.actions;

import io.lightstudios.core.util.interfaces.LightAction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveItemAction implements LightAction {

    @Override
    public void execute(Player player, String[] actionDataArray) {

        String[] seperatedData = actionDataArray[1].split(" ");
        ItemStack itemStack = new ItemStack(Material.valueOf(seperatedData[0].toUpperCase()), Integer.parseInt(seperatedData[1]));

        // check if the player has a full inventory
        if(player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), itemStack);
        } else {
            player.getInventory().addItem(itemStack);
        }

    }
}
