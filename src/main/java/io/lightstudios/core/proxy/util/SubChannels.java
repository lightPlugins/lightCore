package io.lightstudios.core.proxy.util;

public enum SubChannels {

    TELEPORT_REQUEST("lightcore:teleport"),
    ;

    private final String type;
    SubChannels(String type) { this.type = type; }
    public String getId() {

        return type;
    }

}
