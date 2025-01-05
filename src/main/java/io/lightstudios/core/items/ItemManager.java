package io.lightstudios.core.items;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.files.MultiFileManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class ItemManager {

    private JavaPlugin plugin;
    private final HashMap<String, ItemFromConfig> items = new HashMap<>();

    public void addItemsToCache(@NotNull JavaPlugin plugin, @NotNull List<File> itemFiles) {
        this.plugin = plugin;
        readItems(itemFiles);
    }

    @Nullable
    public ItemFromConfig getItemByName(String id) {
        return items.get(id);
    }

    private void readItems(List<File> files) {
        for (File file : files) {
            ItemFromConfig item = new ItemFromConfig(LightCore.instance, file);
            String id = plugin.getName() + "_" + item.getItemID();

            if(!items.containsKey(id)) {
                items.put(id, item);
                LightCore.instance.getConsolePrinter().printItemSystem("Loaded item: " + id);
            }
        }
    }
}
