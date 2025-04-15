package io.lightstudios.core.progression.level;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.progression.level.models.LightLevel;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LightLevelManager {

    private final List<File> levelFiles;
    private final List<LightLevel> lightLevels = new ArrayList<>();

    public LightLevelManager(List<File> levelFiles) {

        if(levelFiles == null ||levelFiles.isEmpty()) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "LightLevelManager could not be initialized.",
                    "The levelFiles list is null or empty."

            ));
        }

        this.levelFiles = levelFiles;

    }

    private void initFiles() {

        for(File file : levelFiles) {

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            LightLevel lightLevel = new LightLevel();

            lightLevel.setName(Component.text(config.getString("name", "Default Level Name")));

            ConfigurationSection singleLevelSection = config.getConfigurationSection("levels");

            if(singleLevelSection == null) {
                LightCore.instance.getConsolePrinter().printError(List.of(
                        "LightLevelManager could not be initialized.",
                        "The level section is null in the file: " + file.getName()
                ));
                return;
            }

            Map<Integer, LightLevel.SingleLevel> singleLevelList = new HashMap<>();

            for(String path : singleLevelSection.getKeys(false)) {

                try {
                    int singleLevel = Integer.parseInt(path);
                    BigDecimal requiredXP = BigDecimal.valueOf(singleLevelSection.getDouble(path + ".required-xp", 10));

                    if(singleLevel == 0) {
                        LightCore.instance.getConsolePrinter().printError(List.of(
                                "LightLevelManager could not be initialized.",
                                "The Level key is zero (0), that is not allowed in file " + file.getName(),
                                "Make sure you start your levels with 1!"
                        ));
                        return;
                    }

                    if(requiredXP.compareTo(BigDecimal.ZERO) <= 0) {
                        LightCore.instance.getConsolePrinter().printError(List.of(
                                "LightLevelManager could not be initialized.",
                                "The required XP is less than or equal to zero (0) in file " + file.getName(),
                                "Make sure you set a valid required XP value! (> 0)"
                        ));
                        return;
                    }

                    LightLevel.SingleLevel lightsingleLevel = new LightLevel.SingleLevel();

                    lightsingleLevel.setLevel(singleLevel);
                    lightsingleLevel.setRequiredXP(requiredXP);
                    singleLevelList.put(singleLevel, lightsingleLevel);

                } catch (NumberFormatException e) {
                    LightCore.instance.getConsolePrinter().printError(List.of(
                            "LightLevelManager could not be initialized.",
                            "The Level key is not a number in file " + file.getName(),
                            "Make sure you set a valid level key! (1, 2, 3, ...)"
                    ));
                    return;
                }
            }

            lightLevel.setLevels(singleLevelList);
            lightLevels.add(lightLevel);
        }
    }

}
