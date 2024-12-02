package io.lightstudios.core.database.redis;

import io.lightstudios.core.LightCore;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

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

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        this.jedisPool = new JedisPool(jedisPoolConfig, host, port, 2000, password);

        // test the connection to the Redis server
        if(testConnection()) {
            LightCore.instance.getConsolePrinter().printInfo("Connected successfully to the provided Redis server.");
        } else {
            throw new RuntimeException("Could not connect to the provided Redis server with params: "
                    + host + ":" + port + " and password: " + password);
        }
    }

    /**
     * Get a connection to the Redis server
     * @return a Jedis connection from the pool
     */
    public Jedis getConnection() {
        return jedisPool.getResource();
    }


    /**
     * Test the connection to the Redis server
     * @return true if the connection was successful
     */
    private boolean testConnection() {
        try (Jedis jedis = getConnection()) {
            // test the connection with a ping
            return "PONG".equals(jedis.ping());
        } catch (Exception e) {
            throw new RuntimeException("Could not connect to the provided Redis server", e);
        }
    }
}
