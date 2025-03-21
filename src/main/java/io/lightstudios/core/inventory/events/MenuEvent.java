package io.lightstudios.core.inventory.events;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.inventory.LightMenu;
import io.lightstudios.core.inventory.LightInventory;
import io.lightstudios.core.inventory.model.InventoryData;
import io.lightstudios.core.inventory.model.MenuItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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
        // Test-Menü greifen
        InventoryData invData = LightCore.instance.getLightInventories().get("test-inv");
        LightInventory lightInventory = new LightInventory(invData);

        // Erstellen einer Liste von MenuItems
        List<MenuItem> testItems = new ArrayList<>();
        for (int i = 1; i <= 94; i++) {
            // Erstelle ein neues Gold-Ingot-Item
            ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT);

            // Setzen von Metadaten (wie Name)
            ItemMeta testMeta = goldIngot.getItemMeta();
            if (testMeta != null) {
                testMeta.displayName(Component.text("Gold Ingot #" + i));
                goldIngot.setItemMeta(testMeta);
            }

            // Klick-Aktion für das MenuItem hinzufügen
            int index = i; // Muss final oder effektiv final sein für Lamba-Ausdruck
            MenuItem menuItem = new MenuItem(goldIngot, (clickEvent, clickedItem) -> {
                Player player = (Player) clickEvent.getWhoClicked();
                player.sendMessage("Du hast auf Gold Ingot #" + index + " geklickt!");
                // Hier kannst du weitere Aktionen hinzufügen
            });

            // MenuItem der Liste hinzufügen
            testItems.add(menuItem);
        }

        // Setze die Item-Liste in einem spezifischen Slotbereich
        int startSlot = 20; // Erster Slot im Bereich
        int endSlot = 33;   // Letzter Slot im Bereich
        lightInventory.setItemList(testItems, startSlot, endSlot);

        // Öffne das Menü für den Spieler
        lightInventory.open(event.getPlayer());
    }


}
