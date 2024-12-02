package io.lightstudios.core.items.events;

import de.tr7zw.changeme.nbtapi.NBT;
import io.lightstudios.core.LightCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class UpdateCustomItem implements Listener {

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {

        if(event.getEntity() instanceof Player player) {

            ItemStack itemStack = event.getItem().getItemStack().clone();
            int mergedAmount = itemStack.getAmount();

            String itemID = NBT.get(itemStack, nbt ->
                    (String) nbt.getString("item_id"));

            if(itemID == null) {
                player.sendMessage("§cThis item is not a custom item.");
                return;
            }

            ItemStack customItem = LightCore.instance.getItemManager().getItemByName(itemID).buildItem(player);
            customItem.setAmount(mergedAmount);

            if(customItem.equals(itemStack)) {
                player.sendMessage("§cThis item is already a custom item.");
                return;
            }

            event.getItem().setItemStack(customItem);
            player.sendMessage("§aYou have picked up a custom item.");

        }
    }

}
