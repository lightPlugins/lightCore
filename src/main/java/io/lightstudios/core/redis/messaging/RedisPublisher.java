package io.lightstudios.core.redis.messaging;

import redis.clients.jedis.JedisPooled;

public class RedisPublisher {

    private final JedisPooled jedisPooled;

    public RedisPublisher(JedisPooled jedisPooled) {
        this.jedisPooled = jedisPooled;
    }

    public void publish(String channel, String message) {
        jedisPooled.publish(channel, message);
    }
}
