package io.lightstudios.core.hooks.mmoitems;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.inventory.ItemStack;

public class MmoItemsManager {

    public MMOItems getMMOItems() {
        return MMOItems.plugin;
    }

    public MMOItem getMMOItemByID(String typeID, String itemID) {
        return getMMOItems().getMMOItem(Type.get(typeID), itemID);
    }

    public ItemStack getStackByMMOItem(String typeID, String itemID) {
        return getMMOItemByID(typeID, itemID).newBuilder().build();
    }
}
