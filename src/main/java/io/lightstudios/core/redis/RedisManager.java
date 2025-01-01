package io.lightstudios.core.redis;

import io.lightstudios.core.LightCore;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPooled;

import java.net.URI;
import java.net.URISyntaxException;

@Getter
public class RedisManager {

    private final JedisPool jedisPool;

    /**
     * Create a new RedisManager instance with redis credentials
     * from the core config.
     * @param host the host of the Redis server
     * @param port the port of the Redis server
     * @param password the password of the Redis server
     */
    public RedisManager(String host, int port, String password) {
        try {
            this.jedisPool = new JedisPool("localhost", 6379);
        } catch (Exception e) {
            throw new RuntimeException("Invalid Redis URI", e);
        }

        // test the connection to the Redis server
        if (testConnection()) {
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
        try (Jedis jedis = getJedisPool().getResource()) {
            String response = jedis.ping();
            return "PONG".equals(response);
        } catch (Exception e) {
            throw new RuntimeException("Could not connect to the provided Redis server", e);
        }
    }

    /**
     * Close the JedisPooled instance
     */
    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}