package io.lightstudios.core.inventory;

import io.lightstudios.core.inventory.model.MenuItem;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Kontext für Aktionen in LightMenu. Gibt Zugriff auf spielerspezifische und menübezogene Methoden.
 */
@Getter
public class LightMenuActionContext {

    private final Player player;
    private final LightMenu menu; // Das aktuelle Menü
    private ItemStack currentItem; // Das Item, auf das geklickt wurde

    public LightMenuActionContext(Player player, LightMenu menu, ItemStack currentItem) {
        this.player = player;
        this.menu = menu;
        this.currentItem = currentItem;
    }

    /**
     * Ändert das aktuelle Item im Inventar.
     *
     * @param newItem Das neue Item.
     */
    public void updateCurrentItem(ItemStack newItem) {
        currentItem = new ItemStack(newItem.getType());
        currentItem.setItemMeta(newItem.getItemMeta());
    }

    /**
     * Fügt ein neues Item an einem bestimmten Slot hinzu.
     *
     * @param page Die Seite, auf der das Item hinzugefügt werden soll.
     * @param slot Der Slot, an dem das Item eingefügt werden soll.
     * @param menuItem Das neue `MenuItem`.
     */
    public void setItem(int page, int slot, MenuItem menuItem) {
        menu.setItem(page, slot, menuItem);
    }

    /**
     * Fügt ein neues Item an mehreren Slots und Seiten hinzu.
     *
     * @param pages Die Seiten, auf denen die Items hinzugefügt werden sollen.
     * @param slots Die Slots, an denen die Items eingefügt werden sollen.
     * @param menuItem Das neue `MenuItem`.
     */
    public void setItems(List<Integer> pages, List<Integer> slots, MenuItem menuItem) {
        menu.setItems(pages, slots, menuItem);
    }

    /**
     * Fügt eine Liste von Items in einem bestimmten Bereich hinzu.
     *
     * @param items Die Menüelemente, die hinzugefügt werden sollen.
     * @param startSlot Der erste Slot des Bereichs.
     * @param endSlot Der letzte Slot des Bereichs.
     */
    public void setItemList(List<MenuItem> items, int startSlot, int endSlot) {
        menu.setItemList(items, startSlot, endSlot);
    }

}