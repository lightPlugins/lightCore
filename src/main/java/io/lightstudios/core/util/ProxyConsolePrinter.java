package io.lightstudios.core.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;

/*
 *  ########## VELOCITY PROXY PLUGIN ##########
 *  WARNING: Do not use stuff from Bukkit here!
 */

public class ProxyConsolePrinter {

    public void sendInfo(String message) {
        String prefix = "[light<#ffdc73>Core<reset>] ";
        Component formattedMessage = MiniMessage.miniMessage().deserialize(prefix + message);
        String ansiMessage = ANSIComponentSerializer.ansi().serialize(formattedMessage);
        System.out.println(ansiMessage);
    }

    public void sendWarning(String message) {
        String prefix = "[light<#ffdc73>Core<reset>] [<yellow>WARNING<reset>] ";
        Component formattedMessage = MiniMessage.miniMessage().deserialize(prefix + message);
        String ansiMessage = ANSIComponentSerializer.ansi().serialize(formattedMessage);
        System.out.println(ansiMessage);
    }

    public void sendError(String message) {
        String prefix = "[light<#ffdc73>Core<reset>] [<red>ERROR<reset>] ";
        Component formattedMessage = MiniMessage.miniMessage().deserialize(prefix + message);
        String ansiMessage = ANSIComponentSerializer.ansi().serialize(formattedMessage);
        System.out.println(ansiMessage);
    }

    public void printSending(String message) {
        String prefix = "[light<yellow>Core<reset>] <gray>[<white>SEND<gray>] <reset>";
        Component formattedMessage = MiniMessage.miniMessage().deserialize(prefix + message);
        String ansiMessage = ANSIComponentSerializer.ansi().serialize(formattedMessage);
        System.out.println(ansiMessage);
    }

    public void printReceiving(String message) {
        String prefix = "[light<#ffdc73>Core<reset>] <gray>[<white>RECEIVE<gray>] <reset>";
        Component formattedMessage = MiniMessage.miniMessage().deserialize(prefix + message);
        String ansiMessage = ANSIComponentSerializer.ansi().serialize(formattedMessage);
        System.out.println(ansiMessage);
    }

}
