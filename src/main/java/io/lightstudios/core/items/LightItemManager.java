package io.lightstudios.core.items;

import io.lightstudios.core.LightCore;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.List;

@Getter
public class LightItemManager {

    private JavaPlugin plugin;
    private final HashMap<String, LightItem> items = new HashMap<>();

    /**
     * Create new items from the give files and
     * add them to the cache.
     * @param plugin The plugin instance name
     * @param itemFiles The files, where the items are stored
     */
    public HashMap<String, LightItem> addItemsToCache(@NotNull JavaPlugin plugin, @NotNull List<File> itemFiles) {
        this.plugin = plugin;
        readItems(itemFiles);
        return this.items;
    }

    /**
     * Get an item by its ID (the item ID is the plugin name + the item ID)
     * The item id is the target file name without the extension
     * for example: "pluginname:itemid"
     * @param id The item ID
     * @return The item
     */
    @Nullable
    public LightItem getItemByName(String id) {
        return this.items.get(id);
    }

    /**
     * Read the items from the files
     * and add them to the cache.
     * @param files The files, where the items are stored
     */
    private void readItems(List<File> files) {
        for (File file : files) {
            LightItem item = new LightItem(LightCore.instance, file);
            String id = plugin.getName().toLowerCase() + ":" + item.getItemID();

            if(!items.containsKey(id)) {
                items.put(id, item);
                LightCore.instance.getConsolePrinter().printItemSystem("Loaded item: " + id);
            }
        }
    }
}
