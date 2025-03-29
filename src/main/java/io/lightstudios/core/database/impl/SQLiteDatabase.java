package io.lightstudios.core.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.lightstudios.core.LightCore;
import io.lightstudios.core.database.PooledDatabase;
import io.lightstudios.core.database.model.ConnectionProperties;
import io.lightstudios.core.database.model.DatabaseTypes;

import java.io.File;
import java.io.IOException;

public class SQLiteDatabase extends PooledDatabase {


    private static final String FILE_NAME = "lightCore.db";

    private final String filePath;
    private final ConnectionProperties connectionProperties;

    public SQLiteDatabase(LightCore plugin, ConnectionProperties connectionProperties) {
        super(plugin);
        this.connectionProperties = connectionProperties;
        this.filePath = this.plugin.getDataFolder().getPath() + File.separator + FILE_NAME;
    }

    @Override
    public DatabaseTypes getDatabaseType() {
        return DatabaseTypes.SQLITE;
    }

    @Override
    public void connect() {

        if(this.hikari != null) {
            this.hikari.close();
        }

        this.createDBFile();

        final HikariConfig hikari = new HikariConfig();

        hikari.setPoolName("light-sqlite-" + POOL_COUNTER.getAndIncrement());

        hikari.setDriverClassName("org.sqlite.JDBC");
        hikari.setJdbcUrl("jdbc:sqlite:" + this.filePath);

        hikari.setConnectionTimeout(connectionProperties.getConnectionTimeout());
        hikari.setIdleTimeout(connectionProperties.getIdleTimeout());
        hikari.setKeepaliveTime(connectionProperties.getKeepAliveTime());
        hikari.setMaxLifetime(connectionProperties.getMaxLifetime());
        hikari.setMinimumIdle(connectionProperties.getMinimumIdle());
        hikari.setMaximumPoolSize(1);
        hikari.setLeakDetectionThreshold(connectionProperties.getLeakDetectionThreshold());
        hikari.setConnectionTestQuery(connectionProperties.getTestQuery());

        this.hikari = new HikariDataSource(hikari);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createDBFile() {
        File dbFile = new File(this.filePath);
        try {
            dbFile.createNewFile();
        } catch (IOException e) {
            this.plugin.getLogger().warning(String.format("Unable to create %s", FILE_NAME));
            throw new RuntimeException("Unable to create " + FILE_NAME, e);
        }
    }
}
