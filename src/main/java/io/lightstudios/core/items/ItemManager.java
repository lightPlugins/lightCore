package io.lightstudios.core.items;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.files.MultiFileManager;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.List;

@Getter
public class ItemManager {

    private final MultiFileManager itemFiles;
    private final HashMap<String, ItemFromConfig> items;

    public ItemManager(MultiFileManager itemFiles) {
        this.itemFiles = itemFiles;
        this.items = new HashMap<>();

        readItems();
    }

    public ItemFromConfig getItemByName(String id) {
        return items.get(id);
    }

    private void readItems() {

        if(!items.isEmpty()) {
            items.clear();
        }

        for (File file : itemFiles.getYamlFiles()) {
            ItemFromConfig item = new ItemFromConfig(file);
            String id = item.getItemID();

            if(items.containsKey(id)) {
                LightCore.instance.getConsolePrinter().printError(List.of(
                        "Duplicate item id found for item: " + id,
                        "Please check your item files and make sure that each item has a unique id."
                ));
                continue;
            }

            items.put(id, item);
        }
    }
}
