package io.lightstudios.core.database.model;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;


public record DatabaseCredentials(String host, String databaseName, String userName, String password, int port) {

    public static DatabaseCredentials fromConfig(FileConfiguration config) {

        String rootPath = "storage.";

        String host = config.getString(rootPath + "host");
        String dbName = config.getString(rootPath + "database");
        String userName = config.getString(rootPath + "username");
        String password = config.getString(rootPath + "password");
        int port = config.getInt(rootPath + "port");

        Validate.notNull(host);
        Validate.notNull(dbName);
        Validate.notNull(userName);
        Validate.notNull(password);

        return new DatabaseCredentials(host, dbName, userName, password, port);
    }

}