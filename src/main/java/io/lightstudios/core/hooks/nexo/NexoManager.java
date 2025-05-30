package io.lightstudios.core.hooks.nexo;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.items.NexoMeta;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NexoManager {

    public ItemStack getNexoItemByID(String id) {
        ItemBuilder itemBuilder = NexoItems.itemFromId(id);
        if(itemBuilder != null) {
            return itemBuilder.build();
        }
        return new ItemStack(Material.DIRT, 1);
    }
}
