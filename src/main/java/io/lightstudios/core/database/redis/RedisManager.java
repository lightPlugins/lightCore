package io.lightstudios.core.database.redis;

import io.lightstudios.core.LightCore;
import lombok.Getter;
import redis.clients.jedis.JedisPooled;

import java.net.URI;
import java.net.URISyntaxException;

@Getter
public class RedisManager {

    private final JedisPooled jedisPooled;

    /**
     * Create a new RedisManager instance with redis credentials
     * from the core config.
     * @param host the host of the Redis server
     * @param port the port of the Redis server
     * @param password the password of the Redis server
     */
    public RedisManager(String host, int port, String password) {
        try {
            URI redisURI = new URI("redis://" + password + "@" + host + ":" + port);
            this.jedisPooled = new JedisPooled(redisURI);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid Redis URI", e);
        }

        // test the connection to the Redis server
        if(testConnection()) {
            LightCore.instance.getConsolePrinter().printInfo("Connected successfully to the provided Redis server.");
        } else {
            throw new RuntimeException("Could not connect to the provided Redis server with params: "
                    + host + ":" + port + " and password: " + password);
        }
    }

    /**
     * Test the connection to the Redis server
     * @return true if the connection was successful
     */
    private boolean testConnection() {
        try {
            // test the connection with a ping
            return "PONG".equals(jedisPooled.ping());
        } catch (Exception e) {
            throw new RuntimeException("Could not connect to the provided Redis server", e);
        }
    }

    public boolean set(String key, String value) {
        try {
            jedisPooled.set(key, value);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Could not set the value for key: " + key, e);
        }
    }

    public String get(String key) {
        try {
            return jedisPooled.get(key);
        } catch (Exception e) {
            throw new RuntimeException("Could not get the value for key: " + key, e);
        }
    }
}