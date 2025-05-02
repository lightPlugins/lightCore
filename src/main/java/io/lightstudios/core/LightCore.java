package io.lightstudios.core;

import com.zaxxer.hikari.HikariDataSource;
import io.lightstudios.core.commands.CoreReloadCommand;
import io.lightstudios.core.commands.events.OnJoinCommandDelay;
import io.lightstudios.core.commands.manager.CommandManager;
import io.lightstudios.core.database.SQLDatabase;
import io.lightstudios.core.database.impl.MariaDatabase;
import io.lightstudios.core.database.impl.MySQLDatabase;
import io.lightstudios.core.database.impl.SQLiteDatabase;
import io.lightstudios.core.database.model.ConnectionProperties;
import io.lightstudios.core.database.model.DatabaseCredentials;
import io.lightstudios.core.economy.EconomyManager;
import io.lightstudios.core.github.VersionChecker;
import io.lightstudios.core.inventory.events.MenuEvent;
import io.lightstudios.core.inventory.model.InventoryData;
import io.lightstudios.core.placeholder.PlaceholderRegistrar;
import io.lightstudios.core.player.PlayerPunishment;
import io.lightstudios.core.player.title.listener.TitleEventListener;
import io.lightstudios.core.proxy.messaging.backend.receiver.ReceiveProxyRequest;
import io.lightstudios.core.redis.RedisManager;
import io.lightstudios.core.events.ProxyTeleportEvent;
import io.lightstudios.core.hooks.HookManager;
import io.lightstudios.core.player.MessageSender;
import io.lightstudios.core.player.title.TitleSender;
import io.lightstudios.core.util.ColorTranslation;
import io.lightstudios.core.util.ConsolePrinter;
import io.lightstudios.core.util.LightTimers;
import io.lightstudios.core.util.TextFormating;
import io.lightstudios.core.util.files.FileManager;
import io.lightstudios.core.util.files.MultiFileManager;
import io.lightstudios.core.util.files.configs.CoreMessage;
import io.lightstudios.core.util.files.configs.CoreSettings;
import io.lightstudios.core.util.interfaces.LightCommand;
import io.lightstudios.core.world.WorldManager;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

@Getter
public class LightCore extends JavaPlugin {

    public static LightCore instance;
    public boolean lightCoreEnabled;
    public boolean isVelocity;
    public boolean isRedis;

    private LightTimers lightTimers;
    private ConsolePrinter consolePrinter;
    private ColorTranslation colorTranslation;
    private TextFormating textFormating;
    private MessageSender messageSender;
    private TitleSender titleSender;
    private PlayerPunishment playerPunishment;
    private RedisManager redisManager;
    private HookManager hookManager;
    private EconomyManager economyManager;
    private WorldManager worldManager;
    private VersionChecker versionChecker;

    private FileManager coreFile;
    private FileManager messageFile;

    private CoreSettings settings;
    private CoreMessage messages;

    private MultiFileManager itemFiles;
    private MultiFileManager inventoryFiles;

    private final ArrayList<LightCommand> commands = new ArrayList<>();
    private final Map<String, InventoryData> lightInventories = new HashMap<>();
    private final HashMap<UUID, Location> teleportRequests = new HashMap<>();
    public static final String IDENTIFIER = "lightstudio:lightcore";

    private SQLDatabase sqlDatabase;
    public HikariDataSource hikariDataSource;

    @Override
    public void onLoad() {
        instance = this;
        this.lightTimers = new LightTimers(this);
        this.consolePrinter = new ConsolePrinter("§7[§rLight§eCore§7] §r");
        printLogo();

        List<String> compatibleServer = List.of("Paper", "Pufferfish", "Purpur");

        if(!compatibleServer.contains(Bukkit.getName())) {
            getConsolePrinter().printError(List.of(
                    "Light series plugins only support PaperMC and forks.",
                    "Please use PaperMC for better performance and support.",
                    "I have decided to not support SpigotMC anymore.",
                    "Now days SpigotMC is not the best option anymore.",
                    "If you would update to PaperMC, you can download it here: https://papermc.io/downloads/paper"
            ));
            throw new IllegalArgumentException("Server engine " + Bukkit.getName() + " is not supported.");
        }

        // Initialize LightTimers instance
        this.hookManager = new HookManager();

        this.colorTranslation = new ColorTranslation();
        this.textFormating = new TextFormating();
        this.messageSender = new MessageSender();
        this.titleSender = new TitleSender();
        this.playerPunishment = new PlayerPunishment();
        this.worldManager = new WorldManager();

        this.consolePrinter.printInfo("Generate core files ...");
        // Generate core files
        generateCoreFiles();
        this.consolePrinter.printInfo("Initializing LightCore ...");
        this.consolePrinter.printInfo("Starting database connection ...");
        // Initialize and connect to the database
        initDatabase();
        // Initialize Redis connection and check if it is enabled
        enableRedisConnection();
        this.consolePrinter.printInfo("Reading core items ...");
        // Read core items and add them to the cache
        // readCoreItems();
        // loadInventories();

    }

    @Override
    public void onEnable() {

        registerCommands();
        registerEvents();

        // proxy messaging register
        registerOutcoming();
        registerIncomings();

        // the economy manager is only enabled if a vault economy plugin is found
        // automatically checks for LightCoins as economy plugin or use the default vault economy as provider
        // used in all of my plugins
        this.economyManager = new EconomyManager();
        // GitHub version checker for all my “Light” plugin series

        if(this.settings.checkForUpdates()) {
            this.versionChecker = new VersionChecker();
        }


        // on success loading the core module
        this.lightCoreEnabled = true;

        this.consolePrinter.printInfo("Starting new bStats metrics instance ...");
        // bStats metrics for LightCoins -> ID: 24559
        new Metrics(this, 24559);

        this.consolePrinter.printInfo("Successfully initialized LightCore. Ready for third party plugins.");

    }

    @Override
    public void onDisable() {
        this.consolePrinter.printInfo("Stopping LightCore ...");
        try {
            if(this.sqlDatabase != null) {
                this.consolePrinter.printInfo("Stopping database connection ...");
                this.sqlDatabase.getConnection().close();
                this.consolePrinter.printInfo("Successfully closed database connection.");
            }
        } catch (SQLException e) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Could not close database connection.",
                    "Error: " + e.getMessage()
            ));
        }

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
            this.inventoryFiles = new MultiFileManager("plugins/" + getName() + "/inventories/");
        } catch (Exception e) {
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
                onDisable();
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

        String placeholderAPIVersion = "§cnot found";
        String townyVersion = "§cnot found";
        String fancyHologramsVersion = "§cnot found";
        String lightCoinsVersion = "§cnot found";
        String worldGuardVersion = "§cnot found";

        Plugin placeholderAPI = Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        Plugin towny = Bukkit.getServer().getPluginManager().getPlugin("Towny");
        Plugin fancyHolograms = Bukkit.getServer().getPluginManager().getPlugin("FancyHolograms");
        Plugin lightCoins = Bukkit.getServer().getPluginManager().getPlugin("LightCoins");
        Plugin worldGuard = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

        if(placeholderAPI != null) {
            placeholderAPIVersion = placeholderAPI.getDescription().getVersion();
        }
        if(towny != null) {
            townyVersion = towny.getDescription().getVersion();
        }
        if(fancyHolograms != null) {
            fancyHologramsVersion = fancyHolograms.getDescription().getVersion();
        }
        if(lightCoins != null) {
            lightCoinsVersion = lightCoins.getDescription().getVersion();
        }
        if(worldGuard != null) {
            worldGuardVersion = worldGuard.getDescription().getVersion();
        }

        Bukkit.getConsoleSender().sendMessage("          LightCore: " + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("          Server OS: §e" + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        Bukkit.getConsoleSender().sendMessage("          Engine: §e" + Bukkit.getBukkitVersion() + " " + Bukkit.getName());
        Bukkit.getConsoleSender().sendMessage("          API-Version: §e" + getDescription().getAPIVersion());
        Bukkit.getConsoleSender().sendMessage("          Soft-Dependency Versions: §e");
        Bukkit.getConsoleSender().sendMessage("           - PlaceholderAPI: §e" + placeholderAPIVersion);
        Bukkit.getConsoleSender().sendMessage("           - LightCoins: §e" + lightCoinsVersion);
        Bukkit.getConsoleSender().sendMessage("           - FancyHolograms: §e" + fancyHologramsVersion);
        Bukkit.getConsoleSender().sendMessage("           - Towny: §e" + townyVersion);
        Bukkit.getConsoleSender().sendMessage("           - WorldGuard: §e" + worldGuardVersion);
        Bukkit.getConsoleSender().sendMessage("          Java: §e" + System.getProperty("java.version"));
        Bukkit.getConsoleSender().sendMessage("          Authors: §e" + getDescription().getAuthors() + "\n");
        Bukkit.getConsoleSender().sendMessage("          If you need help, please visit our §eDiscord §7server.");
        Bukkit.getConsoleSender().sendMessage("          Discord: §ehttps://discord.gg/t9vS3hgWf8");
        Bukkit.getConsoleSender().sendMessage("          Thank you for using §eLightCore §7:)\n");

    }

    private void registerEvents() {
        // Register Events
        LightCore.instance.getConsolePrinter().printInfo("Registering Core Events ...");
        // teleport player to another server event throw proxy (velocity)
        getServer().getPluginManager().registerEvents(new ProxyTeleportEvent(), this);
        // delay command execution on player join (protection for dupes or other exploits)
        getServer().getPluginManager().registerEvents(new OnJoinCommandDelay(), this);
        // InventoryClickEvent for LightMenus
        getServer().getPluginManager().registerEvents(new MenuEvent(), this);
        // Register title queue system
        getServer().getPluginManager().registerEvents(new TitleEventListener(), this);
        // set nbt data to blocks placed by player
        // getServer().getPluginManager().registerEvents(new BlockPlacedByPlayer(), this);
        new PlaceholderRegistrar("lightcore", "lightStudios", "1.0", true, new ArrayList<>()).register();
    }

    public void registerCommands() {
        new CommandManager(new ArrayList<>(List.of(
                new CoreReloadCommand()
        )), "lightcore");
    }

    public void reloadCore() {
        generateCoreFiles();
        // loadInventories();
        this.consolePrinter.printInfo("Reloaded the core files.");

        if (!this.settings.checkForUpdates()) {
            if (this.versionChecker != null) {
                this.versionChecker.getScheduler().shutdown();
            }
            this.versionChecker = null;
        } else {
            if(this.versionChecker == null) {
                this.consolePrinter.printInfo("Enable version checker after reload.");
                this.versionChecker = new VersionChecker();
            }
        }
    }

    private void enableRedisConnection() {
        getConsolePrinter().printInfo("Check for Redis connection ...");
        if(settings.redisEnabled()) {
            getConsolePrinter().printInfo("Redis is enabled in config. Connecting to Redis ...");
            this.redisManager = new RedisManager(
                    settings.redisHost(),
                    settings.redisPort(),
                    settings.redisPassword(),
                    settings.redisUseSSL()
            );
            this.isRedis = true;
            return;
        }
        getConsolePrinter().printInfo(List.of(
                "Redis is not enabled in config.",
                "If you want to use Redis for some server synchronisation,",
                "please enable it in the config file."));
    }
    
    public void loadInventories() {

        getConsolePrinter().printInfo("Loading inventories ...");
        this.lightInventories.clear();

        // create InventoryData from inventoryfiles
        List<File> files = inventoryFiles.getYamlFiles();
        if (files.isEmpty()) {
            getConsolePrinter().printWarning(List.of(
                    "No inventory files found in the inventories folder.",
                    "Skipping this part ..."));
            return;
        }

        files.forEach(file -> {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                String id = file.getName().replace(".yml", "");
                InventoryData inventoryData = new InventoryData(config);
                this.lightInventories.put(id, inventoryData);
                getConsolePrinter().printInfo("Loaded inventory: §e" + id);
            } catch (Exception e) {
                e.printStackTrace();
                getConsolePrinter().printError(List.of(
                        "Failed to load inventory from file: " + file.getName(),
                        "Error: " + e.getMessage()));
            }
        });

        getConsolePrinter().printInfo("Successfully loaded " + this.lightInventories.size() + " inventories.");
    }
}