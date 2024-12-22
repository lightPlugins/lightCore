package io.lightstudios.core.database;

import com.zaxxer.hikari.HikariDataSource;
import io.lightstudios.core.LightCore;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class PooledDatabase extends SQLDatabase {

    protected static final AtomicInteger POOL_COUNTER = new AtomicInteger(0);
    protected HikariDataSource hikari;

    public PooledDatabase(LightCore plugin) {
        super(plugin);
    }

    @Override
    public void close() {
        LightCore.instance.getConsolePrinter().printError("Attempting to close HikariCP connection pool...");
        if (this.hikari != null) {
            this.hikari.close();
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return this.hikari.getConnection();
        } catch (SQLException e) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Failed to get connection from HikariCP",
                    "Error: " + e.getMessage()
            ));
            e.printStackTrace();
            return null;
        }
    }
}
