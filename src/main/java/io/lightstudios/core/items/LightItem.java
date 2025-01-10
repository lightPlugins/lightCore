package io.lightstudios.core.items;

import com.nexomc.nexo.api.NexoItems;
import io.lightstudios.core.LightCore;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class LightItem {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;
    private final Component itemCustomName;
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
        this.itemCustomName = builder.itemCustomName;
        this.itemLore = builder.itemLore;
        this.enchantments = builder.enchantments;
        this.itemFlags = builder.itemFlags;
        this.customModelData = builder.customModelData;
        this.itemID = builder.itemID;

        if(nexoItem) {
            this.itemStack = LightCore.instance.getHookManager().getNexoManager().getNexoItemByID(itemID);
        } else {
            this.itemStack = new ItemStack(itemMaterial, itemStackSize);
        }

        this.itemMeta = itemStack.getItemMeta();
        // allow for stack sizes between 65 and 99 (1.21.4 only)
        if(itemStackSize > 64 && itemStackSize <= 99) {
            this.itemMeta.setMaxStackSize(itemStackSize);
        }
        if (itemCustomName != null) {
            itemMeta.customName(itemCustomName);
        }
        if (itemLore != null) {
            itemMeta.lore(itemLore);
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

        // Add a key-value pair to indicate this is a LightItem powered by lightCore
        NamespacedKey isLightItem = new NamespacedKey("lightcore", "is_light_item");
        // Add a key-value pair to store the item ID (the config file name without the extension)
        NamespacedKey fromConfig = new NamespacedKey("lightcore", "item_id");
        itemMeta.getPersistentDataContainer().set(isLightItem, PersistentDataType.BOOLEAN, true);
        itemMeta.getPersistentDataContainer().set(fromConfig, PersistentDataType.STRING, this.itemID);
        // finally, set the item meta to the item stack
        itemStack.setItemMeta(itemMeta);
    }

    public ItemStack translatePlaceholders(Player player) {
        PlainTextComponentSerializer plainTextSerializer = PlainTextComponentSerializer.plainText();

        if (itemCustomName != null) {
            String plainName = plainTextSerializer.serialize(itemCustomName);
            Component translatedName = Component.text(PlaceholderAPI.setPlaceholders(player, plainName));
            itemMeta.customName(translatedName);
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
        private Component itemCustomName = Component.text("No Display Name Set");
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
            this.itemCustomName = displayName;
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