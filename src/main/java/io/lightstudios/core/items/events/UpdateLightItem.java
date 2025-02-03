package io.lightstudios.core.items.events;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.items.LightItem;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class UpdateLightItem implements Listener {

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {

    }

    @EventHandler
    public void onClickLightItemOnInventory(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            if (event.getCurrentItem() != null) {

                if (event.getClickedInventory() == null) {
                    return;
                }

                // can't update items while a player is in game mode creative
                if (player.getGameMode().equals(GameMode.CREATIVE)) {
                    return;
                }

                // ignore empty slots / clicks
                if (event.getCurrentItem().getType() == Material.AIR) {
                    return;
                }

                ItemStack itemStack = event.getCurrentItem();
                LightItem lightItem = LightCore.instance.getItemManager().isLightItem(itemStack.clone());

                int slot = event.getSlot();
                int stackSize = itemStack.getAmount();

                // it's not a light item, so we don't need to do anything
                if (lightItem == null) {
                    return;
                }
                // set the stack size from the clicked item to the LightItem,
                // so we can check if the lightItem is different compared to the newLightItem.
                ItemStack lightItemClone = lightItem.getItemStack().clone();
                lightItemClone.setAmount(stackSize);

                LightItem newLightItem = LightCore.instance.getItemManager().isDifferent(itemStack, lightItemClone);
                // the LightItem is different, so we need to update it
                if (newLightItem != null) {
                    // Copy persistent data from the original item to the new item
                    ItemMeta originalMeta = itemStack.getItemMeta();
                    ItemMeta newMeta = newLightItem.getItemStack().getItemMeta();

                    // check for custom persistent data and reapplies it to the new item
                    if (originalMeta != null && newMeta != null) {
                        originalMeta.getPersistentDataContainer().getKeys().forEach(key -> {
                            String value = originalMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                            if (value != null) {
                                newMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
                            }
                        });
                        newLightItem.getItemStack().setItemMeta(newMeta);
                    }

                    newLightItem.getItemStack().setAmount(stackSize);
                    event.setCancelled(true);
                    event.getClickedInventory().setItem(slot, newLightItem.getItemStack());
                    player.sendMessage(Component.text("§aItem updated!"));
                }
            }
        }
    }
}
