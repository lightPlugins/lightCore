package io.lightstudios.core.proxy.util;

public enum SubChannels {

    TELEPORT_REQUEST("lightcore:proxyteleport"),
    KICK_REQUEST("lightcore:proxykick"),
    MESSAGE_REQUEST("lightcore:proxymessage"),
    CHECK_PROXY_REQUEST("lightcore:checkproxy"),
    BALANCE_UPDATE_REQUEST("lightcore:balanceupdate"),
    ;

    private final String type;
    SubChannels(String type) { this.type = type; }
    public String getId() {

        return type;
    }

}
