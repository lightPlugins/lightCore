package io.lightstudios.core.util;

import io.lightstudios.core.LightCore;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class LightStrings {

    // example String
    //
    // item: 'diamond:1 enchant:unbreaking:3 hide_attributes hide_enchants lore:This is a;new line model-data:1337'
    public static ItemStack generateItemFromString(String itemString) {

        String[] parts = itemString.split(" ");

        if(parts.length == 0) {
            return null;
        }
        String[] materialPart = parts[0].split(":");
        Material material = Material.valueOf(materialPart[0].toUpperCase());
        // check if the provided material is a real material
        if(!material.isItem()) { return null; }
        // check if the amount is greater than 0 and less than 99+
        int amount = Math.min(Integer.parseInt(materialPart[1]), 99);
        if(amount < 1) { amount = 1; }

        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) { return null; }

        for (String part : parts) {

            // Enchantments part
            if(part.contains("enchant:")) {

                String[] enchantmentParts = part.split(":");

                if(enchantmentParts.length != 3) { continue; }
                int level = Integer.parseInt(enchantmentParts[2]);
                if(level < 1) { continue; }

                NamespacedKey enchantmentKey = NamespacedKey.fromString(enchantmentParts[1]);
                if(enchantmentKey == null) { continue; }

                Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getOrThrow(enchantmentKey);
                itemMeta.addEnchant(enchantment, level, true);

            }

            if(part.contains("hide_tooltip")) {
                itemMeta.setHideTooltip(true);
            }

            if(part.contains("hide_attributes")) {
                itemMeta.getItemFlags().add(ItemFlag.HIDE_ATTRIBUTES);
            }

            if(part.contains("hide_enchants")) {
                itemMeta.getItemFlags().add(ItemFlag.HIDE_ENCHANTS);
            }

            if(part.contains("hide_unbreakable")) {
                itemMeta.getItemFlags().add(ItemFlag.HIDE_UNBREAKABLE);
            }

            if(part.contains("unbreakable")) {
                itemMeta.setUnbreakable(true);
            }

            if(part.contains("model-data")) {
                int customModelData = Integer.parseInt(part.split(":")[1]);
                if(customModelData < 0) { continue; }
                itemMeta.setCustomModelData(customModelData);
            }


            if(part.contains("displayname:")) {
                String[] displayNameParts = part.split(":");
                if(displayNameParts.length != 2) { continue; }
                itemMeta.displayName(LightCore.instance.getColorTranslation().miniMessage(displayNameParts[1]));
            }

            if(part.contains("lore")) {
                String[] loreParts = part.split(":");
                List<String> loreLines = Arrays.stream(loreParts[1].split(";")).toList();
                if(loreLines.isEmpty()) { continue; }
                itemMeta.lore(loreLines.stream().map(loreLine -> LightCore.instance.getColorTranslation().miniMessage(loreLine)).toList());
            }

        }

        if(itemStack.setItemMeta(itemMeta)) {
            return itemStack;
        }

        return null;
    }
}
