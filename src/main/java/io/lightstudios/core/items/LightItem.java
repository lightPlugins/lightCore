package io.lightstudios.core.items;

import io.lightstudios.core.LightCore;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class LightItem {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;
    private final Component itemDisplayName;
    private final List<Component> itemLore;
    private final Material itemMaterial;
    private final int itemStackSize;
    private final String itemID;
    private final int customModelData;
    private final HashMap<Enchantment, Integer> enchantments;
    private final List<ItemFlag> itemFlags;

    private final boolean nexoItem;

    private LightItem(Builder builder) {
        this.nexoItem = builder.nexoItem;
        this.itemMaterial = builder.itemMaterial;
        this.itemStackSize = builder.itemStackSize;
        this.itemDisplayName = builder.itemDisplayName;
        this.itemLore = builder.itemLore;
        this.enchantments = builder.enchantments;
        this.itemFlags = builder.itemFlags;
        this.customModelData = builder.customModelData;
        this.itemID = builder.itemID;

        if(nexoItem) {
            ItemStack nexoStack = LightCore.instance.getHookManager().getNexoManager().getNexoItemByID(itemID);
            if(nexoStack != null) {
                this.itemStack = nexoStack;
            } else {
                LightCore.instance.getConsolePrinter().printConfigError(List.of(
                        "NexoItem is null for item id: §4" + itemID,
                        "Please check if the item is registered in the Nexo plugin",
                        "and the item ID is correct!"
                ));
                this.itemStack = new ItemStack(Material.STONE, itemStackSize);
            }
        } else {
            this.itemStack = new ItemStack(itemMaterial, itemStackSize);
        }

        this.itemMeta = itemStack.getItemMeta();
        // allow for stack sizes between 65 and 99 (1.21.4 only)
        if(itemStackSize > 64 && itemStackSize <= 99) {
            this.itemMeta.setMaxStackSize(itemStackSize);
        }
        if (itemDisplayName != null) {
            itemMeta.displayName(itemDisplayName.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }
        if (itemLore != null) {
            PlainTextComponentSerializer plainTextSerializer = PlainTextComponentSerializer.plainText();
            MiniMessage miniMessage = MiniMessage.miniMessage();

            List<Component> translatedLore = itemLore.stream()
                    .map(lore -> miniMessage.deserialize(plainTextSerializer.serialize(lore))
                            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                    .collect(Collectors.toList());
            itemMeta.lore(translatedLore);
        }


        // apply enchantments from the hashmap
        for (Enchantment enchantment : enchantments.keySet()) {
            if(enchantment != null) {
                itemMeta.addEnchant(enchantment, enchantments.get(enchantment), true);
            } else {
                LightCore.instance.getConsolePrinter().printConfigError(List.of(
                        "Enchantment is null for item: " + itemID
                ));
            }
        }

        // apply existing attribute modifiers into the meta, before adding new ones !
        // -> https://github.com/PaperMC/Paper/issues/10693
        for (Map.Entry<Attribute, AttributeModifier> modifier : itemStack.getType().getDefaultAttributeModifiers().entries()) {
            itemMeta.addAttributeModifier(modifier.getKey(), modifier.getValue());
        }

        // apply item flags from the list
        for (ItemFlag itemFlag : itemFlags) {
            if(itemFlag != null) {
                itemMeta.addItemFlags(itemFlag);
            } else {
                LightCore.instance.getConsolePrinter().printConfigError(List.of(
                        "ItemFlag is null for item: " + itemID
                ));
            }
        }
        // Add a key-value pair to store the item ID (the config file name without the extension)
        // example: "pluginname:itemid" -> 'lightcore:example_item' -> 'nexo:example-item'.
        NamespacedKey fromConfig = new NamespacedKey("lightcore", "item_id");
        itemMeta.getPersistentDataContainer().set(fromConfig, PersistentDataType.STRING, this.itemID);
        // finally, set the item meta to the item stack
        this.itemStack.setItemMeta(itemMeta);
    }

    public ItemStack translatePlaceholders(Player player) {
        PlainTextComponentSerializer plainTextSerializer = PlainTextComponentSerializer.plainText();

        if (itemDisplayName != null) {
            String plainName = plainTextSerializer.serialize(itemDisplayName);
            Component translatedName = Component.text(PlaceholderAPI.setPlaceholders(player, plainName));
            itemMeta.displayName(translatedName);
        }

        if (itemLore != null) {

            List<Component> translatedLore = itemLore.stream()
                    .map(lore -> {
                        String plainLore = plainTextSerializer.serialize(lore);
                        return Component.text(PlaceholderAPI.setPlaceholders(player, plainLore));
                    })
                    .collect(Collectors.toList());
            itemMeta.lore(translatedLore);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static class Builder {
        private Material itemMaterial = Material.STONE;
        private int itemStackSize = 1;
        private Component itemDisplayName = Component.text("No Display Name Set");
        private List<Component> itemLore = List.of(Component.text("No Lore Set"));
        private int customModelData = 0;
        private String itemID;
        private HashMap<Enchantment, Integer> enchantments = new HashMap<>();
        private List<ItemFlag> itemFlags = List.of();
        private boolean nexoItem = false;

        public Builder setMaterial(Material material) {
            this.itemMaterial = material;
            return this;
        }

        public Builder setStackSize(int stackSize) {
            this.itemStackSize = stackSize;
            return this;
        }

        public Builder setCustomName(Component displayName) {
            this.itemDisplayName = displayName;
            return this;
        }

        public Builder setLore(List<Component> lore) {
            this.itemLore = lore;
            return this;
        }

        public Builder setEnchantments(HashMap<Enchantment, Integer> enchantments) {
            this.enchantments = enchantments;
            return this;
        }

        public Builder setItemFlags(List<ItemFlag> itemFlags) {
            this.itemFlags = itemFlags;
            return this;
        }

        public Builder setCustomModelData(int customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        public Builder setNexoItem(boolean nexoItem) {
            this.nexoItem = nexoItem;
            return this;
        }

        public Builder setID(String id) {
            this.itemID = id;
            return this;
        }

        public LightItem build() {
            return new LightItem(this);
        }
    }
}