package io.lightstudios.core.redis.messaging;

import io.lightstudios.core.LightCore;
import io.lightstudios.core.util.interfaces.LightRedisSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;


public class RedisSubscriber {

    private final ArrayList<LightRedisSub> lightRedisListeners;
    private final String channelName;

    public RedisSubscriber(ArrayList<LightRedisSub> lightRedisListeners, String channelName) {
        this.lightRedisListeners = lightRedisListeners;
        this.channelName = channelName;
        receiveData();
    }

    private void receiveData() {
        try(Jedis jedis = LightCore.instance.getRedisManager().getJedisPool().getResource()) {
            jedis.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    for(LightRedisSub lightRedisSub : lightRedisListeners) {
                        lightRedisSub.receiveData(channel, message);
                    }
                }
            }, this.channelName);
        }
    }
}