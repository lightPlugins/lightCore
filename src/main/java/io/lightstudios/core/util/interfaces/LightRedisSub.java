package io.lightstudios.core.util.interfaces;

public interface LightRedisSub {

    void receiveData(String channel, String message);

}
