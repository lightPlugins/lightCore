package io.lightstudios.core.inventory;

import io.lightstudios.core.inventory.model.MenuItem;
import io.lightstudios.core.util.LightTimers;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class LightMenu implements InventoryHolder {
    protected Inventory inventory;
    protected Map<Integer, MenuItem> menuItems;
    protected int currentPage;
    protected int totalPages;
    private final Map<UUID, Integer> clickTracker = new HashMap<>();
    private final Map<UUID, Long> cooldownTracker = new HashMap<>();

    public LightMenu(Component title, int size) {
        this.inventory = Bukkit.createInventory(this, size, title);
        this.menuItems = new HashMap<>();
        this.currentPage = 0;
        this.totalPages = 1;
    }

    public void setItem(int page, int slot, MenuItem item) {
        if (page < 0 || page >= totalPages) {
            throw new IllegalArgumentException("Invalid page number > " + totalPages);
        }
        int adjustedSlot = page * inventory.getSize() + slot;
        menuItems.put(adjustedSlot, item);
        if (page == currentPage) {
            inventory.setItem(slot, item.getItemStack());
        }
    }

    public void setItem(List<Integer> pages, int slot, MenuItem item) {
        for (int page : pages) {
            if (page < 0 || page >= totalPages) {
                throw new IllegalArgumentException("Invalid page number: " + page);
            }
            int adjustedSlot = page * inventory.getSize() + slot;
            menuItems.put(adjustedSlot, item);
            if (page == currentPage) {
                inventory.setItem(slot, item.getItemStack());
            }
        }
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public void handleMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        UUID playerId = player.getUniqueId();

        if (cooldownTracker.containsKey(playerId) && cooldownTracker.get(playerId) > System.currentTimeMillis()) {
            player.sendMessage(Component.text("You clicked way too fast. Slow down!"));
            event.setCancelled(true);
            return;
        }

        clickTracker.put(playerId, clickTracker.getOrDefault(playerId, 0) + 1);

        if (clickTracker.get(playerId) > 5) {
            cooldownTracker.put(playerId, System.currentTimeMillis() + 1000);
            clickTracker.put(playerId, 0);
            player.sendMessage(Component.text("You clicked too fast! Cooldown applied."));
            event.setCancelled(true);
            return;
        }

        int slot = event.getSlot() + (currentPage * inventory.getSize());
        if (menuItems.containsKey(slot)) {
            menuItems.get(slot).onClick(event);
        }

        LightTimers.doSync((task) -> {
            int clicks = clickTracker.getOrDefault(playerId, 0) - 1;
            if (clicks <= 0) {
                clickTracker.remove(playerId);
            } else {
                clickTracker.put(playerId, clicks);
            }
        }, 20);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void nextPage(Player player) {
        if (currentPage < totalPages - 1) {
            currentPage++;
            updateInventory();
            open(player);
        } else {
            player.sendMessage(Component.text("You are already on the last page!"));
        }
    }

    public void previousPage(Player player) {
        if (currentPage > 0) {
            currentPage--;
            updateInventory();
            open(player);
        } else {
            player.sendMessage(Component.text("You are already on the first page!"));
        }
    }

    protected abstract void updateInventory();

    protected void addNavigationItems() {
        // Add next page item
        setItem(List.of(0, 1), 53, new MenuItem(new ItemStack(Material.ARROW), (event, item) -> {
            nextPage((Player) event.getWhoClicked());
            event.getWhoClicked().sendMessage(Component.text("You clicked on the next page!"));
        }));

        // Add previous page item
        setItem(List.of(0, 1), 45, new MenuItem(new ItemStack(Material.ARROW), (event, item) -> {
            previousPage((Player) event.getWhoClicked());
            event.getWhoClicked().sendMessage(Component.text("You clicked on the previous page!"));
        }));
    }

    protected void setDecorativeItems() {
        ItemStack decorativeItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, decorativeItem);
            }
        }
    }
}