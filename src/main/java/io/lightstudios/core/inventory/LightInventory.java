package io.lightstudios.core.inventory;

import io.lightstudios.core.inventory.model.InventoryData;
import io.lightstudios.core.inventory.model.MenuItem;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class LightInventory extends LightMenu {

    private InventoryData configInventory; // Die aktuelle Konfiguration des Inventars
    private final Map<String, BiConsumer<Player, ItemStack>> customActions = new HashMap<>(); // Aktionen basierend auf namespaceKeys

    /**
     * Konstruktor für die Instanz von TestMenu basierend auf einer ConfigInventory.
     *
     * @param configInventory Die ConfigInventory, die das Menü definiert.
     */
    public LightInventory(InventoryData configInventory) {
        super(configInventory.getTitle(), configInventory.getSize());
        this.configInventory = configInventory;

        // apply the interval coming from the config
        this.updateInterval = configInventory.getUpdateInterval();

        // Initialisiere das Standard-Inventar
        populateInventory();
    }

    /**
     * Ergänzt spezielle Aktionen, basierend auf einem namespaceKey, der innerhalb des Menüs verwendet wird.
     * Die Aktion erhält den Spieler und das ItemStack zur Bearbeitung.
     *
     * @param namespaceKey Der Namespace-Key des Items.
     * @param action       Die auszuführende Aktion, wenn auf das Item geklickt wird (Spieler und ItemStack).
     */
    public void addCustomAction(String namespaceKey, BiConsumer<Player, LightMenuActionContext> action) {
        if (namespaceKey != null && action != null) {
            customActions.put(namespaceKey, (player, itemStack) -> {
                // Erzeuge einen Kontext mit Zugriff auf alle relevanten Methoden
                LightMenuActionContext context = new LightMenuActionContext(player, this, itemStack);

                // Führe die benutzerdefinierte Aktion aus
                action.accept(player, context);

                // Aktualisiere das Inventar, falls eine Änderung erfolgt ist
                updateInventory();
            });
        }
    }

    /**
     * Aktualisiert das Inventar bei Änderungen.
     */
    @Override
    protected void updateInventory() {
        // inventory.clear();
        populateInventory();

    }

    public void test() {
    }

    /**
     * Füllt das Inventar basierend auf der aktuellen ConfigInventory-Instanz mit Items.
     */
    private void populateInventory() {

        // Navigation, statische Items und benutzerdefinierte Elemente verarbeiten
        // processItems(configInventory.getNavigationItems());
        processNavigationItems();
        processItems(configInventory.getStaticItems());
        processItems(configInventory.getCustomItems());

        // Dekorationselemente setzen
        if (configInventory.getDecorationItem() != null) {
            ItemStack decorationItem = configInventory.getDecorationItem();
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null) {
                    setDecorativeItems(decorationItem, true);
                }
            }
        }
    }

    private void processNavigationItems() {

        ItemStack previousPageItem = new ItemStack(configInventory.getNavigationItems().get("previous-page").getItemStack().getType());
        ItemStack nextPageItem = new ItemStack(configInventory.getNavigationItems().get("next-page").getItemStack().getType());
        ItemStack closeItem = new ItemStack(configInventory.getNavigationItems().get("close").getItemStack().getType());

        List<Integer> previousPageSlots = configInventory.getNavigationItems().get("previous-page").getSlots();
        List<Integer> nextPageSlots = configInventory.getNavigationItems().get("next-page").getSlots();
        List<Integer> closeSlots = configInventory.getNavigationItems().get("close").getSlots();

        // Navigations
        addNavigationItems(
                previousPageItem,
                nextPageItem,
                previousPageSlots,
                nextPageSlots,
                configInventory.getNavigationItems().get("previous-page").isUsePageAsAmount()
        );

        // Close inventory
        setCloseItem(closeItem, closeSlots);

    }

    /**
     * Verarbeitet die Items aus einer Map und fügt sie dem Inventar hinzu.
     *
     * @param items Die zu verarbeitenden Items.
     */
    private void processItems(Map<String, InventoryData.InventoryItem> items) {
        items.forEach((namespaceKey, configItem) -> {
            ItemStack itemStack = configItem.getItemStack();

            // Setze die Items in die entsprechenden Slots
            for (int slot : configItem.getSlots()) {
                if (slot < inventory.getSize()) {
                    setItem(currentPage, slot, new MenuItem(itemStack, (event, menuItem) -> {
                        Player player = (Player) event.getWhoClicked();

                        // Prüfe, ob für den Namespace eine spezielle Aktion definiert wurde
                        if (customActions.containsKey(namespaceKey)) {
                            customActions.get(namespaceKey).accept(player, itemStack); // Führe die Aktion aus
                            inventory.setItem(event.getSlot(), itemStack); // Aktualisiere das geänderte Item im Menü
                        }

                        // Beispiel: Schließe das Inventar, wenn das Item "close" ist
                        if ("close".equals(namespaceKey)) {
                            player.closeInventory();
                        }

                    }));
                }
            }
        });
    }

    /**
     * Anwendung von PlaceholderAPI auf alle Items, die Spieler-Platzhalter verwenden.
     *
     * @param player Der Spieler, für den die Platzhalter angewendet werden.
     */
    public void applyPlaceholders(Player player) {
        applyPlaceholdersToItems(player, configInventory.getNavigationItems());
        applyPlaceholdersToItems(player, configInventory.getStaticItems());
        applyPlaceholdersToItems(player, configInventory.getCustomItems());
    }

    private void applyPlaceholdersToItems(Player player, Map<String, InventoryData.InventoryItem> items) {
        items.forEach((key, configItem) -> {
            ItemStack itemStack = configItem.getItemStack();
            ItemMeta meta = itemStack.getItemMeta();

            if (meta != null) {
                // Placeholder für Displaynamen anwenden
                if (meta.hasDisplayName()) {
                    String displayName = LegacyComponentSerializer.legacySection().serialize(meta.displayName());
                    displayName = PlaceholderAPI.setPlaceholders(player, displayName);
                    meta.displayName(Component.text(displayName));
                }

                // Placeholder für Lore anwenden
                if (meta.hasLore()) {
                    List<Component> newLore = new ArrayList<>();
                    for (Component line : meta.lore()) {
                        String loreLine = LegacyComponentSerializer.legacySection().serialize(line);
                        loreLine = PlaceholderAPI.setPlaceholders(player, loreLine);
                        newLore.add(Component.text(loreLine));
                    }
                    meta.lore(newLore);
                }
            }

            itemStack.setItemMeta(meta);
        });
    }

    /**
     * Aktualisiert das ConfigInventory mit neuen Daten und erneuert das GUI.
     *
     * @param newConfigInventory Die neu geladene ConfigInventory.
     */
    public void reloadConfig(InventoryData newConfigInventory) {
        this.configInventory = newConfigInventory;
        updateInventory();
    }
}