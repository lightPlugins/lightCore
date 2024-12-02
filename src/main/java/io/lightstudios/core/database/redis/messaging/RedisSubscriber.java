package io.lightstudios.core.database.redis.messaging;

import io.lightstudios.core.util.interfaces.LightRedisSub;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.Arrays;

public class RedisSubscriber {

    private final JedisPooled jedisPooled;
    private final ArrayList<LightRedisSub> subscribers = new ArrayList<>();

    public RedisSubscriber(JedisPooled jedisPooled, LightRedisSub... subscribers) {
        this.jedisPooled = jedisPooled;
        this.subscribers.addAll(Arrays.asList(subscribers));
    }

    public void subscribe(String channel) {
        jedisPooled.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                subscribers.forEach(sub -> sub.onMessage(channel, message));
            }
        }, channel);
    }
}
