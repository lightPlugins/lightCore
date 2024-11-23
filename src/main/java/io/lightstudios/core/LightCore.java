package io.lightstudios.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.zaxxer.hikari.HikariDataSource;
import de.tr7zw.changeme.nbtapi.NBT;
import io.lightstudios.core.database.SQLDatabase;
import io.lightstudios.core.database.impl.MySQLDatabase;
import io.lightstudios.core.database.impl.SQLiteDatabase;
import io.lightstudios.core.database.model.ConnectionProperties;
import io.lightstudios.core.database.model.DatabaseCredentials;
import io.lightstudios.core.player.MessageSender;
import io.lightstudios.core.player.TitleSender;
import io.lightstudios.core.register.ModuleRegister;
import io.lightstudios.core.util.ColorTranslation;
import io.lightstudios.core.util.ConsolePrinter;
import io.lightstudios.core.util.LightTimers;
import io.lightstudios.core.util.files.FileManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public class LightCore extends JavaPlugin {

    public static LightCore instance;
    public boolean lightCoreEnabled;
    private static LightTimers lightTimers;
    private ModuleRegister moduleRegister;
    private ConsolePrinter consolePrinter;
    private ColorTranslation colorTranslation;
    private MessageSender messageSender;
    private TitleSender titleSender;
    private FileManager coreFile;
    private ProtocolManager protocolManager;

    private SQLDatabase sqlDatabase;
    public HikariDataSource hikariDataSource;

    @Override
    public void onLoad() {
        // generate the core.yml file
        instance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        printLogo();
        lightTimers = new LightTimers(this); // Initialize LightTimers instance
        this.consolePrinter = new ConsolePrinter("§7[§rLight§eCore§7] §r");
        this.moduleRegister = new ModuleRegister();
        this.colorTranslation = new ColorTranslation();
        this.messageSender = new MessageSender();
        this.titleSender = new TitleSender();
        this.consolePrinter.printInfo("Generate core files ...");
        generateCoreFiles();
        this.consolePrinter.printInfo("Initializing LightCore ...");
        // Initialize and connect to the database
        this.consolePrinter.printInfo("Starting database connection ...");
        initDatabase();

    }

    @Override
    public void onEnable() {
        // on success loading the core module
        this.lightCoreEnabled = true;
        this.consolePrinter.printInfo("Successfully initialized LightCore. Ready for third party plugins.");
        checkNBTAPI();
        testTimer();
    }

    @Override
    public void onDisable() {
        this.consolePrinter.printInfo("Stopping LightCore instance ...");
        this.consolePrinter.printInfo("Stopping database connection ...");
        this.sqlDatabase.close();
        this.consolePrinter.printInfo("Successfully stopped LightCore instance.");
    }

    private void generateCoreFiles() {
        // Generate core files
         this.coreFile = new FileManager(this, "core.yml", true);
    }

    private void initDatabase() {
        try {
            String databaseType = coreFile.getConfig().getString("storage.type");
            ConnectionProperties connectionProperties = ConnectionProperties.fromConfig(coreFile.getConfig());
            DatabaseCredentials credentials = DatabaseCredentials.fromConfig(coreFile.getConfig());

            if(databaseType == null) {
                this.consolePrinter.printError(List.of(
                        "Database type not specified in config. Disabling plugin.",
                        "Please specify the database type in the config file.",
                        "Valid database types are: SQLite, MySQL.",
                        "Disabling all core related plugins."));
                this.lightCoreEnabled = false;
                return;
            }

            switch (databaseType.toLowerCase()) {
                case "sqlite":
                    this.sqlDatabase = new SQLiteDatabase(this, connectionProperties);
                    this.consolePrinter.printInfo("Using SQLite (local) database.");
                    break;
                case "mysql":
                    this.sqlDatabase = new MySQLDatabase(this, credentials, connectionProperties);
                    this.consolePrinter.printInfo("Using MySQL (remote) database.");
                    break;
                default:
                    this.consolePrinter.printError(List.of(
                            "Database type not specified in config. Disabling plugin.",
                            "Please specify the database type in the config file.",
                            "Valid database types are: SQLite, MySQL.",
                            "Disabling all core related plugins."));
                    this.lightCoreEnabled = false;
                    return;
            }

            this.sqlDatabase.connect();

        } catch (Exception e) {
            getConsolePrinter().printError(List.of(
                    "Could not maintain Database Connection. Disabling third party plugins.",
                    "Please check your database connection & settings in the config file.",
                    "Disabling all core related plugins."));
            this.lightCoreEnabled = false;
            throw new RuntimeException("Could not maintain Database Connection.", e);
        }
    }

    private void checkNBTAPI() {

        if(!NBT.preloadApi()) {
            this.consolePrinter.printError(List.of(
                    "There is a problem with NBT-API. Please contact the developer",
                    "Disabling all core related plugins."));
            this.lightCoreEnabled = false;
        } else {
            this.consolePrinter.printInfo("NBT-API successfully loaded.");
        }
    }

    private void testTimer() {
        // Start Task 1
        LightTimers.startTaskWithCounter((task, count) -> {
            getConsolePrinter().printInfo("Task 1 with Consumer is running... Count: " + count);
            if (count == 10) {
                task.cancel();
                getConsolePrinter().printInfo("Task 1 canceled after 10 iterations.");
            }
        }, 0L, 10L);

        // Start Task 2
        LightTimers.startTaskWithCounter((task, count) -> {
            getConsolePrinter().printInfo("Task 2 with Consumer is running... Count: " + count);
            if (count == 15) {
                task.cancel();
                getConsolePrinter().printInfo("Task 2 canceled after 15 iterations.");
            }
        }, 0L, 10L);

        // Start Task 3
        LightTimers.startTaskWithCounter((task, count) -> {
            getConsolePrinter().printInfo("Task 3 with Consumer is running... Count: " + count);
            if (count == 20) {
                task.cancel();
                getConsolePrinter().printInfo("Task 3 canceled after 15 iterations.");
            }
        }, 0L, 10L);

        // Start Task 4
        LightTimers.startTaskWithCounter((task, count) -> {
            getConsolePrinter().printInfo("Task 4 with Consumer is running... Count: " + count);
            if (count == 25) {
                task.cancel();
                getConsolePrinter().printInfo("Task 4 canceled after 15 iterations.");
            }
        }, 0L, 10L);

        LightTimers.doAsync(task -> {
            getConsolePrinter().printInfo("Task ASYNC with Consumer is running...");
        }, 240L);

        LightTimers.doAsync(task -> {
            getConsolePrinter().printInfo("Task ASYNC with Consumer is running...");

        }, 260L);
    }

    private void printLogo() {

        String[] logo = {
                " ___       ___  ________  ___  ___  _________  §e________  ________  ________  _______",
                "|\\  \\     |\\  \\|\\   ____\\|\\  \\|\\  \\|\\___   ___\\§e\\   ____\\|\\   __  \\|\\   __  \\|\\  ___ \\",
                "\\ \\  \\    \\ \\  \\ \\  \\___|\\ \\  \\\\\\  \\|___ \\  \\_§e\\ \\  \\___|\\ \\  \\|\\  \\ \\  \\|\\  \\ \\   __/|",
                " \\ \\  \\    \\ \\  \\ \\  \\  __\\ \\   __  \\   \\ \\  \\ §e\\ \\  \\    \\ \\  \\\\\\  \\ \\   _  _\\ \\  \\_|/__",
                "  \\ \\  \\____\\ \\  \\ \\  \\|\\  \\ \\  \\ \\  \\   \\ \\  \\ §e\\ \\  \\____\\ \\  \\\\\\  \\ \\  \\\\  \\\\ \\  \\_|\\ \\",
                "   \\ \\_______\\ \\__\\ \\_______\\ \\__\\ \\__\\   \\ \\__\\ §e\\ \\_______\\ \\_______\\ \\__\\\\ _\\\\ \\_______\\",
                "    \\|_______|\\|__|\\|_______|\\|__|\\|__|    \\|__|  §e\\|_______|\\|_______|\\|__|\\|__|\\|_______|\n"
        };

        for (String line : logo) {
            Bukkit.getConsoleSender().sendMessage(line);
        }

        String protocolLibVersion = "not found";
        String placeholderAPIVersion = "not found";

        Plugin placeholderAPI = Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        Plugin protocolLib = Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib");

        if(placeholderAPI != null) {
            placeholderAPIVersion = placeholderAPI.getDescription().getVersion();
        }

        if(protocolLib != null) {
            protocolLibVersion = protocolLib.getDescription().getVersion();
        }


        Bukkit.getConsoleSender().sendMessage("          LightCore: §ev0.1.0");
        Bukkit.getConsoleSender().sendMessage("          Server: §e" + Bukkit.getServer().getVersion());
        Bukkit.getConsoleSender().sendMessage("          API-Version: §e" + getDescription().getAPIVersion());
        Bukkit.getConsoleSender().sendMessage("          Dependency Versions: §e");
        Bukkit.getConsoleSender().sendMessage("           - PlaceholderAPI: §e" + placeholderAPIVersion);
        Bukkit.getConsoleSender().sendMessage("           - ProtocolLib: §e" + protocolLibVersion);
        Bukkit.getConsoleSender().sendMessage("          Java: §e" + System.getProperty("java.version"));
        Bukkit.getConsoleSender().sendMessage("          Authors: §e" + getDescription().getAuthors() + "\n");
        Bukkit.getConsoleSender().sendMessage("          If you need help, please visit our §eDiscord §7server.");
        Bukkit.getConsoleSender().sendMessage("          Discord: §ehttps://discord.gg/t9vS3hgWf8");
        Bukkit.getConsoleSender().sendMessage("          Thank you for using §eLightCore §7:)\n");


    }
}