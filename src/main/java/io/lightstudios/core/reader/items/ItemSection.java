package io.lightstudios.core.reader.items;

import io.lightstudios.coins.LightCoins;
import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.LightNumbers;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class ItemSection {

    private final ConfigurationSection section;
    private final Map<String, String> placeholders = new HashMap<>();
    private final File file;

    private final Player player;

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    private String item;
    private String displayName;
    private List<String> lore;
    private int amount;
    private int modelData;

    private final Map<Enchantment, Integer> enchantments = new HashMap<>();

    public ItemSection(ConfigurationSection section, File file, Player player) {

        this.section = section;
        this.file = file;
        this.player = player;

        if(section == null) {
            this.itemStack = new ItemStack(Material.STONE, 1);
            return;
        }

        if(!Objects.requireNonNull(section.getString("id")).equalsIgnoreCase("item")) {
            this.itemStack = new ItemStack(Material.STONE, 1);
            LightCoins.instance.getConsolePrinter().printConfigError(List.of(
                    "The section " + section.getName() + " is not an item section!",
                    "The section must have the id 'item'!",
                    "Returning fallback item (STONE)"
            ));
            return;
        }

        readFromConfig();
        applyNBTData();
        handleItemString();

    }

    /**
     * Builds the item stack from the values in the file.
     * This method should be called after creating a new ItemFromConfig instance.
     * This method needs a player to apply placeholders to the item.
     * This method returns any time a new item stack with all values applied,
     * even if the item stack is misconfigured.
     *
     * @NotNull is used to indicate that the method will never return null. If
     * it is not possible to return a valid item stack, the method will return a
     * default item stack with the type Material.STONE.
     * @param player the player for whom the item is being built
     * @return the final item stack with all values applied
     */
    @NotNull
    public ItemStack buildItem(Player player) {

        // Taking ItemMeta snapshot after changes to nbt
        ItemMeta temporaryMeta = this.itemStack.getItemMeta();

        if(temporaryMeta == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "The temporary Item meta is null for item stack " + this.itemStack + " in file " + this.file.getName(),
                    "Please check if the item meta is not null.",
                    "Otherwise contact the developer."
            ));
            // always clone itemStacks to prevent modifying the original item stack in this class !!!
            return this.itemStack.clone();
        }

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            // Bearbeiten Sie den vorhandenen Eintrag hier
            String value = entry.getValue();
            entry.setValue(value.replace(value, PlaceholderAPI.setPlaceholders(player, value)));
        }

        translateDisplayName(player);
        List<String> translatedLore = translateLore(player);

        temporaryMeta.setDisplayName(LightCore.instance.getColorTranslation().adventureTranslator(displayName, player));
        temporaryMeta.setLore(translatedLore);

        // finally set the item meta to the item stack
        // Do not set it again after this point
        this.itemStack.setItemMeta(temporaryMeta);

        // always clone itemStacks to prevent modifying the original item stack in this class !!!
        return this.itemStack.clone();

    }

    /**
     * Translates custom placeholders in the displayName and headData fields.
     * Iterates through all keys in the placeholders map and replaces
     * occurrences of each placeholder in the displayName and headData
     * with their corresponding values.
     */
    private void translateDisplayName(Player player) {
        for(String key : placeholders.keySet()) {
            this.displayName = this.displayName
                    .replace("#" + key + "#", placeholders.get(key))
                    .replace(key, PlaceholderAPI.setPlaceholders(player, key));
            // apply colors to the display name here
            this.displayName = LightCore.instance.getColorTranslation().adventureTranslator(displayName, player);
        }
    }
    /**
     * Translates custom placeholders in the lore list.
     * Iterates through each line in the lore list and replaces
     * occurrences of each placeholder with their corresponding values.
     */
    private List<String> translateLore(Player player) {
        List<String> newLore = new ArrayList<>();
        for (String line : lore) {
            for (String key : placeholders.keySet()) {
                line = line.replace("#" + key + "#", placeholders.get(key))
                        .replace(key, PlaceholderAPI.setPlaceholders(player, key));
            }
            newLore.add(LightCore.instance.getColorTranslation().adventureTranslator(line, player));
        }
        return newLore;
    }


    private void handleItemString() {

        String[] splitItem = item.split(" ");
        Material material = Material.getMaterial(splitItem[0].toUpperCase());

        if(LightNumbers.isNumber(splitItem[1])) {
            this.itemStack.setAmount(Integer.parseInt(splitItem[1]));
        }

        if(material != null) {
            this.itemStack.setType(material);
        } else {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Invalid material: " + splitItem[0] + " in file " + this.file.getName(),
                    "Material must be a valid material.",
                    "It is set to the backup material -> Stone"
            ));
            this.itemStack.setType(Material.STONE);
        }

        // Taking ItemMeta snapshot after changes to nbt
        ItemMeta temporaryMeta = this.itemStack.getItemMeta();

        if(temporaryMeta == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "The temporary Item meta is null for item stack " + this.itemStack + " in file " + this.file.getName(),
                    "Please check if the item meta is not null.",
                    "Otherwise contact the developer."
            ));
            return;
        }

        for(String split : splitItem) {

            if(split.contains("model-data")) {
                String[] splitModelData = split.split(":");
                if(LightNumbers.isNumber(splitModelData[1])) {
                    temporaryMeta.setCustomModelData(Integer.parseInt(splitModelData[1]));
                    this.modelData = Integer.parseInt(splitModelData[1]);
                } else {
                    LightCore.instance.getConsolePrinter().printError(List.of(
                            "Invalid model data: " + splitModelData[1] + " in file " + this.file.getName(),
                            "Model data must be a number.",
                            "Syntax: model-data:12345"
                    ));
                }
            }

            // Check if the split contains enchantments and apply them
            if(split.contains("enchant") &! split.contains("hide_enchants")) {
                String[] enchantSplit = split.split(":");

                if(enchantSplit.length > 3) {
                    LightCore.instance.getConsolePrinter().printError(List.of(
                            "Invalid enchantment: " + enchantSplit[1] + " in file " + this.file.getName(),
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
                            temporaryMeta.addEnchant(enchantment, level, true);
                            enchantments.put(enchantment, level);
                        } else {
                            LightCore.instance.getConsolePrinter().printError(List.of(
                                    "Invalid enchantment: " + enchantSplit[1] + " in file " + this.file.getName(),
                                    "Enchantment must be a valid enchantment.",
                                    "Syntax: enchant:flame:1"
                            ));
                        }
                    } else {
                        LightCore.instance.getConsolePrinter().printError(List.of(
                                "Invalid enchantment level: " + enchantSplit[2] + " in file " + this.file.getName(),
                                "Enchantment level must be a number.",
                                "Syntax: enchant:flame:1"
                        ));
                    }
                }
            }

            // Check if the item should glow
            if(split.equalsIgnoreCase("glow")) {
                temporaryMeta.addEnchant(Enchantment.FLAME, 1, true);
            }

            // Check if the split contains item flags
            try {
                ItemFlag itemFlag = ItemFlag.valueOf(split.toUpperCase());
                temporaryMeta.addItemFlags(itemFlag);
            } catch (IllegalArgumentException ignored) { }

            // apply the new temporary meta to the old item stack
            itemStack.setItemMeta(temporaryMeta);
        }
    }


    private void applyNBTData() {


    }


    /**
     * Reads all values from the file
     * and stores them in the class fields.
     */
    private void readFromConfig() {
        // read the "placeholders" section from the file
        if (section.contains("placeholders")) {
            if(section.get("placeholders") != null) {
                for(String path : section.getKeys(false)) {
                    placeholders.put(path, section.getString("placeholders." + path));
                }
            }
        }

        if(section.contains("item")) {
            item = section.getString("item");
        } else {
            LightCore.instance.getConsolePrinter().printConfigError(List.of(
                    "The item builder does not have an item attribute!",
                    "Please add an item to the item!"
            ));
        }

        if(section.contains("displayName")) {
            displayName = section.getString("displayName");
        } else {
            LightCore.instance.getConsolePrinter().printConfigError(List.of(
                    "The item builder does not have a displayName attribute!",
                    "Please add a displayName to the item!"
            ));
        }

        if(section.contains("lore")) {
            lore = section.getStringList("lore");
        } else {
            LightCore.instance.getConsolePrinter().printConfigError(List.of(
                    "The item builder does not have a lore attribute!",
                    "Please add a lore to the item!"
            ));
        }
    }
}
