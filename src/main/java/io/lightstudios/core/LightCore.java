package io.lightstudios.core;

import com.zaxxer.hikari.HikariDataSource;
import io.lightstudios.core.database.SQLDatabase;
import io.lightstudios.core.database.impl.MySQLDatabase;
import io.lightstudios.core.database.impl.SQLiteDatabase;
import io.lightstudios.core.database.model.ConnectionProperties;
import io.lightstudios.core.database.model.DatabaseCredentials;
import io.lightstudios.core.player.MessageSender;
import io.lightstudios.core.player.TitleSender;
import io.lightstudios.core.util.ColorTranslation;
import io.lightstudios.core.util.ConsolePrinter;
import io.lightstudios.core.util.files.FileManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public class LightCore extends JavaPlugin {

    public static LightCore instance;
    public boolean lightCoreEnabled;
    private ConsolePrinter consolePrinter;
    private ColorTranslation colorTranslation;
    private MessageSender messageSender;
    private TitleSender titleSender;
    private FileManager coreFile;

    private SQLDatabase sqlDatabase;
    public HikariDataSource hikariDataSource;


    @Override
    public void onLoad() {
        // generate the core.yml file
        instance = this;
        this.consolePrinter = new ConsolePrinter("§7[§rLight§eCore§7] §r");
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
            this.getLogger().warning("Could not maintain Database Connection. Disabling third party plugins.");
            this.lightCoreEnabled = false;
            throw new RuntimeException("Could not maintain Database Connection.", e);
        }
    }
}