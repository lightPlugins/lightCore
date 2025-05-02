package io.lightstudios.core.items.models;

import io.lightstudios.core.items.types.ItemType;
import io.lightstudios.core.items.types.RecipeSlot;
import io.lightstudios.core.items.types.RecipeType;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class LightItem {

    private ConfigurationSection itemSection;
    private ConfigurationSection recipeSection;
    private ConfigurationSection extraSection;
    private String materialOrID;
    private ItemType itemType;
    private ItemStack itemStack;

    private ItemArguments itemArguments;
    private RecipeArguments recipeArguments;

    @Getter
    @Setter
    public static class ItemArguments {
        private Component displayName;
        private List<Component> lore;
        private boolean unbreakable;
        private int durability;
        private List<ItemFlag> flags;
        private Map<Enchantment, Integer> enchantments;
    }

    @Getter
    @Setter
    public static class RecipeArguments {
        private boolean canBeCrafted;
        private RecipeType recipeType;
        private Map<Integer, String> recipeSlotItems;
        private int resultAmount;
        private boolean shapeless;
        private ShapelessRecipe shapelessRecipe;
        private ShapedRecipe shapedRecipe;
    }

}
