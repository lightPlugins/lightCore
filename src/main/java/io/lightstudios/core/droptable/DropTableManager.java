package io.lightstudios.core.droptable;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.droptable.model.DropTable;
import io.lightstudios.core.util.LightStrings;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

@Getter
public class DropTableManager {

    private final DropTable dropTable;
    private final Player player;
    private final Map<UUID, Player> droppedItemList = new HashMap<>();

    public DropTableManager(Player player, DropTable dropTable) {
        this.dropTable = dropTable;
        this.player = player;
        checkChance(player);
    }
    
    public void checkChance(Player player) {

        dropTable.getDropsList().forEach((id, drop) -> {
            double dropChance = drop.getChanceAsDouble();
            double randomValue = Math.random() * 100;

            LightCore.instance.getConsolePrinter().printInfo(List.of(
                    "Random chance value: -> " + randomValue + " %",
                    "Item drop chance: -> " + dropChance + " %"
            ));

            if (randomValue <= dropChance) {
                // Chance met
                executeDrop(player);
                executeActions(player);
            }

        });
    }

    private void executeDrop(Player player) {
        dropTable.getDropsList().forEach((id, drop) -> {
            if (drop.getMmoItemsItem() != null) {
                handleMMOItemsDrop(player, id, drop.getMmoItemsItem(), drop.getDropLocation());
            }

            if (drop.getNexoItem() != null) {
                handleNexoDrop(player, id, drop.getNexoItem(), drop.getDropLocation());
            }

            if (drop.getVanillaItem() != null) {
                handleVanillaDrop(player, id, drop.getVanillaItem(), drop.getDropLocation());
            }
        });
    }

    private void executeActions(Player player) {

        dropTable.getDropsList().forEach((id, drop) -> {

            DropTable.Actions.Message message = drop.getActions().getMessage();
            DropTable.Actions.Title title = drop.getActions().getTitle();
            DropTable.Actions.Sound sound = drop.getActions().getSound();

            if(message != null) {
                sendMessage(player, message.getMessage());
            }

            if(title != null) {
                sendTitle(player, title.getTitle());
            }

            if(sound != null) {
                playSound(player, sound);
            }
        });
    }

    private void handleMMOItemsDrop(Player player, String id, DropTable.Drops.MMOItemsItem mmoItemsItem, Location dropLocation) {
        DropTable.ItemSettings itemSettings = mmoItemsItem.getItemSettings();

        if (itemSettings == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Unable to drop MMOItems item with id " + id + ".",
                    "Please check your dropTables for correct settings!",
                    "Item settings are null!"
            ));
            return;
        }

        ItemStack itemStack = initializeItemStack(mmoItemsItem.getItemStack(), mmoItemsItem.getAmountMin(), mmoItemsItem.getAmountMax());

        if (itemSettings.isDirectDrop() && !isInventoryFull()) {
            player.getInventory().addItem(itemStack);
        } else {
            dropItem(player, itemSettings, itemStack, dropLocation);
        }
    }

    private void handleNexoDrop(Player player, String id, DropTable.Drops.NexoItem nexoItem, Location dropLocation) {
        DropTable.ItemSettings itemSettings = nexoItem.getItemSettings();

        if (itemSettings == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Unable to drop Nexo item with id " + id + ".",
                    "Please check your dropTables for correct settings!",
                    "Item settings are null!"
            ));
            return;
        }

        ItemStack itemStack = initializeItemStack(nexoItem.getItemStack(), nexoItem.getAmountMin(), nexoItem.getAmountMax());

        if (itemSettings.isDirectDrop()) {
            player.getInventory().addItem(itemStack);
        } else {
            dropItem(player, itemSettings, itemStack, dropLocation);
        }
    }

    private void handleVanillaDrop(Player player, String id, DropTable.Drops.VanillaItem vanillaItem, Location dropLocation) {
        DropTable.ItemSettings itemSettings = vanillaItem.getItemSettings();

        if (itemSettings == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Unable to drop Vanilla item with id " + id + ".",
                    "Please check your dropTables for correct settings!",
                    "Item settings are null!"
            ));
            return;
        }

        ItemStack itemStack = LightStrings.generateItemFromString(vanillaItem.getVanillaItemBuilder());
        if(itemStack == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Could not generate item from string: " + vanillaItem.getVanillaItemBuilder()
            ));
            return;
        }

        initializeItemStack(itemStack, vanillaItem.getAmountMin(), vanillaItem.getAmountMax());

        if (itemSettings.isDirectDrop()) {
            player.getInventory().addItem(itemStack);
        } else {
            dropItem(player, itemSettings, itemStack, dropLocation);
        }
    }

    private ItemStack initializeItemStack(ItemStack is, int amountMin, int amountMax) {
        Random random = new Random();
        int amount = random.nextInt(amountMax - amountMin) + amountMin;
        is.setAmount(amount);
        return is;
    }

    private void dropItem(Player player, DropTable.ItemSettings itemSettings, ItemStack is, Location dropLocation) {
        if (dropLocation == null) {
            dropLocation = player.getLocation();
        }

        Item item = dropLocation.getWorld().dropItem(dropLocation, is);
        droppedItemList.put(item.getUniqueId(), player);
        item.setCanMobPickup(false);

        if (itemSettings.isEnableGlow()) {
            setItemGlowColor(item, itemSettings.getGlowColor().asHexString());
            item.setVelocity(item.getVelocity().multiply(0.05));
        }

        if (itemSettings.isPickUpOnlyOwner()) {
            item.setOwner(player.getUniqueId());
        }
    }


    private void sendTitle(Player player, Title title) { player.showTitle(title); }

    private void sendMessage(Player player, Component message) {
        player.sendMessage(LightCore.instance.getColorTranslation().translateComponent(message, player));
    }

    private void playSound(Player player, DropTable.Actions.Sound sound) {
        player.playSound(player.getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch());
    }

    private void setItemGlowColor(Item item, String hexColor) {
        TextColor color = TextColor.fromHexString(hexColor);
        if (color == null) {
            throw new IllegalArgumentException("Unsupported Hex color format: " + hexColor);
        }

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        String teamName = "glow-" + hexColor.replace("#", "") + "-" + UUID.randomUUID();
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.color(NamedTextColor.nearestTo(color));
        }

        team.addEntry(item.getUniqueId().toString());
        item.setGlowing(true);
    }

    private boolean isInventoryFull() {
        return player.getInventory().firstEmpty() == -1;
    }
}
