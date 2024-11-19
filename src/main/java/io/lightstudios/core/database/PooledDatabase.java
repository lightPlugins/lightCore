package io.lightstudios.core.database;

import com.zaxxer.hikari.HikariDataSource;
import io.lightstudios.core.LightCore;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class PooledDatabase extends SQLDatabase {

    protected static final AtomicInteger POOL_COUNTER = new AtomicInteger(0);
    protected HikariDataSource hikari;

    public PooledDatabase(LightCore plugin) {
        super(plugin);
    }

    @Override
    public void close() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return this.hikari.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("[LightCore] Failed to get connection from HikariCP", e);
        }
    }
}
