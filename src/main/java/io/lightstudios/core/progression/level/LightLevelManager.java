package io.lightstudios.core.progression.level;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.droptable.DropTableBuilder;
import io.lightstudios.core.droptable.DropTableManager;
import io.lightstudios.core.droptable.model.DropTable;
import io.lightstudios.core.progression.level.events.OnPlayerJoin;
import io.lightstudios.core.progression.level.models.LightLevel;
import io.lightstudios.core.progression.level.models.LightLevelData;
import io.lightstudios.core.progression.level.response.LevelResponse;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public class LightLevelManager {

    private final DropTableBuilder dropTableBuilder;
    private final List<File> levelFiles;
    private final List<LightLevel> lightLevels = new ArrayList<>();
    private final Plugin plugin;

    @Getter
    private final Map<UUID, Map<LightLevel, LightLevelData>> playerLevels = new HashMap<>();
    private final Map<UUID, List<Integer>> activeTasks = new HashMap<>();

    public LightLevelManager(List<File> levelFiles, DropTableBuilder dropTableBuilder, Plugin plugin) {

        if (levelFiles == null || levelFiles.isEmpty()) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "LightLevelManager could not be initialized.",
                    "The levelFiles list is null or empty."
            ));
        }

        if(dropTableBuilder == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "LightLevelManager could not be initialized.",
                    "The provided DropTableBuilder is null."
            ));
        }

        if(plugin == null) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "LightLevelManager could not be initialized.",
                    "The provided plugin instance is null."
            ));
        }

        this.levelFiles = levelFiles;
        this.dropTableBuilder = dropTableBuilder;
        this.plugin = plugin;
        initFiles();
        registerEvents();
    }

    private void initFiles() {
        for (File file : levelFiles) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            LightLevel lightLevel = new LightLevel();

            String id = file.getName();
            if (!id.isEmpty()) {
                lightLevel.setId(id.replace(".yml", ""));
            } else {
                LightCore.instance.getConsolePrinter().printError(List.of(
                        "LightLevelManager could not be initialized.",
                        "The ID is null or empty for file: " + file.getName()
                ));
                return;
            }

            lightLevel.setName(Component.text(config.getString("name", "Default Level Name")));

            ConfigurationSection singleLevelSection = config.getConfigurationSection("levels");

            if (singleLevelSection == null) {
                LightCore.instance.getConsolePrinter().printError(List.of(
                        "LightLevelManager could not be initialized.",
                        "The level section is null in the file: " + file.getName()
                ));
                return;
            }

            Map<Integer, LightLevel.SingleLevel> singleLevelList = new HashMap<>();
            BigDecimal previousRequiredXP = BigDecimal.ZERO;

            for (String path : singleLevelSection.getKeys(false)) {
                try {
                    int level = Integer.parseInt(path);
                    BigDecimal requiredXP = BigDecimal.valueOf(singleLevelSection.getDouble(path + ".required-xp", 10));
                    String visualName = singleLevelSection.getString(path + ".visual-name", "Default Visual Name");
                    String dropTableName = singleLevelSection.getString(path + ".drop-table", "default");

                    if (level == 0 || level == 1) {
                        LightCore.instance.getConsolePrinter().printError(List.of(
                                "LightLevelManager could not be initialized.",
                                "The Level key is zero (0) or 1, that is not allowed in file " + file.getName(),
                                "Make sure you start your levels with 2!"
                        ));
                        return;
                    }

                    if (requiredXP.compareTo(previousRequiredXP) <= 0) {
                        LightCore.instance.getConsolePrinter().printError(List.of(
                                "LightLevelManager could not be initialized.",
                                "The required XP for level " + level + " is not greater than the previous level in file " + file.getName(),
                                "Ensure that each level has a higher required XP than the previous one!"
                        ));
                        return;
                    }

                    DropTable dropTable = dropTableBuilder.getDropTableByName(dropTableName);

                    LightLevel.SingleLevel lightSingleLevel = new LightLevel.SingleLevel();
                    lightSingleLevel.setLevel(level);
                    lightSingleLevel.setVisualName(Component.text(visualName));
                    lightSingleLevel.setRequiredXP(requiredXP);

                    if (dropTable != null) {
                        lightSingleLevel.setDropTable(dropTable);
                    }

                    singleLevelList.put(level, lightSingleLevel);
                    previousRequiredXP = requiredXP;

                } catch (NumberFormatException e) {
                    LightCore.instance.getConsolePrinter().printError(List.of(
                            "LightLevelManager could not be initialized.",
                            "The Level key is not a number in file " + file.getName(),
                            "Make sure you set a valid level key! (2, 3, 4, ...)"
                    ));
                    return;
                }
            }

            lightLevel.setLevels(singleLevelList);
            lightLevels.add(lightLevel);
        }
    }

    public LightLevel getLightLevelByID(String id) {
        for (LightLevel lightLevel : lightLevels) {
            if (lightLevel.getId().equalsIgnoreCase(id)) {
                return lightLevel;
            }
        }
        return null; // Falls keine LightLevel mit der ID gefunden wurde
    }

    public List<String> getLightLevelTypes() {
        List<String> lightLevelTypes = new ArrayList<>();
        for (LightLevel lightLevel : lightLevels) {
            lightLevelTypes.add(lightLevel.getId());
        }
        return lightLevelTypes;
    }

    public void addPlayerWithAllLightLevels(UUID uuid) {
        // Spieler zur Map hinzufügen, falls noch nicht vorhanden
        playerLevels.putIfAbsent(uuid, new HashMap<>());

        // Spieler zu allen vorhandenen LightLevels hinzufügen
        for (LightLevel lightLevel : lightLevels) {
            Map<LightLevel, LightLevelData> playerData = playerLevels.get(uuid);
            if (playerData != null && !playerData.containsKey(lightLevel)) {
                playerData.put(lightLevel, new LightLevelData(uuid, lightLevel, BigDecimal.ZERO, 1));
            }
        }
    }

    public LevelResponse addXP(UUID uuid, LightLevel lightLevel, BigDecimal xp) {
        LightLevelData data = getPlayerLevelData(uuid, lightLevel);
        if (data == null) {
            return new LevelResponse(LevelResponse.Status.FAILURE, "LightLevel nicht gefunden.");
        }

        // Abbrechen aller laufenden Tasks für diesen Spieler
        if (activeTasks.containsKey(uuid)) {
            activeTasks.get(uuid).forEach(Bukkit.getScheduler()::cancelTask);
            activeTasks.remove(uuid);
        }

        BigDecimal newXP = data.getCurrentXP().add(xp);
        data.setCurrentXP(newXP);

        List<LightLevel.SingleLevel> levelsToReward = new ArrayList<>();
        LightLevel.SingleLevel currentLevel = lightLevel.getLevels().get(data.getCurrentLevel());

        while (currentLevel != null && newXP.compareTo(currentLevel.getRequiredXP()) >= 0) {
            // Subtrahiere die benötigte XP und erhöhe das Level
            newXP = newXP.subtract(currentLevel.getRequiredXP());
            data.setCurrentXP(newXP);
            data.setCurrentLevel(data.getCurrentLevel() + 1);

            // Füge das neue Level zur Belohnungsliste hinzu
            LightLevel.SingleLevel newLevel = lightLevel.getLevels().get(data.getCurrentLevel());
            if (newLevel != null) {
                levelsToReward.add(newLevel);
            }

            currentLevel = lightLevel.getLevels().get(data.getCurrentLevel());
        }

        // Verarbeite die Belohnungen
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && !levelsToReward.isEmpty()) {
            if (levelsToReward.size() == 1) {
                // Keine Verzögerung, wenn nur ein Level aufgestiegen wird
                LightLevel.SingleLevel level = levelsToReward.getFirst();
                if (level.getDropTable() != null) {
                    new DropTableManager(player, level.getDropTable());
                }
            } else {
                // Verzögerung von 1 Sekunde zwischen den Belohnungen
                List<Integer> taskIds = new ArrayList<>();
                Bukkit.getScheduler().runTaskAsynchronously(LightCore.instance, () -> {
                    for (int i = 0; i < levelsToReward.size(); i++) {
                        LightLevel.SingleLevel level = levelsToReward.get(i);
                        int taskId = Bukkit.getScheduler().runTaskLater(LightCore.instance, () -> {
                            if (level.getDropTable() != null) {
                                new DropTableManager(player, level.getDropTable());
                            }
                        }, i * 20L).getTaskId();
                        taskIds.add(taskId);
                    }
                });
                activeTasks.put(uuid, taskIds);
            }
        }

        return new LevelResponse(LevelResponse.Status.SUCCESS, "XP hinzugefügt.");
    }

    public LightLevelData getPlayerLevelData(UUID uuid, LightLevel lightLevel) {
        return playerLevels.getOrDefault(uuid, new HashMap<>()).get(lightLevel);
    }

    public LevelResponse setLevel(UUID uuid, LightLevel lightLevel, int level) {
        LightLevelData data = getPlayerLevelData(uuid, lightLevel);
        if (data == null) {
            return new LevelResponse(LevelResponse.Status.FAILURE, "LightLevel nicht gefunden.");
        }

        if (!lightLevel.getLevels().containsKey(level)) {
            return new LevelResponse(LevelResponse.Status.FAILURE, "Ungültiges Level.");
        }

        // Abbrechen aller laufenden Tasks für diesen Spieler
        if (activeTasks.containsKey(uuid)) {
            activeTasks.get(uuid).forEach(Bukkit.getScheduler()::cancelTask);
            activeTasks.remove(uuid);
        }

        int currentLevel = data.getCurrentLevel();
        if (level <= currentLevel) {
            // Wenn das neue Level kleiner oder gleich dem aktuellen Level ist, nur setzen
            data.setCurrentLevel(level);
            data.setCurrentXP(BigDecimal.ZERO);
            return new LevelResponse(LevelResponse.Status.SUCCESS, "Level gesetzt.");
        }

        // Sammle alle Level zwischen dem aktuellen und dem neuen Level
        List<LightLevel.SingleLevel> levelsToReward = new ArrayList<>();
        for (int i = currentLevel + 1; i <= level; i++) {
            LightLevel.SingleLevel singleLevel = lightLevel.getLevels().get(i);
            if (singleLevel != null) {
                levelsToReward.add(singleLevel);
            }
        }

        // Setze das neue Level
        data.setCurrentLevel(level);
        data.setCurrentXP(BigDecimal.ZERO);

        // Verarbeite die Belohnungen
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && !levelsToReward.isEmpty()) {
            if (levelsToReward.size() == 1) {
                // Keine Verzögerung, wenn nur ein Level gesetzt wird
                LightLevel.SingleLevel singleLevel = levelsToReward.getFirst();
                if (singleLevel.getDropTable() != null) {
                    new DropTableManager(player, singleLevel.getDropTable());
                }
            } else {
                // Verzögerung von 1 Sekunde zwischen den Belohnungen
                List<Integer> taskIds = new ArrayList<>();
                Bukkit.getScheduler().runTaskAsynchronously(LightCore.instance, () -> {
                    for (int i = 0; i < levelsToReward.size(); i++) {
                        LightLevel.SingleLevel singleLevel = levelsToReward.get(i);
                        int taskId = Bukkit.getScheduler().runTaskLater(LightCore.instance, () -> {
                            if (singleLevel.getDropTable() != null) {
                                new DropTableManager(player, singleLevel.getDropTable());
                            }
                        }, i * 20L).getTaskId();
                        taskIds.add(taskId);
                    }
                });
                activeTasks.put(uuid, taskIds);
            }
        }

        return new LevelResponse(LevelResponse.Status.SUCCESS, "Level gesetzt.");
    }

    public LevelResponse removeXP(UUID uuid, LightLevel lightLevel, BigDecimal xp) {
        LightLevelData data = getPlayerLevelData(uuid, lightLevel);
        if (data == null) {
            return new LevelResponse(LevelResponse.Status.FAILURE, "LightLevel nicht gefunden.");
        }

        BigDecimal newXP = data.getCurrentXP().subtract(xp);
        if (newXP.compareTo(BigDecimal.ZERO) < 0) {
            data.setCurrentXP(BigDecimal.ZERO);
            return new LevelResponse(LevelResponse.Status.SUCCESS, "XP entfernt. Spieler hat jetzt 0 XP.");
        }

        data.setCurrentXP(newXP);
        return new LevelResponse(LevelResponse.Status.SUCCESS, "XP entfernt.");
    }

    public LevelResponse removeLevel(UUID uuid, LightLevel lightLevel, int levelsToRemove) {
        LightLevelData data = getPlayerLevelData(uuid, lightLevel);
        if (data == null) {
            return new LevelResponse(LevelResponse.Status.FAILURE, "LightLevel nicht gefunden.");
        }

        int newLevel = data.getCurrentLevel() - levelsToRemove;
        if (newLevel < 1) {
            data.setCurrentLevel(1);
            data.setCurrentXP(BigDecimal.ZERO);
            return new LevelResponse(LevelResponse.Status.SUCCESS, "Level entfernt. Spieler ist jetzt auf Level 1.");
        }

        data.setCurrentLevel(newLevel);
        data.setCurrentXP(BigDecimal.ZERO);
        return new LevelResponse(LevelResponse.Status.SUCCESS, "Level entfernt.");
    }

    private void registerEvents() {
        this.plugin.getServer().getPluginManager().registerEvents(
                new OnPlayerJoin(this), this.plugin);
    }

}