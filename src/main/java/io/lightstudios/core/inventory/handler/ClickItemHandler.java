package io.lightstudios.core.inventory.handler;

import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import io.lightstudios.core.LightCore;
import io.lightstudios.core.actions.ActionHandler;
import io.lightstudios.core.util.LightNumbers;
import io.lightstudios.core.util.SkullUtil;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

/**
 * This class handles the creation and management of GUI items for players.
 * It processes configuration data to generate items with specific properties,
 * applies placeholders, and handles actions associated with the items.
 * <p>
 * The main functionalities include:
 * - Applying placeholders to item properties.
 * - Translating placeholders in item display names and lore.
 * - Constructing ItemStack objects based on configuration data.
 * - Reading and processing actions associated with the items.
 * <p>
 * Author: lightPlugins
 * Copyright: © 2023 [lightStudios]. All rights reserved.
 * You may not use, distribute, or modify this code without explicit permission.
 */

@Getter
public class ClickItemHandler {

    private final ConfigurationSection GUI_ITEM_ARGS;
    private final ConfigurationSection PLACEHOLDERS;
    private ConfigurationSection EXTRA_SECTION;
    private final Player player;
    private String item;
    private String displayName;
    private List<String> lore = new ArrayList<>();
    private final Map<String, String> placeholders = new HashMap<>();
    private int modelData;
    private String headData;
    private Slot slot;

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    private final List<String> actionsSection;
    private final List<ActionHandler> actionHandlers = new ArrayList<>();
    private final int extraSlot = 1;

    private List<String> extraActionsSection;
    private final List<ActionHandler> extraActionHandlers = new ArrayList<>();

    /**
     * Constructs a new ClickItemHandler.
     *
     * @param section the configuration section containing item and action data
     * @param player the player for whom the item is to be handled
     */
    public ClickItemHandler(ConfigurationSection section, Player player) {

        this.GUI_ITEM_ARGS = section.getConfigurationSection("args");
        this.actionsSection = section.getStringList("args.click-actions");
        this.PLACEHOLDERS = section.getConfigurationSection("args.placeholders");
        this.player = player;

        applyPlaceholders();
        readItemVariables();
        translatePlaceholders();
        translateLore();
        loadActions();

        this.itemStack = getGuiItem();
        this.itemMeta = itemStack.getItemMeta();

    }

    /**
     * Sets the extra section for the ClickItemHandler.
     * The extra section is used for separate configuration data for example rewards: ...
     *
     * @param extraSection the extra section to set
     */
    public void setExtraSection(ConfigurationSection extraSection) {
        this.EXTRA_SECTION = extraSection;
        readExtraActions();
    }

    /**
     * Applies placeholders to the placeholders map.
     * Iterates through all keys in the PLACEHOLDERS configuration section,
     * retrieves the corresponding value, and sets the placeholder using
     * PlaceholderAPI for the given player.
     */
    private void applyPlaceholders() {

        if(PLACEHOLDERS == null) {
            return;
        }

        for(String placeholder : PLACEHOLDERS.getKeys(true)) {
            placeholders.put(
                    placeholder,
                    PlaceholderAPI.setPlaceholders(
                            player,
                            Objects.requireNonNull(PLACEHOLDERS.getString(placeholder))
                    ));
        }
    }

    /**
     * Translates placeholders in the displayName and headData fields.
     * Iterates through all keys in the placeholders map and replaces
     * occurrences of each placeholder in the displayName and headData
     * with their corresponding values.
     */
    private void translatePlaceholders() {
        for(String key : placeholders.keySet()) {
            this.displayName = displayName.replace("#" + key + "#", placeholders.get(key));
            this.headData = headData.replace("#" + key + "#", placeholders.get(key));
        }
    }

    /**
     * Translates placeholders in the lore list.
     * Iterates through each line in the lore list and replaces
     * occurrences of each placeholder with their corresponding values.
     * The translated lines are then processed through the colorTranslation
     * method and added to the translatedLore list.
     */
    private void translateLore() {
        List<String> translatedLore = new ArrayList<>();
        for(String line : lore) {
            for(String key : placeholders.keySet()) {
                line = line.replace("#" + key + "#", placeholders.get(key));
            }
            translatedLore.add(LightCore.instance.getColorTranslation().adventureTranslator(line, player));
        }
        this.lore = translatedLore;
    }

    /**
     * Retrieves and sets the item content from the GUI_ITEM_ARGS configuration section.
     * This includes the item type, display name, lore, and head data.
     */
    private void readItemVariables() {

        this.item = GUI_ITEM_ARGS.getString("item");
        this.slot = Slot.fromIndex(GUI_ITEM_ARGS.getInt("slot"));
        this.displayName = GUI_ITEM_ARGS.getString("displayname");
        this.lore = GUI_ITEM_ARGS.getStringList("lore");
        this.headData = GUI_ITEM_ARGS.getString("head-data");

    }

    /**
     * Constructs an ItemStack based on the configuration and player data.
     *
     * @return the constructed ItemStack
     */
    public ItemStack getGuiItem() {

        ItemStack itemStack = new ItemStack(Material.STONE, 1);

        String[] splitItem = item.split(" ");
        Material material = Material.getMaterial(splitItem[0].toUpperCase());

        if(LightNumbers.isNumber(splitItem[1])) {
            itemStack.setAmount(Integer.parseInt(splitItem[1]));
        }

        if(material != null) {
            itemStack.setType(material);
        } else {
            LightCore.instance.getConsolePrinter().printError("Invalid material: " + splitItem[0] + " in file: " + GUI_ITEM_ARGS.getCurrentPath());
            LightCore.instance.getConsolePrinter().printError("Material must be a valid material.");
            LightCore.instance.getConsolePrinter().printError("It is set to the backup material -> Stone");
            return new ItemStack(Material.STONE, 1);
        }

        OfflinePlayer offlinePlayer = Bukkit.getPlayer(headData);

        if(itemStack.getItemMeta() instanceof SkullMeta) {
            if(offlinePlayer != null) {
                itemStack = SkullUtil.getPlayerSkull(offlinePlayer.getPlayer());
            } else {
                LightCore.instance.getConsolePrinter().printError("Invalid head data: " + headData + " in file: " + GUI_ITEM_ARGS.getCurrentPath());
                LightCore.instance.getConsolePrinter().printError("Head data must be a valid UUID or name.");
                LightCore.instance.getConsolePrinter().printError("It is set to the backup material -> Stone");
                return new ItemStack(Material.STONE, 1);
            }
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null) {
            return null;
        }

        itemMeta.setDisplayName(LightCore.instance.getColorTranslation().adventureTranslator(displayName, player));

        for(String split : splitItem) {

            // Check if the split contains model-data
            if(split.contains("model-data")) {
                String[] splitModelData = split.split(":");
                if(LightNumbers.isNumber(splitModelData[1])) {
                    itemMeta.setCustomModelData(Integer.parseInt(splitModelData[1]));
                    this.modelData = Integer.parseInt(splitModelData[1]);
                } else {
                    LightCore.instance.getConsolePrinter().printError("Invalid model data: " + splitModelData[1] + " in file: "
                            + GUI_ITEM_ARGS.getCurrentPath());
                    LightCore.instance.getConsolePrinter().printError("Model data must be a number.");
                    LightCore.instance.getConsolePrinter().printError("Syntax: model-data:12345");
                }
            }

            // Check if the split contains enchantments and apply them
            if(split.contains("enchant") &! split.contains("hide_enchants")) {
                String[] enchantSplit = split.split(":");

                if(enchantSplit.length > 3) {
                    LightCore.instance.getConsolePrinter().printError("Invalid enchantment: " + enchantSplit[1] + " in file: "
                            + GUI_ITEM_ARGS.getCurrentPath());
                    LightCore.instance.getConsolePrinter().printError("Enchantment must be a valid enchantment.");
                    LightCore.instance.getConsolePrinter().printError("Syntax: enchant:flame");
                } else {
                    // Enchantment.getByName() is deprecated, but it is still used in this code
                    // TODO: find a better way to get enchantments from the string
                    Enchantment enchantment = Enchantment.getByName(enchantSplit[1].toUpperCase());
                    int level = Integer.parseInt(enchantSplit[2]);
                    if(enchantment != null) {
                        itemMeta.addEnchant(enchantment, level, true);
                    } else {
                        LightCore.instance.getConsolePrinter().printError("Invalid enchantment: " + enchantSplit[1] + " in file: "
                                + GUI_ITEM_ARGS.getCurrentPath());
                        LightCore.instance.getConsolePrinter().printError("Enchantment must be a valid enchantment.");
                        LightCore.instance.getConsolePrinter().printError("Syntax: enchant:flame");
                    }
                }
            }

            // Check if the item should glow
            if(split.equalsIgnoreCase("glow")) {
                itemMeta.addEnchant(Enchantment.FLAME, 1, true);
            }

            // Check if the split contains item flags
            try {
                ItemFlag itemFlag = ItemFlag.valueOf(split.toUpperCase());
                itemMeta.addItemFlags(itemFlag);
            } catch (IllegalArgumentException ignored) { }
        }

        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Replaces a specific line in the lore with a new line.
     * For example:
     * -> lineToReplace = '- #reward#'
     * -> newLine = "replacement"
     * - rewards:
     *      - '...'
     *      - '- #reward#'
     *      - '...'
     * - rewards:
     *      - '...'
     *      - '- replacement'
     *      - '...'
     *
     * @param lineToReplace the line to replace
     * @param newLine the new line to set for the replacement
     */
    public void replaceLoreLine(String lineToReplace, String newLine) {

        List<String> newLore = new ArrayList<>();

        if(itemMeta.getLore() == null) {
            return;
        }

        for(String line : lore) {
            if(line.contains(lineToReplace)) {
                line = line.replace(lineToReplace, newLine);
            }
            newLore.add(line);
        }

        itemMeta.setLore(newLore);
        itemStack.setItemMeta(itemMeta);
    }

    /**
     * Replaces a specific line in the lore with a new line.
     * For example:
     * -> lineToReplace = '- #reward#'
     * -> newLines = ("replacement1", "replacement2", "replacement3")
     * - rewards:
     *      - '...'
     *      - '- #reward#'
     *      - '...'
     * - rewards:
     *      - '...'
     *      - '- replacement1'
     *      - '- replacement2'
     *      - '- replacement3'
     *      - '...'
     *
     * @param placeholderToReplace the line to replace
     * @param newLines the new lines to set for the replacement (multiple lines)
     */
    public void replaceMultipleLoreLines(String placeholderToReplace, List<String> newLines) {

        List<String> newLore = new ArrayList<>();

        if (itemMeta.getLore() == null) {
            return;
        }

        for (String line : lore) {
            if (line.contains(placeholderToReplace)) {
                for (String newLine : newLines) {
                    newLore.add(line.replace(placeholderToReplace, newLine));
                }
                continue;
            }
            newLore.add(line);
        }

        itemMeta.setLore(newLore);
        itemStack.setItemMeta(itemMeta);
    }

    /**
     * Replaces a specified placeholder in the display name with a new string.
     * @param replacement the placeholder to replace
     * @param newDisplayName the new replacement for the placeholder
     */
    public void replaceDisplayName(String replacement, String newDisplayName) {
        if(itemMeta.getDisplayName().contains(replacement)) {
            itemMeta.setDisplayName(itemMeta.getDisplayName().replace(replacement, newDisplayName));
        }
        itemStack.setItemMeta(itemMeta);
    }

    /**
     * Set the item amount for the itemStack.
     * @param amount the amount to set
     */
    public void setItemAmount(int amount) {
        itemStack.setAmount(amount);
    }

    /**
     * Reads and processes actions from the actionsSection list.
     * Iterates through each action, replaces placeholders with their corresponding values,
     * and adds the processed action to the actionHandlers list.
     */
    private void loadActions() {
        actionsSection.forEach(action -> {
            for(String key : placeholders.keySet()) {
                action = action.replace("#" + key + "#", placeholders.get(key));
            }
            actionHandlers.add(new ActionHandler(player, action));
        });
    }

    /**
     * Reads and processes extra actions from the extraActionsSection list.
     * Iterates through each action, replaces placeholders with their corresponding values,
     * and adds the processed action to the extraActionHandlers list.
     */
    private void readExtraActions() {
        this.extraActionsSection = EXTRA_SECTION.getStringList("actions");
        LightCore.instance.getConsolePrinter().printInfo("Extra actions: " + extraActionsSection);

        extraActionsSection.forEach(action -> {
            for(String key : placeholders.keySet()) {
                action = action.replace("#" + key + "#", placeholders.get(key));
            }
            extraActionHandlers.add(new ActionHandler(player, action));
        });
    }

}
