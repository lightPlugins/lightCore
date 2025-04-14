package io.lightstudios.core.droptable;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.droptable.model.DropTable;
import io.lightstudios.core.util.LightStrings;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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

            if (randomValue <= dropChance) {
                // Chance met
                executeDrop(drop, id, player);
                executeActions(drop, player);
            }

        });
    }

    private void executeDrop(DropTable.Drops drop, String id, Player player) {
        if (drop.getMmoItemsItem() != null) {
            handleMMOItemsDrop(player, id, drop);
        }

        if (drop.getNexoItem() != null) {
            handleNexoDrop(player, id, drop);
        }

        if (drop.getVanillaItem() != null) {
            handleVanillaDrop(player, id, drop);
        }
    }

    private void executeActions(DropTable.Drops drop, Player player) {

        DropTable.Actions.Message message = drop.getActions().getMessage();
        DropTable.Actions.Title title = drop.getActions().getTitle();
        DropTable.Actions.Sound sound = drop.getActions().getSound();

        if(message != null) {
            sendMessage(drop, player, message.getMessage());
        }

        if(title != null) {
            sendTitle(drop, player, title.getTitle());
        }

        if(sound != null) {
            playSound(player, sound);
        }
    }

    private void handleMMOItemsDrop(Player player, String id, DropTable.Drops mmoItemsItem) {
        DropTable.ItemSettings itemSettings = mmoItemsItem.getMmoItemsItem().getItemSettings();

        if (itemSettings == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Unable to drop MMOItems item with id " + id + ".",
                    "Please check your dropTables for correct settings!",
                    "Item settings are null!"
            ));
            return;
        }

        ItemStack itemStack = initializeItemStack(
                mmoItemsItem,
                mmoItemsItem.getMmoItemsItem().getItemStack(),
                mmoItemsItem.getMmoItemsItem().getAmountMin(),
                mmoItemsItem.getMmoItemsItem().getAmountMax());

        if (itemSettings.isDirectDrop() && !isInventoryFull()) {
            player.getInventory().addItem(itemStack);
        } else {
            dropItem(player, itemSettings, itemStack, mmoItemsItem.getDropLocation());
        }
    }

    private void handleNexoDrop(Player player, String id, DropTable.Drops nexoItem) {
        DropTable.ItemSettings itemSettings = nexoItem.getMmoItemsItem().getItemSettings();

        if (itemSettings == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Unable to drop Nexo item with id " + id + ".",
                    "Please check your dropTables for correct settings!",
                    "Item settings are null!"
            ));
            return;
        }

        ItemStack itemStack = initializeItemStack(
                nexoItem,
                nexoItem.getMmoItemsItem().getItemStack(),
                nexoItem.getMmoItemsItem().getAmountMin(),
                nexoItem.getMmoItemsItem().getAmountMax());

        if (itemSettings.isDirectDrop()) {
            player.getInventory().addItem(itemStack);
        } else {
            dropItem(player, itemSettings, itemStack, nexoItem.getDropLocation());
        }
    }

    private void handleVanillaDrop(Player player, String id, DropTable.Drops vanillaItem) {
        DropTable.ItemSettings itemSettings = vanillaItem.getVanillaItem().getItemSettings();

        if (itemSettings == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Unable to drop Vanilla item with id " + id + ".",
                    "Please check your dropTables for correct settings!",
                    "Item settings are null!"
            ));
            return;
        }

        ItemStack itemStack = LightStrings.generateItemFromString(vanillaItem.getVanillaItem().getVanillaItemBuilder());
        if(itemStack == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Could not generate item from string: " + vanillaItem.getVanillaItem().getVanillaItemBuilder()
            ));
            return;
        }

        initializeItemStack(
                vanillaItem,
                itemStack,
                vanillaItem.getVanillaItem().getAmountMin(),
                vanillaItem.getVanillaItem().getAmountMax());

        if (itemSettings.isDirectDrop()) {
            player.getInventory().addItem(itemStack);
        } else {
            dropItem(player, itemSettings, itemStack, vanillaItem.getDropLocation());
        }
    }

    private ItemStack initializeItemStack(DropTable.Drops drop, ItemStack is, int amountMin, int amountMax) {
        Random random = new Random();
        int amount = random.nextInt(amountMax - amountMin + 1) + amountMin;
        drop.setFinalAmount(amount);
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


    private void sendTitle(DropTable.Drops drop, Player player, Title title) {
        Map<String, String> replacements = Map.of(
                "#display-name#", PlainTextComponentSerializer.plainText().serialize(drop.getRewardName()),
                "#amount#", String.valueOf(drop.getFinalAmount())
        );

        // Translate the title components with replacements
        Component titleComponent = LightCore.instance.getColorTranslation()
                .translateComponentWithReplacements(title.title(), player, replacements);
        Component subtitleComponent = LightCore.instance.getColorTranslation()
                .translateComponentWithReplacements(title.subtitle(), player, replacements);

        // Create the final title with the translated components
        Title finalTitle = Title.title(titleComponent, subtitleComponent, title.times());

        // Show the title to the player
        player.showTitle(finalTitle);
    }

    private void sendMessage(DropTable.Drops drop, Player player, Component message) {
        Map<String, String> replacements = Map.of(
                "#display-name#", PlainTextComponentSerializer.plainText().serialize(drop.getRewardName()),
                "#amount#", String.valueOf(drop.getFinalAmount())
        );
        player.sendMessage(LightCore.instance.getColorTranslation().translateComponentWithReplacements(
                message,
                player,
                replacements
        ));
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
