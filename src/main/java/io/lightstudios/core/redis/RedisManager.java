package io.lightstudios.core.redis;

import io.lightstudios.core.LightCore;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

@Getter
public class RedisManager {

    private JedisPool jedisPool = null;

    /**
     * Create a new RedisManager instance with redis credentials
     * from the core config.
     * @param host the host of the Redis server
     * @param port the port of the Redis server
     * @param password the password of the Redis server
     * @param useSSL if the Redis server uses SSL
     */
    public RedisManager(String host, int port, String password, boolean useSSL) {
        try {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            if (password == null || password.isEmpty()) {
                this.jedisPool = new JedisPool(poolConfig, host, port, 0, null, useSSL);
            } else {
                this.jedisPool = new JedisPool(poolConfig, host, port, 0, password, useSSL);
            }
        } catch (Exception e) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Could not connect Redis server on " + host + ":" + port + ". ",
                    "Please check your credentials in the core.yml from LightCore.",
                    "Error: " + e.getMessage()
            ));
            return;
        }

        // test the connection to the Redis server
        if (testConnection()) {
            LightCore.instance.getConsolePrinter().printInfo("Connection to Redis server on " + host + ":" + port + " was successful!");
        } else {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Could not connect Redis server on " + host + ":" + port + ". ",
                    "Please check your credentials in the core.yml from LightCore.",
                    "Error: &4Test connection failed!"
            ));
        }
    }

    /**
     * Test the connection to the Redis server
     * @return true if the connection was successful
     */
    private boolean testConnection() {
        try (Jedis jedis = getJedisPool().getResource()) {
            String response = jedis.ping();
            return "PONG".equals(response);
        } catch (Exception e) {
            LightCore.instance.getConsolePrinter().printError(List.of(
                    "Could not receive a PONG result from your Redis server!",
                    "Please check your credentials in the core.yml from LightCore",
                    "or check if your Redis server is running correctly.",
                    "Error: " + e.getMessage()
            ));
            return false;
        }
    }

    /**
     * Close the JedisPooled instance
     */
    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
            LightCore.instance.getConsolePrinter().printInfo("Redis connection closed!");
        }
    }
}