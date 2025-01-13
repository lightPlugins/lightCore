package io.lightstudios.core.world.events;

import io.lightstudios.core.LightCore;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlacedByPlayer implements Listener {

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        LightCore.instance.getWorldManager().setBlockPlacedByPlayer(block, player);
    }
}
