package io.lightstudios.core.inventory;

import io.lightstudios.core.inventory.model.MenuItem;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TestMenu extends LightMenu {

    public TestMenu() {
        super(MiniMessage.miniMessage().deserialize("<rainbow>Test <red> GUI"), 54);
        this.totalPages = 2; // Example with 2 pages
        updateInventory();
    }

    @Override
    protected void updateInventory() {
        inventory.clear();

        setItem(0, 0, new MenuItem(new ItemStack(Material.DIAMOND), (event, item) -> {
            event.getWhoClicked().sendMessage("You clicked on a diamond!");
        }));
        setItem(0, 1, new MenuItem(new ItemStack(Material.EMERALD), (event, item) -> {
            event.getWhoClicked().sendMessage("You clicked on an emerald!");
        }));

        setItem(1, 0, new MenuItem(new ItemStack(Material.GOLD_INGOT), (event, item) -> {
            event.getWhoClicked().sendMessage("You clicked on a gold ingot!");
        }));
        setItem(1, 1, new MenuItem(new ItemStack(Material.IRON_INGOT), (event, item) -> {
            event.getWhoClicked().sendMessage("You clicked on an iron ingot!");
        }));
        addNavigationItems();
        setDecorativeItems();
    }
}