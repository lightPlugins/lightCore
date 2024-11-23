package io.lightstudios.core.tests;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import de.tr7zw.changeme.nbtapi.NBT;
import io.lightstudios.core.LightCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ClientSideLore implements Listener {

    private final ProtocolManager protocolManager;

    public ClientSideLore(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    private ItemStack setData(ItemStack item) {
        if (item == null || item.getType().isAir() || item.getAmount() == 0) {
            throw new IllegalArgumentException("ItemStack can't be null/air/amount of 0!");
        }

        NBT.modify(item, nbt -> {
            nbt.setString("lore", "§4Das ist eine Client Side Lore");
        });

        return item.clone();
    }

    private void applyNBTLore(Player player, ItemStack item, int rawSlow) {
        NBT.modify(item, nbt -> {
            String lore = nbt.getString("lore");
            LightCore.instance.getConsolePrinter().printInfo("Lore: " + lore);
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setLore(List.of(lore));
                item.setItemMeta(itemMeta);
            }
        });
        sendLorePacket(player, item, rawSlow);
    }

    private void sendLorePacket(Player player, ItemStack itemStack, int rawSlow) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SET_SLOT);
        packet.getIntegers().write(0, 0); // Window ID (0 for player inventory)
        packet.getIntegers().write(2, rawSlow); // Slot index (first slot of the main inventory)
        packet.getItemModifier().write(0, itemStack);

        try {
            protocolManager.sendServerPacket(player, packet);
            LightCore.instance.getConsolePrinter().printInfo("Sent lore packet to player " + player.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(event.isShiftClick()) {
            return;
        }
        ItemStack item = event.getCurrentItem();
        if (item != null) {
            if(item.getType().isAir() && event.getCursor() != null) {
                applyNBTLore(player, event.getCursor().clone() , event.getRawSlot());
                return;
            }
            applyNBTLore(player, item.clone(), event.getRawSlot());
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (!itemInHand.getType().isAir()) {
                NBT.modify(itemInHand, nbt -> {
                    nbt.setString("lore", "§4Das ist eine Client Side Lore");
                });

                ItemStack modifiedItem = setData(itemInHand);
                player.getInventory().setItemInMainHand(modifiedItem);
            }
        }
    }
}