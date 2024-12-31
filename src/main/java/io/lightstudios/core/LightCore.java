package io.lightstudios.core;

import com.zaxxer.hikari.HikariDataSource;
import de.tr7zw.changeme.nbtapi.NBT;
import io.lightstudios.core.commands.CoreReloadCommand;
import io.lightstudios.core.commands.manager.CommandManager;
import io.lightstudios.core.commands.tabcomplete.HologramCommand;
import io.lightstudios.core.database.SQLDatabase;
import io.lightstudios.core.database.impl.MariaDatabase;
import io.lightstudios.core.database.impl.MySQLDatabase;
import io.lightstudios.core.database.impl.SQLiteDatabase;
import io.lightstudios.core.database.model.ConnectionProperties;
import io.lightstudios.core.database.model.DatabaseCredentials;
import io.lightstudios.core.placeholder.PlaceholderRegistrar;
import io.lightstudios.core.player.PlayerPunishment;
import io.lightstudios.core.proxy.messaging.ReceiveProxyRequest;
import io.lightstudios.core.redis.RedisManager;
import io.lightstudios.core.economy.VaultManager;
import io.lightstudios.core.events.ProxyTeleportEvent;
import io.lightstudios.core.hooks.HookManager;
import io.lightstudios.core.items.ItemManager;
import io.lightstudios.core.player.MessageSender;
import io.lightstudios.core.player.TitleSender;
import io.lightstudios.core.util.ColorTranslation;
import io.lightstudios.core.util.ConsolePrinter;
import io.lightstudios.core.util.LightTimers;
import io.lightstudios.core.util.files.FileManager;
import io.lightstudios.core.util.files.MultiFileManager;
import io.lightstudios.core.util.files.configs.CoreMessage;
import io.lightstudios.core.util.files.configs.CoreSettings;
import io.lightstudios.core.util.interfaces.LightCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class LightCore extends JavaPlugin {

    public static LightCore instance;
    public boolean lightCoreEnabled;
    public boolean isVelocity;
    public boolean isRedis;

    private LightTimers lightTimers;
    private ConsolePrinter consolePrinter;
    private ColorTranslation colorTranslation;
    private MessageSender messageSender;
    private TitleSender titleSender;
    private PlayerPunishment playerPunishment;
    private VaultManager vaultManager;
    private RedisManager redisManager;
    private HookManager hookManager;

    private FileManager coreFile;
    private FileManager messageFile;

    private CoreSettings settings;
    private CoreMessage messages;

    private MultiFileManager itemFiles;
    private ItemManager itemManager;

    private final ArrayList<LightCommand> commands = new ArrayList<>();
    private final HashMap<UUID, Location> teleportRequests = new HashMap<>();
    public static final String IDENTIFIER = "lightstudio:lightcore";

    private SQLDatabase sqlDatabase;
    public HikariDataSource hikariDataSource;

    @Override
    public void onLoad() {
        instance = this;
        printLogo();

        // Initialize LightTimers instance
        this.lightTimers = new LightTimers(this);
        this.consolePrinter = new ConsolePrinter("§7[§rLight§eCore§7] §r");

        this.hookManager = new HookManager();

        this.colorTranslation = new ColorTranslation();
        this.messageSender = new MessageSender();
        this.titleSender = new TitleSender();
        this.playerPunishment = new PlayerPunishment();

        this.consolePrinter.printInfo("Generate core files ...");
        // Generate core files
        generateCoreFiles();
        this.consolePrinter.printInfo("Initializing LightCore ...");
        this.consolePrinter.printInfo("Starting database connection ...");
        // Initialize and connect to the database
        initDatabase();
        // Initialize Redis connection and check if it is enabled
        enableRedisConnection();
        // proxy messaging register
        registerOutcoming();
        registerIncomings();


    }

    @Override
    public void onEnable() {
        this.consolePrinter.printInfo("Successfully initialized LightCore. Ready for third party plugins.");
        checkNBTAPI();
        registerCommands();
        registerEvents();

        this.vaultManager = new VaultManager();

        // on success loading the core module
        this.lightCoreEnabled = true;

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
        this.settings = new CoreSettings(coreFile.getConfig());

        String selectedLanguage = switch (settings.language()) {
            case "de" -> "de";
            case "pl" -> "pl";
            default -> "en";
        };

        this.messageFile = new FileManager(this, "language/" + selectedLanguage + ".yml", true);
        this.messages = new CoreMessage(messageFile.getConfig());

        try {
            this.itemFiles = new MultiFileManager("plugins/" + getName() + "/items/");
            this.itemManager = new ItemManager(itemFiles);
        }catch (Exception e) {
            throw new RuntimeException("Error reading item files.", e);
        }
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
                case "mariadb":
                    this.sqlDatabase = new MariaDatabase(this, credentials, connectionProperties);
                    this.consolePrinter.printInfo("Using MariaDB (remote) database.");
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

    /**
     * Register outcome message for the proxy (Velocity)
     */
    private void registerOutcoming() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(
                this, IDENTIFIER);
    }

    /**
     * Register incoming messages from the proxy (Velocity)
     */
    private void registerIncomings() {
        // Register the LightMessageListener -> requests from the proxy
        this.getServer().getMessenger().registerIncomingPluginChannel(
                this, IDENTIFIER, new ReceiveProxyRequest());
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

        String protocolLibVersion = "$&cnot found";
        String placeholderAPIVersion = "&cnot found";
        String townyVersion = "&cnot found";
        String lightCoinsVersion = "&cnot found";

        Plugin placeholderAPI = Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        Plugin protocolLib = Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib");
        Plugin towny = Bukkit.getServer().getPluginManager().getPlugin("Towny");
        Plugin lightCoins = Bukkit.getServer().getPluginManager().getPlugin("LightCoins");

        if(placeholderAPI != null) {
            placeholderAPIVersion = placeholderAPI.getDescription().getVersion();
        }
        if(protocolLib != null) {
            protocolLibVersion = protocolLib.getDescription().getVersion();
        }
        if(towny != null) {
            townyVersion = towny.getDescription().getVersion();
        }
        if(lightCoins != null) {
            lightCoinsVersion = lightCoins.getDescription().getVersion();
        }

        Bukkit.getConsoleSender().sendMessage("          LightCore: §ev0.2.9");
        Bukkit.getConsoleSender().sendMessage("          Server: §e" + Bukkit.getServer().getVersion());
        Bukkit.getConsoleSender().sendMessage("          API-Version: §e" + getDescription().getAPIVersion());
        Bukkit.getConsoleSender().sendMessage("          Soft-Dependency Versions: §e");
        Bukkit.getConsoleSender().sendMessage("           - PlaceholderAPI: §e" + placeholderAPIVersion);
        Bukkit.getConsoleSender().sendMessage("           - ProtocolLib: §e" + protocolLibVersion);
        Bukkit.getConsoleSender().sendMessage("           - LightCoins: §e" + lightCoinsVersion);
        Bukkit.getConsoleSender().sendMessage("           - Towny: §e" + townyVersion);
        Bukkit.getConsoleSender().sendMessage("          Java: §e" + System.getProperty("java.version"));
        Bukkit.getConsoleSender().sendMessage("          Authors: §e" + getDescription().getAuthors() + "\n");
        Bukkit.getConsoleSender().sendMessage("          If you need help, please visit our §eDiscord §7server.");
        Bukkit.getConsoleSender().sendMessage("          Discord: §ehttps://discord.gg/t9vS3hgWf8");
        Bukkit.getConsoleSender().sendMessage("          Thank you for using §eLightCore §7:)\n");

    }

    private void registerEvents() {
        // Register Events
        LightCore.instance.getConsolePrinter().printInfo("Registering Core Events ...");
        // getServer().getPluginManager().registerEvents(new UpdateCustomItem(), this);
        getServer().getPluginManager().registerEvents(new ProxyTeleportEvent(), this);
        new PlaceholderRegistrar("lightcore", "lightStudios", "1.0", true, new ArrayList<>()).register();
    }

    public void registerCommands() {
        new CommandManager(new ArrayList<>(List.of(
                new CoreReloadCommand(),
                new HologramCommand()
        )), "core");
    }

    public void reloadCore() {
        generateCoreFiles();
        this.consolePrinter.printInfo("Reloaded the core files.");
    }

    private void enableRedisConnection() {
        getConsolePrinter().printInfo("Check for Redis connection ...");
        if(settings.redisEnabled()) {
            getConsolePrinter().printInfo("Redis is enabled in config. Connecting to Redis ...");
            this.redisManager = new RedisManager(
                    settings.redisHost(),
                    settings.redisPort(),
                    settings.redisPassword()
            );
            return;
        }
        getConsolePrinter().printInfo(List.of(
                "Redis is not enabled in config.",
                "If you want to use Redis for some server synchronisation,",
                "please enable it in the config file."));
    }
}