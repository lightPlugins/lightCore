package io.lightstudios.core.util.interfaces;

public interface LightRedisSub {

    void onMessage(String channel, String message);

}
