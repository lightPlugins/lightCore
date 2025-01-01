package io.lightstudios.core.proxy.messaging;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.lightstudios.core.LightCore;
import io.lightstudios.core.proxy.util.SubChannels;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageRecipient;

import java.math.BigDecimal;
import java.util.UUID;

public class SendProxyRequest {

    public static void sendBalanceLiveUpdate(Player player, UUID uuid, BigDecimal newBalance) {
        LightCore.instance.getConsolePrinter().printInfo("Sending balance update request to proxy for " + uuid + " with balance " + newBalance);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SubChannels.BALANCE_UPDATE_REQUEST.getId());
        out.writeUTF(uuid.toString());
        out.writeDouble(newBalance.doubleValue());

        player.sendPluginMessage(LightCore.instance, "lightstudio:lightcore", out.toByteArray());
    }

    public static void sendBalanceUpdateToServer(String serverName, UUID uuid, BigDecimal newBalance) {
        LightCore.instance.getConsolePrinter().printInfo("Sending balance update request to server " + serverName + " for " + uuid + " with balance " + newBalance);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SubChannels.BALANCE_UPDATE_REQUEST.getId());
        out.writeUTF(uuid.toString());
        out.writeDouble(newBalance.doubleValue());

        // Send the plugin message to the BungeeCord proxy
        Bukkit.getServer().sendPluginMessage(LightCore.instance, "lightstudio:lightcore", out.toByteArray());
    }


    public static void kickPlayerFromProxy(Player player, String kickMessage) {

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SubChannels.KICK_REQUEST.getId());
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(LightCore.instance.getColorTranslation().adventureTranslator(kickMessage, player));

        player.sendPluginMessage(LightCore.instance, "lightstudio:lightcore", out.toByteArray());
    }

    public static void sendMessageToPlayer(Player player, UUID uuid, String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SubChannels.MESSAGE_REQUEST.getId());
        out.writeUTF(uuid.toString());
        out.writeUTF(LightCore.instance.getColorTranslation().adventureTranslator(message, player));

        player.sendPluginMessage(LightCore.instance, "lightstudio:lightcore", out.toByteArray());
    }

    public static void teleportPlayer(Player sender, String serverName, String world, double x, double y, double z, float yaw, float pitch) {

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SubChannels.TELEPORT_REQUEST.getId());
        out.writeUTF(serverName);
        out.writeUTF(sender.getUniqueId().toString());
        out.writeUTF(world);
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
        out.writeFloat(yaw);
        out.writeFloat(pitch);

        sender.sendPluginMessage(LightCore.instance, "lightstudio:lightcore", out.toByteArray());
    }


}
