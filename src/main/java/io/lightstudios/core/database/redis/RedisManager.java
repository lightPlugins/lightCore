package io.lightstudios.core.database.redis;

import io.lightstudios.core.LightCore;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Getter
public class RedisManager {

    private final JedisPool jedisPool;

    public RedisManager(String host, int port, String password) {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        this.jedisPool = new JedisPool(jedisPoolConfig, host, port, 2000, password);

        if(testConnection()) {
            LightCore.instance.getConsolePrinter().printInfo("Connected successfully to the provided Redis server.");
        }
    }

    public Jedis getConnection() {
        return jedisPool.getResource();
    }


    private boolean testConnection() {
        try (Jedis jedis = getConnection()) {
            return "PONG".equals(jedis.ping());
        } catch (Exception e) {
            throw new RuntimeException("Could not connect to the provided Redis server", e);
        }
    }



}
