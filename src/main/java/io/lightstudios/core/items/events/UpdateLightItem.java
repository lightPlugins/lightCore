package io.lightstudios.core.items.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class UpdateLightItem implements Listener {

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {

        if(event.getEntity() instanceof Player player) {

            ItemStack itemStack = event.getItem().getItemStack().clone();
            int mergedAmount = itemStack.getAmount();


        }
    }
}
