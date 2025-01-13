package io.lightstudios.core.world;

import io.lightstudios.core.util.namespace.LightNamespaceKey;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

public class WorldManager {

    public boolean isBlockPlacedByPlayer(Block block) {
        if (block instanceof PersistentDataHolder dataHolder) {
            PersistentDataContainer dataContainer = dataHolder.getPersistentDataContainer();
            NamespacedKey placedByPlayerKey = LightNamespaceKey.IS_PLAYER_PLACED.getKey();
            // check if block is placed by player
            return dataContainer.has(placedByPlayerKey, PersistentDataType.BOOLEAN);
        }
        return false;
    }

    public void setBlockPlacedByPlayer(Block block, Player player) {
        // check if block is already placed by player
        if(isBlockPlacedByPlayer(block)) {
            return;
        }
        // set custom nbt tag to indicate block is placed by player
        if (block instanceof PersistentDataHolder dataHolder) {
            PersistentDataContainer dataContainer = dataHolder.getPersistentDataContainer();
            NamespacedKey placedByPlayerKey = LightNamespaceKey.IS_PLAYER_PLACED.getKey();
            // Set the custom NBT tag to indicate it was placed by a player.
            dataContainer.set(placedByPlayerKey, PersistentDataType.BOOLEAN, true);
        }
    }

}
