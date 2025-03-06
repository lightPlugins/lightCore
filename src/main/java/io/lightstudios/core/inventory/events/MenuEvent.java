package io.lightstudios.core.inventory.events;

import io.lightstudios.core.inventory.LightMenu;
import io.lightstudios.core.inventory.TestMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuEvent implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof LightMenu) {
            event.setCancelled(true);
            ((LightMenu) event.getInventory().getHolder()).handleMenuClick(event);
        }
    }

    // @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        TestMenu menu = new TestMenu();
        menu.open(player);
    }

}
