package io.lightstudios.core.proxy.util;

public enum SubChannels {

    TELEPORT_REQUEST("lightcore:teleport"),
    KICK_REQUEST("lightcore:proxykick"),
    CHECK_PROXY_REQUEST("lightcore:checkproxy"),
    ;

    private final String type;
    SubChannels(String type) { this.type = type; }
    public String getId() {

        return type;
    }

}
