package io.lightstudios.core.items;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.LightNumbers;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
        readItems(plugin, itemFiles);
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
    private void readItems(JavaPlugin plugin, List<File> files) {
        for (File file : files) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            String id = plugin.getName() + ":" + file.getName().replace(".yml", "");
            boolean isNexo = false;
            Material material = Material.STONE;
            String item;
            int stackSize = 1;
            String customName = null;
            List<String> lore = new ArrayList<>();
            HashMap<Enchantment, Integer> enchantments = new HashMap<>();
            List<ItemFlag> itemFlags = new ArrayList<>();
            int customModelData = 0;

            // checks if the file has the 'item' key
            // this key is required to create the item
            // otherwise the file will be skipped!
            if(config.contains("item")) {
                item = config.getString("item");
            } else {
                LightCore.instance.getConsolePrinter().printConfigError(List.of(
                        "Item key is missing in file: " + file.getName(),
                        "No item will be set for this item.",
                        "skipping this file ..."
                ));
                continue;
            }

            String[] splitItem = item.split(" ");

            if(splitItem[0].contains("nexo:")) {
                if(LightCore.instance.getHookManager().isExistNexo()) {
                    id = splitItem[0];
                    isNexo = true;
                } else {
                    LightCore.instance.getConsolePrinter().printError(List.of(
                            "Nexo is not installed, but you are trying to use a Nexo item in file " + file.getName(),
                            "Material must be a valid material.",
                            "It is set to the backup material -> Stone"
                    ));
                }

            } else {
                material = Material.getMaterial(splitItem[0].toUpperCase());
                if(material == null) {
                    LightCore.instance.getConsolePrinter().printError(List.of(
                            "Invalid material: " + splitItem[0] + " in file " + file.getName(),
                            "Material must be a valid material.",
                            "It is set to the backup material -> Stone"
                    ));
                }
            }

            if(LightNumbers.isNumber(splitItem[1])) {
                stackSize = Integer.parseInt(splitItem[1]);
            }

            for(String split : splitItem) {
                // check if the split contains model data and apply it
                if(split.contains("model-data")) {
                    String[] splitModelData = split.split(":");
                    if(LightNumbers.isNumber(splitModelData[1])) {
                        customModelData = Integer.parseInt(splitModelData[1]);
                    } else {
                        LightCore.instance.getConsolePrinter().printError(List.of(
                                "Invalid model data: " + splitModelData[1] + " in file " + file.getName(),
                                "Model data must be a number.",
                                "Syntax: model-data:12345"
                        ));
                    }
                }

                // Check if the split contains enchantments and apply them
                if(split.contains("enchant") &! split.contains("hide_enchants")) {
                    String[] enchantSplit = split.split(":");

                    if(enchantSplit.length != 3) {
                        LightCore.instance.getConsolePrinter().printError(List.of(
                                "Invalid enchantment: " + enchantSplit[1] + " in file " + file.getName(),
                                "Enchantment must be a valid enchantment.",
                                "Syntax: enchant:flame:1"
                        ));
                    } else {
                        // TODO: find a better way to get enchantments from the string, because
                        //       Enchantment.getByName() is deprecated
                        Enchantment enchantment = Enchantment.getByName(enchantSplit[1].toUpperCase());

                        if(LightNumbers.isNumber(enchantSplit[2])) {
                            int level = Integer.parseInt(enchantSplit[2]);
                            if(enchantment != null) {
                                enchantments.put(enchantment, level);
                            } else {
                                LightCore.instance.getConsolePrinter().printError(List.of(
                                        "Invalid enchantment: " + enchantSplit[1] + " in file " + file.getName(),
                                        "Enchantment must be a valid enchantment.",
                                        "Syntax: enchant:flame:1"
                                ));
                            }
                        } else {
                            LightCore.instance.getConsolePrinter().printError(List.of(
                                    "Invalid enchantment level: " + enchantSplit[2] + " in file " + file.getName(),
                                    "Enchantment level must be a number.",
                                    "Syntax: enchant:flame:1"
                            ));
                        }
                    }
                }

                // Check if the split contains item flags
                try {
                    ItemFlag itemFlag = ItemFlag.valueOf(split.toUpperCase());
                    itemFlags.add(itemFlag);
                } catch (IllegalArgumentException ignored) { }
            }

            if (config.contains("customName")) {
                customName = config.getString("custom-name");
            } else {
                LightCore.instance.getConsolePrinter().printConfigError(List.of(
                        "Custom name key is missing in file: " + file.getName(),
                        "No custom name will be set for this item."
                ));
            }

            if (config.contains("lore")) {
                lore = config.getStringList("lore");
            } else {
                LightCore.instance.getConsolePrinter().printConfigError(List.of(
                        "Lore key is missing in file: " + file.getName(),
                        "No lore will be set for this item."
                ));
            }

            LightItem lightItem = new LightItem.Builder()
                    .setMaterial(material) // the item material
                    .setStackSize(stackSize) // the stack size (supports 1 - 99)
                    .setCustomName(customName != null ? Component.text(customName) : null) // item display name
                    .setLore(lore.stream().map(Component::text).collect(Collectors.toList())) // item lore
                    .setCustomModelData(customModelData) // custom model data
                    .setEnchantments(enchantments) // item enchantments
                    .setItemFlags(itemFlags) // item flags
                    .setNexoItem(isNexo) // is this a Nexo item
                    .setID(id) // the item ID (file name without the extension)
                    .build(); // build the item
            items.put(id, lightItem);
        }
    }
}
