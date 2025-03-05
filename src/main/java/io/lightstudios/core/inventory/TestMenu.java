package io.lightstudios.core.inventory;

import io.lightstudios.core.inventory.model.MenuItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class TestMenu extends LightMenu {

    private int counter = 0;
    private int counter2 = 1000;

    public TestMenu() {
        super(Component.text("Test GUI").color(NamedTextColor.RED), 54);
        this.totalPages = 2;

        // Initialisiere das Inventar
        updateInventory();
    }

    @Override
    protected void updateInventory() {
        inventory.clear();

        // Dynamisches Item mit Adventure-API
        ItemStack dynamicItem = new ItemStack(Material.DIAMOND);
        ItemStack dynamicItem2 = new ItemStack(Material.DIAMOND);
        ItemMeta meta = dynamicItem.getItemMeta();
        ItemMeta meta2 = dynamicItem.getItemMeta();
        if (meta != null) {
            // Setze den Display Name mit Adventure Component
            meta.displayName(Component.text("Dynamisches Item")
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false)); // Ohne Kursivschrift

            // Setze die Lore mit einer Component-Liste
            meta.lore(Collections.singletonList(
                    Component.text("Wert: ")
                            .color(NamedTextColor.GRAY)
                            .append(Component.text(String.valueOf(counter)).color(NamedTextColor.GREEN))
            ));

            dynamicItem.setItemMeta(meta);
        }

        if (meta2 != null) {
            // Setze den Display Name mit Adventure Component
            meta2.displayName(Component.text("Dynamisches Item")
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false)); // Ohne Kursivschrift

            // Setze die Lore mit einer Component-Liste
            meta2.lore(Collections.singletonList(
                    Component.text("Wert2: ")
                            .color(NamedTextColor.GRAY)
                            .append(Component.text(String.valueOf(counter2)).color(NamedTextColor.YELLOW))
            ));

            dynamicItem2.setItemMeta(meta2);
        }

        // Füge das dynamische Item in den Slot 0 ein
        setItem(0, 0, new MenuItem(dynamicItem, (event, item) -> {
            event.getWhoClicked().sendMessage("Du hast auf das dynamische Item geklickt!");
        }));

        setItem(1, 5, new MenuItem(dynamicItem2, (event, item) -> {
            event.getWhoClicked().sendMessage("Du hast auf das zweite dynamische Item geklickt!");
        }));

        // Erhöhe den Zähler
        counter++;
        counter2--;

        // Optional: Navigation oder Dekorationen hinzufügen
        addNavigationItems();
        setDecorativeItems();
    }
}