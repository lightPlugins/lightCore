package io.lightstudios.core.proxy.messaging.proxy.receive;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import io.lightstudios.core.LightCoreProxy;
import io.lightstudios.core.proxy.util.SubChannels;

import static io.lightstudios.core.LightCoreProxy.IDENTIFIER;

public class ReceiveBackendTeleportRequest {

    @Subscribe
    public void onPluginMessageFromPlugin(PluginMessageEvent event) {

        LightCoreProxy.instance.getConsolePrinter().sendInfo("Received plugin messaging ...");

        if(!IDENTIFIER.equals(event.getIdentifier())) {
            LightCoreProxy.instance.getConsolePrinter().sendInfo("Identifier does not match!");
            return;
        }

        if(!(event.getSource() instanceof ServerConnection)) {
            LightCoreProxy.instance.getConsolePrinter().sendInfo("Source is not an instance of ServerConnection!");
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
        String subChannel = input.readUTF();

        if(!subChannel.equalsIgnoreCase(SubChannels.TELEPORT_REQUEST.getId())) {
            LightCoreProxy.instance.getConsolePrinter().sendInfo("SubChannel does not match!");
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
