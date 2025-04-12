package io.lightstudios.core.droptable;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.droptable.model.DropTable;
import io.lightstudios.core.util.LightMath;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DropTableBuilder {

    private final List<File> dropTableFiles;
    private final Map<String, DropTable> dropTables = new HashMap<>();

    public DropTableBuilder(List<File> dropTableFiles) {
        if (dropTableFiles == null || dropTableFiles.isEmpty()) {
            throw new IllegalArgumentException("Drop table files cannot be null or empty in DropTableBuilder");
        }
        this.dropTableFiles = dropTableFiles;
        loadDropTables();
    }

    private void loadDropTables() {

        for(File dropTableFile : dropTableFiles) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(dropTableFile);

            DropTable dropTable = new DropTable();
            String dropTableID = dropTableFile.getName().replace(".yml", "");
            dropTable.setDropTableID(dropTableID);

            Map<String, DropTable.Drops> dropsList = new HashMap<>();

            ConfigurationSection dropSection = config.getConfigurationSection("table");

            if(dropSection == null) {
                LightCore.instance.getConsolePrinter().printError(List.of(
                        "Unable to load DropTable file " + dropTableFile.getName(),
                        "DropTable file " + dropTableFile.getName() + " is missing the 'drops' section."
                ));
                return;
            }

            for(String key : dropSection.getKeys(false)) {

                DropTable.Drops drops = new DropTable.Drops();

                // LightMath zur Berechnung verwenden (Expression)
                LightMath lightMath = new LightMath();
                try {
                    String mathString = dropSection.getString(key + ".chance", "100 / 1");
                    drops.setChance(mathString);
                    double mathChance = lightMath.evaluateExpression(mathString);
                    drops.setChanceAsDouble(mathChance > 100 ? 100 : mathChance < 0 ? 0 : mathChance);
                } catch (Exception e) {
                    LightCore.instance.getConsolePrinter().printError(List.of(
                            "Unable to load DropTable file " + dropTableFile.getName(),
                            "DropTable file " + dropTableFile.getName() + " has an invalid chance expression for drop " + key,
                            "Error: " + e.getMessage()
                    ));
                    return;
                }
                drops.setDropsID(key);

                String id = config.getString("table." + key + ".id");

                if(id == null) {
                    LightCore.instance.getConsolePrinter().printError(List.of(
                            "Unable to load DropTable file " + dropTableFile.getName(),
                            "DropTable file " + dropTableFile.getName().replace(".yml", "") + " is missing the 'id' field for drop " + key + "."
                    ));
                    return;
                }

                if(id.equalsIgnoreCase("vanilla-item")) {

                    DropTable.Drops.VanillaItem vanillaItem = new DropTable.Drops.VanillaItem();
                    vanillaItem.setVanillaItemBuilder(config.getString("table." + key + ".args.item", "stone"));
                    vanillaItem.setAmountMin(config.getInt("table." + key + ".args.amount.min", 1));
                    vanillaItem.setAmountMax(config.getInt("table." + key + ".args.amount.max", 1));

                    DropTable.ItemSettings vanillaItemSettings = new DropTable.ItemSettings();
                    vanillaItemSettings.setDirectDrop(
                            config.getBoolean("table." + key + ".args.settings.direct-drop", false));
                    vanillaItemSettings.setPickUpOnlyOwner(
                            config.getBoolean("table." + key + ".args.settings.pick-up-only-owner", false));
                    vanillaItemSettings.setEnableGlow(
                            config.getBoolean("table." + key + ".args.settings.glow.enable", false));
                    vanillaItemSettings.setGlowColor(
                            TextColor.fromHexString(config.getString("table." + key + ".args.glow.color", "#FFFFFF")));

                    vanillaItem.setItemSettings(vanillaItemSettings);
                    drops.setVanillaItem(vanillaItem);

                }

                if(id.equalsIgnoreCase("nexo-item")) {

                    DropTable.Drops.NexoItem nexoItem = new DropTable.Drops.NexoItem();
                    nexoItem.setNexoID(config.getString("table." + key + ".args.item", "unknown"));
                    nexoItem.setAmountMin(config.getInt("table." + key + ".args.amount.min", 1));
                    nexoItem.setAmountMax(config.getInt("table." + key + ".args.amount.max", 1));

                    DropTable.ItemSettings nexoItemSettings = new DropTable.ItemSettings();
                    nexoItemSettings.setDirectDrop(
                            config.getBoolean("table." + key + ".args.settings.direct-drop", false));
                    nexoItemSettings.setPickUpOnlyOwner(config.getBoolean("table." + key + ".args.settings.pick-up-only-owner", false));
                    nexoItemSettings.setEnableGlow(
                            config.getBoolean("table." + key + ".args.settings.glow.enable", false));
                    nexoItemSettings.setGlowColor(
                            TextColor.fromHexString(config.getString("table." + key + ".args.glow.color", "#FFFFFF")));

                    nexoItem.setItemSettings(nexoItemSettings);

                    ItemStack nexoItemStack = LightCore.instance.getHookManager().getNexoManager().getNexoItemByID(nexoItem.getNexoID());

                    if(nexoItemStack == null) {
                        LightCore.instance.getConsolePrinter().printError(List.of(
                                "Unable to load DropTable file " + dropTableFile.getName(),
                                "Could not find Nexo item with id: " + nexoItem.getNexoID()
                        ));
                        return;
                    } else {
                        nexoItem.setItemStack(nexoItemStack);
                        drops.setNexoItem(nexoItem);
                    }

                }

                if(id.equalsIgnoreCase("mmoitems-item")) {

                    DropTable.Drops.MMOItemsItem mmoItem = new DropTable.Drops.MMOItemsItem();
                    mmoItem.setMmoItem(config.getString("table." + key + ".args.item", "unknown"));
                    mmoItem.setAmountMin(config.getInt("table." + key + ".args.amount.min", 1));
                    mmoItem.setAmountMax(config.getInt("table." + key + ".args.amount.max", 1));

                    DropTable.ItemSettings mmoItemSettings = new DropTable.ItemSettings();
                    mmoItemSettings.setDirectDrop(
                            config.getBoolean("table." + key + ".args.settings.direct-drop", false));
                    mmoItemSettings.setPickUpOnlyOwner(config.getBoolean("table." + key + ".args.settings.pick-up-only-owner", false));
                    mmoItemSettings.setEnableGlow(
                            config.getBoolean("table." + key + ".args.settings.glow.enable", false));
                    mmoItemSettings.setGlowColor(
                            TextColor.fromHexString(config.getString("table." + key + ".args.glow.color", "#FFFFFF")));

                    mmoItem.setItemSettings(mmoItemSettings);

                    ItemStack mmoItemStack = LightCore.instance.getHookManager().getNexoManager().getNexoItemByID(mmoItem.getMmoItem());

                    if(mmoItemStack == null) {
                        LightCore.instance.getConsolePrinter().printError(List.of(
                                "Unable to load DropTable file " + dropTableFile.getName(),
                                "Could not find MMOItems item with id: " + mmoItem.getMmoItem()
                        ));
                        return;
                    } else {
                        mmoItem.setItemStack(mmoItemStack);
                        drops.setMmoItemsItem(mmoItem);
                    }
                }

                ConfigurationSection actionSection = config.getConfigurationSection("table." + key + ".actions");

                if(actionSection == null) {
                    return;
                }

                DropTable.Actions actions = new DropTable.Actions();

                for(String actionKey : actionSection.getKeys(false)) {

                    String actionID = actionSection.getString(actionKey + ".id");

                    if(actionID == null) {
                        LightCore.instance.getConsolePrinter().printError(List.of(
                                "Unable to load DropTable file " + dropTableFile.getName(),
                                "DropTable file " + dropTableFile.getName() + " is missing the 'id' field for action " + actionKey + "."
                        ));
                        return;
                    }

                    if(actionID.equalsIgnoreCase("message")) {
                        DropTable.Actions.Message message = new DropTable.Actions.Message();
                        message.setMessage(Component.text(
                                actionSection.getString(actionKey + ".args.message", "This is the Default message.")
                        ));

                        actions.setMessage(message);
                    }

                    if(actionID.equalsIgnoreCase("title")) {
                        DropTable.Actions.Title title = new DropTable.Actions.Title();
                        title.setUpperTitle(Component.text(
                                actionSection.getString(actionKey + ".args.title", "Default Upper title.")
                        ));
                        title.setLowerTitle(Component.text(
                                actionSection.getString(actionKey + ".args.subtitle", "Default Sub title")
                        ));
                        title.setFadeIn(actionSection.getInt(actionKey + ".args.fade-in", 20));
                        title.setStay(actionSection.getInt(actionKey + ".args.stay", 70));
                        title.setFadeOut(actionSection.getInt(actionKey + ".args.fade-out", 20));


                        Title confTitle = Title.title(
                                title.getLowerTitle(),
                                title.getUpperTitle(),
                                Title.Times.times(
                                        Duration.ofMillis(title.getFadeIn()),
                                        Duration.ofMillis(title.getStay()),
                                        Duration.ofMillis(title.getFadeOut()))
                        );

                        title.setTitle(confTitle);
                        actions.setTitle(title);

                    }

                    if(actionID.equalsIgnoreCase("sound")) {
                        DropTable.Actions.Sound sound = new DropTable.Actions.Sound();
                        NamespacedKey namespacedKey = NamespacedKey.fromString(
                                actionSection.getString(actionKey + ".args.sound", "BLOCK_NOTE_BLOCK_BASS").replace("_", "."));

                        if(namespacedKey == null) {
                            LightCore.instance.getConsolePrinter().printError(List.of(
                                    "Unable to load DropTable file " + dropTableFile.getName(),
                                    "DropTable file " + dropTableFile.getName() + " is missing the 'sound' field for action " + actionKey + "."
                            ));
                            return;
                        }

                        Sound bukkitSound = Registry.SOUNDS.get(namespacedKey);

                        if(bukkitSound == null) {
                            LightCore.instance.getConsolePrinter().printError(List.of(
                                    "Unable to load DropTable file " + dropTableFile.getName(),
                                    "Sound " + namespacedKey.getNamespace()+ ":" + namespacedKey.getKey() + " is not a valid sound."
                            ));
                            return;
                        }
                        sound.setSound(bukkitSound);
                        sound.setVolume((float) actionSection.getDouble(actionKey + ".args.volume", 1.0f));
                        sound.setPitch((float) actionSection.getDouble(actionKey + ".args.pitch", 1.0f));

                        actions.setSound(sound);

                    }
                }
                drops.setActions(actions);
                dropsList.put(key, drops);
                if (!drops.isValid()) {
                    throw new IllegalStateException("Invalid Drops object: More than 1 item type is set, or none is set.");
                }


            }
            dropTable.setDropsList(dropsList);
            dropTables.put(dropTableID, dropTable);
        }
    }

    public Map<String, DropTable> getDropTables() {
        return Collections.unmodifiableMap(dropTables);
    }

    public DropTable getDropTableByName(String name) { return dropTables.get(name); }

}
