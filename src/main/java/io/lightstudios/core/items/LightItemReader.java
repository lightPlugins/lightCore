package io.lightstudios.core.items;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.items.models.LightItem;
import io.lightstudios.core.items.types.ItemType;
import io.lightstudios.core.items.types.RecipeType;
import io.lightstudios.core.util.LightNumbers;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LightItemReader {

    private final Map<String, LightItem> lightItems = new HashMap<>();
    private final Plugin plugin;

    public LightItemReader(Plugin plugin, List<File> itemFiles) {

        this.plugin = plugin;

        if(plugin == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Provided Plugin is null. Cannot read item files.",
                    "Please ensure the plugin is initialized correctly."
            ));
            return;
        }

        for (File file : itemFiles) {
            String id = file.getName().replace(".yml", "");
            LightItem item = readItemFromConfig(file);
            if (item != null) {
                lightItems.put(id, item);
            } else {
                LightCore.instance.getConsolePrinter().printError(List.of(
                        "Failed to read item from file: " + file.getName(),
                        "This file is not a valid item file!",
                        "Please check the file format and content."
                ));
                return;
            }
        }

        LightCore.instance.getConsolePrinter().printInfo("Successfully loaded §e" + lightItems.size() + "§r item files.");
    }


    @Nullable
    private LightItem readItemFromConfig(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection itemSection = config.getConfigurationSection("item");
        ConfigurationSection itemArgumentSection = config.getConfigurationSection("item.args");
        ConfigurationSection recipeSection = config.getConfigurationSection("crafting-recipe");
        ConfigurationSection extraSection = config.getConfigurationSection("extras");

        if(itemSection == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Item section not found in file: " + file.getName(),
                    "It looks like this file is missing the item section.",
                    "Please ensure the item section is defined correctly."
            ));
            return null;
        }

        if(itemArgumentSection == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Item argument section not found in file: " + file.getName(),
                    "It looks like this file is missing the item argument section.",
                    "Please ensure the item argument section is defined correctly."
            ));
            return null;
        }

        if(recipeSection == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Recipe section not found in file: " + file.getName(),
                    "It looks like this file is missing the recipe section.",
                    "Please ensure the recipe section is defined correctly."
            ));
            return null;
        }

        if(extraSection == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Extra section not found in file: " + file.getName(),
                    "It looks like this file is missing the extra section.",
                    "Please ensure the extra section is defined correctly."
            ));
            return null;
        }

        LightItem item = new LightItem();

        String itemType = itemSection.getString("type");
        ItemType type = ItemType.valueOfNullable(itemType);

        if(type == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Item type is not valid in file: " + file.getName(),
                    "It looks like this file has an invalid item type.",
                    "Please ensure the item type is defined correctly.",
                    "Provided type: " + itemType
            ));
            return null;
        }

        item.setItemType(type);

        String materialOrID = itemSection.getString("material");

        if(materialOrID == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Material Data is not valid in file: " + file.getName(),
                    "It looks like this file has an invalid material or ID.",
                    "Please ensure the material or ID is defined correctly.",
                    "Fallback to default data: stone"
            ));
            materialOrID = "stone";
        }

        item.setMaterialOrID(materialOrID);

        LightItem.ItemArguments itemArguments = readItemArguments(itemArgumentSection);
        LightItem.RecipeArguments recipeArguments = readRecipeArguments(recipeSection);

        item.setExtraSection(extraSection);
        item.setItemSection(itemSection);
        item.setRecipeSection(recipeSection);

        item.setItemArguments(itemArguments);
        item.setRecipeArguments(recipeArguments);

        return item;
    }

    private LightItem.ItemArguments readItemArguments(ConfigurationSection itemSection) {

        LightItem.ItemArguments itemArguments = new LightItem.ItemArguments();

        // read and converting display name to component
        String displayName = itemSection.getString("display-name");
        if(displayName == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Display name is not valid in file: " + itemSection.getName(),
                    "It looks like this file has an invalid display name.",
                    "Please ensure the display name is defined correctly.",
                    "Fallback to default data: <red>Default Display Name"
            ));
            displayName = "<red>Default Display Name";
        }
        // real color and placeholder translation only on physical Item creation!
        Component displayNameComponent = Component.text(displayName);
        itemArguments.setDisplayName(displayNameComponent);

        // reading and converting lore to components
        List<Component> lore = itemSection.getStringList("lore").stream()
                .map(Component::text)
                .collect(Collectors.toList());
        itemArguments.setLore(lore);

        boolean unbreakable = itemSection.getBoolean("unbreakable", false);
        itemArguments.setUnbreakable(unbreakable);
        String durabilityCheck = itemSection.getString("durability", "-1");

        // Check if the durability is a valid integer
        if(!LightNumbers.isValidInteger(durabilityCheck)) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Durability is not valid in file: " + itemSection.getName(),
                    "It looks like this file has an invalid durability.",
                    "Durability must be a valid positiv number!",
                    "Fallback to default durability: 100"
            ));
            durabilityCheck = "100";
        }

        int durability = Integer.parseInt(durabilityCheck);
        // if unbreakable is true, set durability to -1
        itemArguments.setDurability(unbreakable ? -1 : durability);

        // Check if the durability is a valid number between 0 and 1000000
        int customModelData = itemSection.getInt("model-data", -1);

        if(!LightNumbers.isNumberInRange(customModelData, -1, 1000000)) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Custom Model Data is not valid in file: " + itemSection.getName(),
                    "It looks like this file has an invalid custom model data.",
                    "Custom Model Data must be between 0 and 1000000!",
                    "Fallback to default custom model data: -1"
            ));
            customModelData = -1;
        }
        itemArguments.setDurability(customModelData);

        // reading and converting given strings to ItemFlags
        List<ItemFlag> itemFlags = itemSection.getStringList("itemFlags").stream()
                .map(String::toUpperCase)
                .flatMap(flag -> {
                    try {
                        return Stream.of(ItemFlag.valueOf(flag));
                    } catch (IllegalArgumentException e) {
                        LightCore.instance.getConsolePrinter().printError(List.of(
                                "Invalid ItemFlag: " + flag + " in file: " + itemSection.getName(),
                                "This flag will be skipped."
                        ));
                        return Stream.empty();
                    }
                })
                .toList();

        itemArguments.setFlags(itemFlags);

        // reading and converting given strings to Enchantments with NamespacedKey and RegistryAccess
        Map<Enchantment, Integer> itemEnchantments = itemSection.getStringList("enchantments").stream()
                .flatMap(enchantment -> {
                    try {
                        String[] parts = enchantment.split(":");
                        String enchantmentName = parts[0].toLowerCase();
                        int level = parts.length > 1 && LightNumbers.isValidInteger(parts[1])
                                ? Integer.parseInt(parts[1])
                                : 1;

                        NamespacedKey key = NamespacedKey.fromString("minecraft:" + enchantmentName);
                        if (key == null) {
                            LightCore.instance.getConsolePrinter().printError(List.of(
                                    "Invalid NamespacedKey minecraft:" + enchantmentName + " in file: " + itemSection.getName(),
                                    "This enchantment will be skipped."
                            ));
                            return Stream.empty();
                        }

                        Enchantment enchant = RegistryAccess.registryAccess()
                                .getRegistry(RegistryKey.ENCHANTMENT)
                                .get(key);
                        if (enchant == null) {
                            LightCore.instance.getConsolePrinter().printError(List.of(
                                    "Invalid RegistryAccess for namespace " + key.getNamespace() + " in file: " + itemSection.getName(),
                                    "This enchantment will be skipped."
                            ));
                            return Stream.empty();
                        }

                        return Stream.of(Map.entry(enchant, level));
                    } catch (Exception e) {
                        LightCore.instance.getConsolePrinter().printError(List.of(
                                "Invalid Enchantment: " + enchantment + " in file: " + itemSection.getName(),
                                "This enchantment will be skipped."
                        ));
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        itemArguments.setEnchantments(itemEnchantments);

        return itemArguments;
    }


    /** Generates the recipes for the item
     * @param recipeSection The recipe section from the configuration
     * @return The recipe arguments for the item
     */
    public LightItem.RecipeArguments readRecipeArguments(ConfigurationSection recipeSection) {

        LightItem.RecipeArguments recipeArguments = new LightItem.RecipeArguments();

        boolean canBeCrafted = recipeSection.getBoolean("can-be-crafted", false);
        boolean shapeless = recipeSection.getBoolean("shapeless", false);
        int resultAmount = recipeSection.getInt("result-amount", 1);

        recipeArguments.setResultAmount(resultAmount);
        recipeArguments.setCanBeCrafted(canBeCrafted);
        recipeArguments.setShapeless(shapeless);

        RecipeType recipeType = RecipeType.valueOfNullable(recipeSection.getString("type", ""));

        if(recipeType == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Recipe type is not valid in file: " + recipeSection.getName(),
                    "It looks like this file has an invalid recipe type.",
                    "Please ensure the recipe type is defined correctly.",
                    "Fallback to default data: workbench"
            ));
            recipeType = RecipeType.WORKBENCH;
        }

        recipeArguments.setRecipeType(recipeType);

        Map<Integer, String> slotsMap = new HashMap<>();
        ConfigurationSection slotsSection = recipeSection.getConfigurationSection("slots");
        if (slotsSection != null) {
            for (String slotKey : slotsSection.getKeys(false)) {
                try {
                    int slot = Integer.parseInt(slotKey);
                    String value = slotsSection.getString(slotKey);
                    if (value != null) {
                        slotsMap.put(slot, value);
                    }
                } catch (NumberFormatException e) {
                    LightCore.instance.getConsolePrinter().printError(List.of(
                            "Invalid slot key: " + slotKey + " in file: " + recipeSection.getName(),
                            "Slot keys must be integers. This slot will be skipped.",
                            "Valid numbers are: 1-9 for workbench and 1-4 for player crafting!",
                            "This recipe will not be registered!"
                    ));
                    return null;
                }
            }
        }

        if (recipeType.equals(RecipeType.PLAYER) && slotsMap.size() > 4) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Too many ingredients for player crafting in file: " + recipeSection.getName(),
                    "Player crafting recipes can only have up to 4 ingredients. (1-4)",
                    "This recipe will not be registered!"
            ));
            return null;
        }

        recipeArguments.setRecipeSlotItems(slotsMap);

        return recipeArguments;
    }

    /**
     * Reads the recipe from the configuration and generates the recipe object
     * After reading the recipe, you can register it with the Bukkit API
     * @param itemID The ID of the item
     * @param recipeType The type of the recipe (WORKBENCH or PLAYER)
     * @param recipeSection The recipe section from the configuration
     * @return The generated recipe object
     */
    @Nullable
    public ShapedRecipe readShapedRecipe(String itemID, RecipeType recipeType, ConfigurationSection recipeSection, ItemStack resultItem) {

        NamespacedKey key = new NamespacedKey(plugin.getName().toLowerCase(), itemID);
        ShapedRecipe recipe = new ShapedRecipe(key, resultItem);

        if (recipeType.equals(RecipeType.WORKBENCH)) {
            recipe.shape("123", "456", "789");
        } else if (recipeType.equals(RecipeType.PLAYER)) {
            recipe.shape("12", "34");
        } else {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Recipe type is not valid in file: " + recipeSection.getName(),
                    "It looks like this file has an invalid recipe type.",
                    "Please ensure the recipe type is defined correctly.",
                    "Fallback to default data: workbench"
            ));
            recipe.shape("123", "456", "789");
        }

        ConfigurationSection slotsSection = recipeSection.getConfigurationSection("slots");
        Map<Integer, String> slotsMap = new HashMap<>();

        if (slotsSection != null) {
            for (String slotKey : slotsSection.getKeys(false)) {
                try {
                    int slot = Integer.parseInt(slotKey);
                    String value = slotsSection.getString(slotKey);
                    if (value != null) {
                        slotsMap.put(slot, value);
                    }
                } catch (NumberFormatException e) {
                    LightCore.instance.getConsolePrinter().printError(List.of(
                            "Invalid slot key: " + slotKey + " in file: " + recipeSection.getName(),
                            "Slot keys must be integers. This slot will be skipped.",
                            "Valid numbers are: 1-9 for workbench and 1-4 for player crafting!",
                            "This recipe will not be registered!"
                    ));
                    return null;
                }
            }
        } else {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Slots section not found in file: " + recipeSection.getName(),
                    "Please ensure the slots section is defined correctly.",
                    "This recipe will not be registered!"
            ));
            return null;
        }

        if (recipeType.equals(RecipeType.PLAYER) && slotsMap.size() > 4) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Too many ingredients for player crafting in file: " + recipeSection.getName(),
                    "Player crafting recipes can only have up to 4 ingredients. (1-4)",
                    "This recipe will not be registered!"
            ));
            return null;
        }

        slotsMap.forEach((slot, value) -> {
            try {
                String[] parts = value.split(":");
                if (parts.length < 2) {
                    return;
                }

                String namespace = parts[0];
                ItemType itemType = ItemType.valueOfNullable(parts[1]);

                if (itemType == null) {
                    LightCore.instance.getConsolePrinter().printError(List.of(
                            "Item type is not valid in file: " + recipeSection.getName(),
                            "It looks like this file has an invalid item type.",
                            "Please ensure the item type is defined correctly.",
                            "This slot will be skipped."
                    ));
                    return;
                }

                if (itemType.equals(ItemType.VANILLA)) {
                    String materialName = parts[1];
                    if (!LightNumbers.isNumber(parts[2])) {
                        LightCore.instance.getConsolePrinter().printError(List.of(
                                "Invalid amount for slot " + slot + ": " + parts[2] + " in file: " + recipeSection.getName(),
                                "Amount must be a valid number.",
                                "This slot will be skipped."
                        ));
                        return;
                    }

                    int amount = Integer.parseInt(parts[2]);

                    Material material = Material.matchMaterial(materialName);
                    if (material == null) {
                        LightCore.instance.getConsolePrinter().printError(List.of(
                                "Invalid material name: " + materialName + " in file: " + recipeSection.getName(),
                                "Material name must be a valid material.",
                                "This slot will be skipped."
                        ));
                        return;
                    }

                    ItemStack itemStack = new ItemStack(material, amount);

                    char ingredientKey = (char) ('A' + slot - 1);
                    recipe.setIngredient(ingredientKey, itemStack);
                } else {
                    LightCore.instance.getConsolePrinter().printError(List.of(
                            "Invalid namespace: " + namespace + " in file: " + recipeSection.getName(),
                            "Currently only vanilla items are supported.",
                            "This slot will be skipped."
                    ));
                }
            } catch (Exception e) {
                LightCore.instance.getConsolePrinter().printError(List.of(
                        "Failed to parse slot " + slot + " with value: " + value + " in file: " + recipeSection.getName(),
                        "Error: " + e.getMessage(),
                        "This slot will be skipped."
                ));
            }
        });

        return recipe;
    }

    /**
     * Reads the recipe from the configuration and generates the recipe object
     * After reading the recipe, you can register it with the Bukkit API
     * @param itemID The ID of the item
     * @param recipeType The type of the recipe (WORKBENCH or PLAYER)
     * @param recipeSection The recipe section from the configuration
     * @return The generated recipe object
     */
    @Nullable
    public ShapelessRecipe readShapelessRecipe(String itemID, RecipeType recipeType, ConfigurationSection recipeSection, ItemStack resultItem) {

        NamespacedKey key = new NamespacedKey(plugin.getName().toLowerCase(), itemID);
        ShapelessRecipe recipe = new ShapelessRecipe(key, resultItem);

        ConfigurationSection slotsSection = recipeSection.getConfigurationSection("slots");
        if (slotsSection == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Slots section not found in file: " + recipeSection.getName(),
                    "Please ensure the slots section is defined correctly.",
                    "This recipe will not be registered!"
            ));
            return null;
        }

        if (recipeType.equals(RecipeType.PLAYER) && slotsSection.getKeys(false).size() > 4) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Too many ingredients for player crafting in file: " + recipeSection.getName(),
                    "Player crafting recipes can only have up to 4 ingredients. (1-4)",
                    "This recipe will not be registered!"
            ));
            return null;
        }

        // read ingredients from slots section
        for (String slotKey : slotsSection.getKeys(false)) {
            try {
                String value = slotsSection.getString(slotKey);
                if (value == null || value.isEmpty()) {
                    continue; // Skip empty ingredient (slots)
                }

                String[] parts = value.split(":");
                if (parts.length < 2) {
                    LightCore.instance.getConsolePrinter().printError(List.of(
                            "Invalid slot value: " + value + " in file: " + recipeSection.getName(),
                            "Slot values must follow the format 'namespace:item:amount'.",
                            "This ingredient will be skipped."
                    ));
                    continue;
                }

                ItemType itemType = ItemType.valueOfNullable(parts[0]);
                if (itemType == null || !itemType.equals(ItemType.VANILLA)) {
                    LightCore.instance.getConsolePrinter().printError(List.of(
                            "Unsupported or invalid ItemType: " + parts[0] + " in file: " + recipeSection.getName(),
                            "Currently only 'vanilla' items are supported.",
                            "This ingredient will be skipped."
                    ));
                    continue;
                }

                String materialName = parts[1];
                int amount = LightNumbers.isNumber(parts[2]) ? Integer.parseInt(parts[2]) : 1;

                Material material = Material.matchMaterial(materialName);
                if (material == null) {
                    LightCore.instance.getConsolePrinter().printError(List.of(
                            "Invalid material name: " + materialName + " in file: " + recipeSection.getName(),
                            "Material name must be a valid material.",
                            "This ingredient will be skipped."
                    ));
                    continue;
                }

                ItemStack ingredient = new ItemStack(material, amount);
                recipe.addIngredient(ingredient);
            } catch (Exception e) {
                LightCore.instance.getConsolePrinter().printError(List.of(
                        "Failed to parse slot " + slotKey + " with value: " + slotsSection.getString(slotKey) + " in file: " + recipeSection.getName(),
                        "Error: " + e.getMessage(),
                        "This ingredient will be skipped."
                ));
            }
        }

        return recipe;
    }

}
