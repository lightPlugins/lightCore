package io.lightstudios.core.items;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBTList;
import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.LightNumbers;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ItemFromConfig {

    // value sections from the file
    private final File file;
    private final String itemID;
    private final ItemStack itemStack;
    private final FileConfiguration fileConfiguration;
    private String item;
    private int modelData;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private final Map<String, String> placeholders = new HashMap<>();
    private String headData;
    private String displayname;
    private List<String> lore = new ArrayList<>();

    // Item attributes overrides
    private int durability;
    private int attackDamage;
    private int attackSpeed;
    private int armor;
    private int armorToughness;
    private int knockbackResistance;

    /**
     * Creates a new ItemFromConfig instance from a given file.
     * The item stack is built from the values in the file.
     * After checking ItemFromConfig != null, you can call buildItem()
     * to get the final item stack.
     *
     * @param file the file containing the item configuration
     */
    public ItemFromConfig(File file) {
        this.file = file;
        this.fileConfiguration = YamlConfiguration.loadConfiguration(file);
        this.itemID = file.getName().replace(".yml", "");

        this.itemStack = new ItemStack(Material.STONE, 1);

        // STEP-1: read all values from the file
        readFromConfig();

        // STEP-2: apply all values to the item stack as NBT data
        // only set nbt data after modifying the item meta !!!
        // https://github.com/tr7zw/Item-NBT-API/wiki/Using-the-NBT-API#working-with-items
        applyNBTData();

        // STEP-3: split the item string and handle it
        handleItemString();

        // If all values are set, buildItem() can be called to get the final item stack

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

        translateDisplayHeadData(player);
        List<String> translatedLore = translateLore(player);

        temporaryMeta.setDisplayName(LightCore.instance.getColorTranslation().adventureTranslator(displayname, player));
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
    private void translateDisplayHeadData(Player player) {
        for(String key : placeholders.keySet()) {
            this.displayname = this.displayname
                    .replace("#" + key + "#", placeholders.get(key))
                    .replace(key, PlaceholderAPI.setPlaceholders(player, key));
            // apply colors to the display name here
            this.displayname = LightCore.instance.getColorTranslation().adventureTranslator(displayname, player);
            this.headData = this.headData
                    .replace("#" + key + "#", placeholders.get(key))
                    .replace(key, PlaceholderAPI.setPlaceholders(player, key));
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

    /**
     * Applies all important values to the item file
     * and stores them in the item stack as NBT data.
     */
    private void applyNBTData() {

        NBT.modify(itemStack, nbt -> {
            nbt.setString("item_id", this.itemID);
            nbt.setString("displayname", this.displayname);
            nbt.setString("head_data", this.headData);

            // not sure if this is the correct way to store a list of strings
            ReadWriteNBTList<String> stringList = nbt.getStringList("lore");
            // try to clear the list first before adding new values
            // TODO: check if this is the correct way to clear the list
            stringList.clear();
            // add all lore lines to the list
            for(String line : lore) { stringList.add(line); }

            nbt.setInteger("durability", this.durability);
            nbt.setInteger("attack_damage", this.attackDamage);
            nbt.setInteger("attack_speed", this.attackSpeed);
            nbt.setInteger("armor", this.armor);
            nbt.setInteger("armor_toughness", this.armorToughness);
            nbt.setInteger("knockback_resistance", this.knockbackResistance);

        });

    }

    private void handleItemString() {
        if (this.item == null || this.item.isEmpty()) {
            this.itemStack.setType(Material.STONE);
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Item string is not set for item " + this.itemID + " in file " + this.file.getName(),
                    "Please set the item string to a valid format.",
                    "Example: item: `stone 1 hide_attributes`"
            ));
        }

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

    /**
     * Reads all values from the file
     * and stores them in the class fields.
     */
    private void readFromConfig() {
        // read the "item" string from the file
        if (fileConfiguration.contains("item")) {
            item = fileConfiguration.getString("item");
        }
        // read the "placeholders" section from the file
        if (fileConfiguration.contains("placeholders")) {
            ConfigurationSection placeholderSection = fileConfiguration.getConfigurationSection("placeholders");
            if(placeholderSection != null) {
                for(String path : placeholderSection.getKeys(false)) {
                    placeholders.put(path, fileConfiguration.getString("placeholders." + path));
                }
            }
        }
        // read the "head-data" string from the file
        if (fileConfiguration.contains("head-data")) {
            this.headData = fileConfiguration.getString("head-data");
        }
        // read the "display-name" string from the file
        if (fileConfiguration.contains("displayname")) {
            this.displayname = fileConfiguration.getString("displayname");
        }
        // read the "lore" List<String> from the file
        if (fileConfiguration.contains("lore")) {
            this.lore = fileConfiguration.getStringList("lore");
        }
        // read the "durability" int from the file
        if (fileConfiguration.contains("overrides.durability")) {
            this.durability = fileConfiguration.getInt("durability");
        }
        // read the "attack-damage" int from the file
        if (fileConfiguration.contains("overrides.attack-damage")) {
            this.attackDamage = fileConfiguration.getInt("attack-damage");
        }
        // read the "attack-speed" int from the file
        if (fileConfiguration.contains("overrides.attack-speed")) {
            this.attackSpeed = fileConfiguration.getInt("attack-speed");
        }
        // read the "armor" int from the file
        if (fileConfiguration.contains("overrides.armor")) {
            this.armor = fileConfiguration.getInt("armor");
        }
        // read the "armor-toughness" int from the file
        if (fileConfiguration.contains("overrides.armor-toughness")) {
            this.armorToughness = fileConfiguration.getInt("armor-toughness");
        }
        // read the "knockback-resistance" int from the file
        if (fileConfiguration.contains("overrides.knockback-resistance")) {
            this.knockbackResistance = fileConfiguration.getInt("knockback-resistance");
        }
    }
}
