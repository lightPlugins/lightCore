package io.lightstudios.core.proxy.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import io.lightstudios.core.LightCoreProxy;
import io.lightstudios.core.proxy.util.SubChannels;
import net.kyori.adventure.text.Component;

import java.math.BigDecimal;
import java.util.UUID;

import static io.lightstudios.core.LightCoreProxy.IDENTIFIER;

public class ReceiveBackendRequest {

    @Subscribe
    public void balanceLiveUpdate(PluginMessageEvent event) {

        if(!IDENTIFIER.equals(event.getIdentifier())) {
            return;
        }

        if(!(event.getSource() instanceof ServerConnection)) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
        String subChannel = input.readUTF();

        if(!subChannel.equalsIgnoreCase(SubChannels.BALANCE_UPDATE_REQUEST.getId())) {
            return;
        }

        String uuid = input.readUTF();
        double newBalance = input.readDouble();

        Player target = LightCoreProxy.instance.getServer().getAllPlayers().stream().filter(p
                -> p.getUniqueId().toString().equals(uuid)).findFirst().orElse(null);

        if(target == null) {
            return;
        }

        ServerConnection connection = target.getCurrentServer().orElse(null);

        if(connection == null) {
            return;
        }

        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(SubChannels.BALANCE_UPDATE_REQUEST.getId());
        output.writeUTF(uuid);
        output.writeDouble(newBalance);

        connection.getServer().sendPluginMessage(IDENTIFIER, output.toByteArray());

    }

    @Subscribe
    public void kickPlayerFromProxy(PluginMessageEvent event) {

        if(!IDENTIFIER.equals(event.getIdentifier())) {
            return;
        }

        if(!(event.getSource() instanceof ServerConnection)) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());

        String subChannel = input.readUTF();

        if(!subChannel.equalsIgnoreCase(SubChannels.KICK_REQUEST.getId())) {
            return;
        }

        UUID uuid = UUID.fromString(input.readUTF());
        Component text = Component.text(input.readUTF());

        LightCoreProxy.instance.getServer().getPlayer(uuid).ifPresent(target -> target.disconnect(text));

    }

    @Subscribe
    public void sendMessageToPlayer(PluginMessageEvent event) {

        if(!IDENTIFIER.equals(event.getIdentifier())) {
            return;
        }

        if(!(event.getSource() instanceof ServerConnection)) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());

        String subChannel = input.readUTF();

        if(!subChannel.equalsIgnoreCase(SubChannels.MESSAGE_REQUEST.getId())) {
            return;
        }


        UUID uuid = UUID.fromString(input.readUTF());
        Component text = Component.text(input.readUTF());

        LightCoreProxy.instance.getServer().getPlayer(uuid).ifPresent(target -> target.sendMessage(text));

    }

    @Subscribe
    public void teleportPlayerToLocation(PluginMessageEvent event) {

        if(!IDENTIFIER.equals(event.getIdentifier())) {
            return;
        }

        if(!(event.getSource() instanceof ServerConnection)) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
        String subChannel = input.readUTF();

        if(!subChannel.equalsIgnoreCase(SubChannels.TELEPORT_REQUEST.getId())) {
            return;
        }

        String serverName = input.readUTF();
        String uuid = input.readUTF();
        String world = input.readUTF();
        double x = input.readDouble();
        double y = input.readDouble();
        double z = input.readDouble();
        float yaw = input.readFloat();
        float pitch = input.readFloat();

        Player target = LightCoreProxy.instance.getServer().getAllPlayers().stream().filter(p
                -> p.getUniqueId().toString().equals(uuid)).findFirst().orElse(null);

        if(target == null) {
            LightCoreProxy.instance.getConsolePrinter().printReceiving("Target Player from velocity not found!");
            return;
        }

        LightCoreProxy.instance.getServer().getServer(serverName).ifPresent(server -> {
            LightCoreProxy.instance.getConsolePrinter().sendInfo("Teleporting player to target server ...");
            target.createConnectionRequest(server).connect().thenAccept(result -> {
                LightCoreProxy.instance.getConsolePrinter().sendInfo("Player teleported to target server ... " + serverName);
                // Create a byte array output stream to send the teleportation data
                LightCoreProxy.instance.getConsolePrinter().sendInfo("Creating byte array output stream ...");
                ByteArrayDataOutput output = ByteStreams.newDataOutput();
                output.writeUTF(SubChannels.TELEPORT_REQUEST.getId());
                output.writeUTF(serverName);
                output.writeUTF(uuid);
                output.writeUTF(world);
                output.writeDouble(x);
                output.writeDouble(y);
                output.writeDouble(z);
                output.writeFloat(yaw);
                output.writeFloat(pitch);

                // Send the plugin message to the target server
                server.sendPluginMessage(IDENTIFIER, output.toByteArray());
                LightCoreProxy.instance.getConsolePrinter().sendInfo("Teleport request sent to target server ... " + serverName);
            });
        });
    }
}
